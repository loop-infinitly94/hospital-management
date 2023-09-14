package com.learning.hospitalmanagement.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.hospitalmanagement.dto.*;
import com.learning.hospitalmanagement.exception.SlotNotAvailable;
import com.learning.hospitalmanagement.model.AppointmentModel;
import com.learning.hospitalmanagement.model.PatientModel;
import com.learning.hospitalmanagement.model.ProviderModel;
import com.learning.hospitalmanagement.repository.AppointmentRepository;
import com.learning.hospitalmanagement.repository.PatientRepository;
import com.learning.hospitalmanagement.repository.ProviderRepository;
import com.learning.hospitalmanagement.utils.AppointmentStatus;
import jakarta.persistence.EntityNotFoundException;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AppointmentService {
    @Autowired
    PatientRepository patientRepository;

    @Autowired
    ProviderRepository providerRepository;

    @Autowired
    AppointmentRepository appointmentRepository;

    public List<AppointmentModel> getConfirmedAppointments(Integer providerId, String date) {
        return appointmentRepository.findByProviderModel_IdAndDateAndStatus(providerId, date, AppointmentStatus.CONFIRMED.getIndex());
    }

    public void saveToRepo(AppointmentRequest appointmentRequest, PatientModel patient, ProviderModel provider, Integer status) {
        AppointmentModel appointment = AppointmentModel.builder()
                .patientModel(patient)
                .providerModel(provider)
                .date(appointmentRequest.getDate())
                .status(status)
                .time(appointmentRequest.getTime())
                .notes(appointmentRequest.getNotes())
                .build();
        appointmentRepository.save(appointment);
    }

    public AppointmentFreeSlots calculateFreeSlots(WorkingHours workingHours, AppointmentFreeSlots slots, Integer providerDuration) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String providerEndTime = workingHours.end;
        String providedStartTime = workingHours.start;
        LocalTime providerStart = LocalTime.parse(providedStartTime, formatter);
        LocalTime providerEnd = LocalTime.parse(providerEndTime, formatter);
        AppointmentFreeSlots slotsAvailable = new AppointmentFreeSlots();

        while (providerStart.plusMinutes(providerDuration).isBefore(providerEnd.plusMinutes(providerDuration))) {
            String startTimeStr = providerStart.format(formatter);
            String endTimeStr = providerStart.plusMinutes(providerDuration).format(formatter);
            String timeSlot = startTimeStr + " - " + endTimeStr;
            if (!slots.getTimeSlots().contains(timeSlot)) {
                slotsAvailable.addSlots(timeSlot);
            }
            providerStart = providerStart.plusMinutes(providerDuration);
        }

        return slotsAvailable;
    }

    public AppointmentSave validateAndCreateAppointment(PatientModel patient, Integer providerId, ProviderModel provider, AppointmentRequest appointmentRequest) throws JsonProcessingException {
        String appointmentDate = appointmentRequest.getDate();
        String appointmentTime = appointmentRequest.getTime();
        Integer status = appointmentRequest.getStatus().getIndex();
        ObjectMapper objectMapper = new ObjectMapper();
        Optional<List<AppointmentModel>> appointments = Optional.ofNullable(getConfirmedAppointments(providerId, appointmentDate));
        //get working of provider
        List<WorkingHours> workingHoursList = objectMapper.readValue(provider.getWorkinghours(), new TypeReference<List<WorkingHours>>() {
        });
        //filter for the appointmentdate
        List<WorkingHours> filteredList = workingHoursList.stream()
                .filter(hours -> hours.date.equals(appointmentDate))
                .collect(Collectors.toList());
        if (filteredList.size() == 0) {
            throw new SlotNotAvailable("No working hour found for the date");
        }
        WorkingHours workingHoursForDate = filteredList.get(0);
        String providerEndTime = workingHoursForDate.end;
        Integer providerDuration = provider.getDuration();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        //provider end time
        LocalTime providerEndTimeParsed = LocalTime.parse(providerEndTime, formatter);
        //appointment time
        LocalTime appointmentTimeParsed = LocalTime.parse(appointmentTime, formatter);
        //total duration of appointment
        LocalTime appointmentDuration = appointmentTimeParsed.plusMinutes(providerDuration);

        //check appointTime + providerDuration > endtime
        Boolean isExceedingTimelimit = appointmentDuration.isAfter(providerEndTimeParsed);
        if (isExceedingTimelimit) {
            throw new SlotNotAvailable("time exceeded");
        }

        List<AppointmentModel> appointmentList = appointments.get();
        int length = appointmentList.size();

        AppointmentFreeSlots timeSlots = new AppointmentFreeSlots();

        // if no CONFIRMED appointments found for the day
        if (length == 0) {
            AppointmentSave appointmentSave = new AppointmentSave();
            appointmentSave.setAppointmentRequest(appointmentRequest);
            appointmentSave.setPatientModel(patient);
            appointmentSave.setProviderModel(provider);
            appointmentSave.setStatus(status);
            return appointmentSave;
        } else {
            try {
                Boolean isSlotAvailable = true;
                for (AppointmentModel appointment : appointmentList) {
                    String currentAppointmentStartTime = appointment.getTime();
                    LocalTime currentAppointmentStartTimeParsed = LocalTime.parse(currentAppointmentStartTime, formatter);
                    LocalTime currentAppointmentEndTimeParsed = currentAppointmentStartTimeParsed.plusMinutes(providerDuration);
                    //check if the appointment date is coinciding with any CONFIRMED appointments
                    Boolean isStartTimeInRange = (appointmentTimeParsed.isAfter(currentAppointmentStartTimeParsed) && appointmentTimeParsed.isBefore(currentAppointmentEndTimeParsed));
                    Boolean isEndTimeInRange = (appointmentDuration.isAfter(currentAppointmentStartTimeParsed) && appointmentDuration.isBefore(currentAppointmentEndTimeParsed));
//                    Boolean isInRange = appointmentTimeParsed.plusMinutes(1).isAfter(currentAppointmentEndTimeParsed) && appointmentDuration.isBefore(currentAppointmentStartTimeParsed);

                    if (isStartTimeInRange || isEndTimeInRange) {
                        String timeSlot = currentAppointmentStartTimeParsed.format(formatter) + " - " + currentAppointmentEndTimeParsed.format(formatter);
                        timeSlots.addSlots(timeSlot);
                        isSlotAvailable = false;
                    }
                }
                AppointmentFreeSlots availableslots = new AppointmentFreeSlots();
                if (!isSlotAvailable) {
                    availableslots = calculateFreeSlots(workingHoursForDate, timeSlots, providerDuration);
                }
                AppointmentSave appointmentSave = new AppointmentSave();
                appointmentSave.setAppointmentRequest(appointmentRequest);
                appointmentSave.setPatientModel(patient);
                appointmentSave.setProviderModel(provider);
                appointmentSave.setStatus(status);
                appointmentSave.setSlots(availableslots);
                appointmentSave.setSlotAvailable(isSlotAvailable);
                return appointmentSave;

            } catch (Exception exception) {
                throw new SlotNotAvailable("Slot is booked");
            }
        }
    }

    public ResponseEntity<AppointmentFreeSlots> addAppointment(@NotNull AppointmentRequest appointmentRequest) throws JsonProcessingException, ParseException {
        Integer patientId = appointmentRequest.getPatientid();
        Integer providerId = appointmentRequest.getProviderid();

        PatientModel patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found with ID"));

        ProviderModel provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new EntityNotFoundException("Provider not found with ID"));

        AppointmentSave appointmentSave = validateAndCreateAppointment(patient, providerId, provider, appointmentRequest);
        Integer status = appointmentSave.getStatus();
        Boolean slotAvailable = appointmentSave.getSlotAvailable();
        AppointmentFreeSlots slots = appointmentSave.getSlots();
        if(slotAvailable) {
            saveToRepo(appointmentRequest, patient, provider, status);
            return ResponseEntity.ok(slots);
        }
        return new ResponseEntity<AppointmentFreeSlots>(slots, HttpStatus.FORBIDDEN);
    }

    public AppointmentResponse getAppointments(Integer id) {
        Optional<AppointmentModel> appointment = appointmentRepository.findById(id);
        if (!appointment.isPresent()) {
            throw new RuntimeException("Error fetching id");
        }
        AppointmentModel appointmentById = appointment.get();
        PatientModel patient = appointmentById.getPatientModel();
        ProviderModel provider = appointmentById.getProviderModel();

        Integer status = appointmentById.getStatus();
        return AppointmentResponse.builder()
                .id(appointmentById.getId())
                .status(AppointmentStatus.fromValue(String.valueOf(status)))
                .providerid(provider.getId())
                .patientid(patient.getId())
                .notes(appointmentById.getNotes())
                .date(appointmentById.getDate())
                .time(appointmentById.getTime())
                .build();
    }

    public void updateAppointment(Integer id, AppointmentRequest appointmentRequest) throws JsonProcessingException {
        Optional<AppointmentModel> appointment = appointmentRepository.findById(id);
        if (!appointment.isPresent()) {
            throw new RuntimeException("Error fetching id");
        }
        AppointmentModel appointmentById = appointment.get();

        PatientModel patient = appointmentById.getPatientModel();
        ProviderModel provider = appointmentById.getProviderModel();
        Integer providerId = provider.getId();

        AppointmentSave appointmentSave = validateAndCreateAppointment(patient, providerId, provider, appointmentRequest);
        Integer status = appointmentSave.getStatus();
        appointmentById.setPatientModel(patient);
        appointmentById.setProviderModel(provider);
        appointmentById.setStatus(status);
        appointmentById.setNotes(appointmentRequest.getNotes());
        appointmentById.setTime(appointmentRequest.getTime());
        appointmentById.setDate(appointmentRequest.getDate());
        appointmentRepository.save(appointmentById);
    }

    public void deleteAppointment(Integer id) {
        Optional<AppointmentModel> appointment = appointmentRepository.findById(id);
        if (!appointment.isPresent()) {
            throw new RuntimeException("Error fetching id");
        }
        appointmentRepository.deleteById(id);
    }
}
