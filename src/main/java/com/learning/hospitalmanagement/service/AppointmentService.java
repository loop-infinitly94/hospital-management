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

    @Autowired
    CheckSlots checkSlots;

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
    public ResponseEntity<AppointmentFreeSlots> addAppointment(@NotNull AppointmentRequest appointmentRequest) throws JsonProcessingException, ParseException {
        Integer patientId = appointmentRequest.getPatientid();
        Integer providerId = appointmentRequest.getProviderid();

        PatientModel patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found with ID"));

        ProviderModel provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new EntityNotFoundException("Provider not found with ID"));

        AppointmentSave appointmentSave = checkSlots.validateAndCreateAppointment(patient, providerId, provider, appointmentRequest);
        Integer status = appointmentSave.getStatus();
        Boolean slotAvailable = appointmentSave.getSlotAvailable();
        AppointmentFreeSlots slots = appointmentSave.getSlots();
        if (slotAvailable) {
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

        AppointmentSave appointmentSave = checkSlots.validateAndCreateAppointment(patient, providerId, provider, appointmentRequest);
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
