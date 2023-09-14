package com.learning.hospitalmanagement.controller;

import com.learning.hospitalmanagement.dto.PatientRequest;
import com.learning.hospitalmanagement.dto.PatientResponse;
import com.learning.hospitalmanagement.model.PatientModel;
import com.learning.hospitalmanagement.repository.PatientRepository;
import com.learning.hospitalmanagement.service.PatientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@RestController
@CrossOrigin
@Slf4j
public class PatientManagement {

    @Autowired
    PatientRepository patientRepository;

    @Autowired
    PatientService patientService;

    /**
     * POST Patient
     */
    @PostMapping("/patient")
    public ResponseEntity postPatient(@RequestBody PatientRequest patientRequest){
        try{
            return patientService.postPatient(patientRequest);
        }
        catch (Exception e){
            log.error("Something went wrong");
            throw new RuntimeException("Something went wrong");
        }
    }

    /**
     * GET Patient
     */
    @GetMapping("/patient/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PatientResponse getPatient(@RequestParam Integer id){
        return patientService.getPatientById(id);
    }

    /**
     * PUT Patient
     */
    @PutMapping("/patient/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void putPatient(@RequestParam Integer id, @RequestBody PatientRequest patientRequest) throws ParseException {
        patientService.updatePatient(id, patientRequest);
    }

    /**
     * Delete Patient
     */
    @DeleteMapping("/patient/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deletePatient(@RequestParam Integer id) {
        patientService.deletePatient(id);
    }
}
