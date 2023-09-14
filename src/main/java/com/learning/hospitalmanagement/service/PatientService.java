package com.learning.hospitalmanagement.service;

import com.learning.hospitalmanagement.dto.PatientRequest;
import com.learning.hospitalmanagement.dto.PatientResponse;
import com.learning.hospitalmanagement.model.PatientModel;
import com.learning.hospitalmanagement.repository.PatientRepository;
import com.learning.hospitalmanagement.utils.GenderTypes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

@Service
@Slf4j
public class PatientService {
    @Autowired
    PatientRepository patientRepository;

    public ResponseEntity postPatient(PatientRequest patientRequest) throws ParseException {
        PatientModel patient = buildPatient(patientRequest);
        patientRepository.save(patient);
        log.info("Patient {} is saved", patient.getId());
        return new ResponseEntity(null, HttpStatus.CREATED);
    }

    private PatientModel buildPatient(PatientRequest patientRequest) throws ParseException {
        String genderType = patientRequest.getGender();
        String dateOfBirth = patientRequest.getDob();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date dob = sdf.parse(dateOfBirth);
        Integer gender;
        if (genderType.equalsIgnoreCase("male")) {
            gender = GenderTypes.MALE.ordinal();
        } else if (genderType.equalsIgnoreCase("female")) {
            gender = GenderTypes.FEMALE.ordinal();
        } else {
            gender = GenderTypes.OTHER.ordinal();
        }
        PatientModel patient = PatientModel.builder()
                .name(patientRequest.getName())
                .gender(gender)
                .dob(dob)
                .email(patientRequest.getEmail())
                .phone(patientRequest.getPhone())
                .medicalHistory(patientRequest.getMedicalHistory())
                .build();
        return patient;
    }

    public PatientResponse getPatientById(Integer id){
        Optional<PatientModel> patientDetail = patientRepository.findById(id);
        if (!patientDetail.isPresent()) {
            throw new RuntimeException("Error fetching id");
        }
        PatientModel patient = patientDetail.get();
        PatientResponse patientResponse = mapToPatientResponse(patient);

        return patientResponse;
    }

    private PatientResponse mapToPatientResponse(PatientModel patient){
        Date dateOfBirth = patient.getDob();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = sdf.format(dateOfBirth);
        Integer genderType = patient.getGender();
        String gender = "other";
        if(genderType.equals(0)){
            gender = "male";
        } else if (genderType.equals(1)) {
            gender = "female";
        }
        return PatientResponse.builder()
                .id(patient.getId())
                .name(patient.getName())
                .phone(patient.getName())
                .dob(formattedDate)
                .email(patient.getEmail())
                .medicalHistory(patient.getMedicalHistory())
                .gender(gender)
                .build();
    }

    public void updatePatient(Integer id, PatientRequest patientRequest) throws ParseException {
        Optional<PatientModel> patient = patientRepository.findById(id);

        if(patient.isPresent()){
            String genderType = patientRequest.getGender();
            String dateOfBirth = patientRequest.getDob();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date dob = sdf.parse(dateOfBirth);
            Integer gender;
            if (genderType.equalsIgnoreCase("male")) {
                gender = GenderTypes.MALE.ordinal();
            } else if (genderType.equalsIgnoreCase("female")) {
                gender = GenderTypes.FEMALE.ordinal();
            } else {
                gender = GenderTypes.OTHER.ordinal();
            }
            PatientModel updatedPatient = patient.get();
            updatedPatient.setName(patientRequest.getName());
            updatedPatient.setEmail(patientRequest.getEmail());
            updatedPatient.setPhone(patientRequest.getPhone());
            updatedPatient.setMedicalHistory(patientRequest.getMedicalHistory());
            updatedPatient.setGender(gender);
            updatedPatient.setDob(dob);
            patientRepository.save(updatedPatient);
        }
    }

    public void deletePatient(@RequestParam Integer id){
        Optional<PatientModel> patient = patientRepository.findById(id);
        if(patient.isPresent()){
            patientRepository.deleteById(id);
        }
    }
}
