package com.learning.hospitalmanagement.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.hospitalmanagement.dto.AppointmentFreeSlots;
import com.learning.hospitalmanagement.dto.AppointmentRequest;
import com.learning.hospitalmanagement.dto.AppointmentSave;
import com.learning.hospitalmanagement.dto.WorkingHours;
import com.learning.hospitalmanagement.exception.SlotNotAvailable;
import com.learning.hospitalmanagement.model.AppointmentModel;
import com.learning.hospitalmanagement.model.PatientModel;
import com.learning.hospitalmanagement.model.ProviderModel;
import com.learning.hospitalmanagement.repository.AppointmentRepository;
import com.learning.hospitalmanagement.utils.AppointmentStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CheckSlots {

    @Autowired
    AppointmentRepository appointmentRepository;
    public List<AppointmentModel> getConfirmedAppointments(Integer providerId, String date) {
        return appointmentRepository.findByProviderModel_IdAndDateAndStatus(providerId, date, AppointmentStatus.CONFIRMED.getIndex());
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
                    Boolean isInRange = (appointmentTimeParsed.isBefore(currentAppointmentEndTimeParsed) && appointmentDuration.isAfter(currentAppointmentStartTimeParsed));
                    if (isInRange) {
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
}
