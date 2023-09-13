package com.learning.hospitalmanagement.controller;

import com.learning.hospitalmanagement.dto.ConsultationRequest;
import com.learning.hospitalmanagement.dto.ConsultationResponse;
import com.learning.hospitalmanagement.model.ConsultationModel;
import com.learning.hospitalmanagement.service.ConsultationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@Slf4j
public class ConsultationManagement {
    @Autowired
    ConsultationService consultationService;

    /**
     * POST consultation
     */
    @PostMapping("/consultation")
    @ResponseStatus(HttpStatus.CREATED)
    public void postConsultation(@RequestBody ConsultationRequest consultationRequest){
        consultationService.addConsultation(consultationRequest);
    }

    /**
     * GET consultation based on patient
     */
    @GetMapping("/consultation/patient/{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<ConsultationResponse> getConsultation(@RequestParam Integer id){
        return consultationService.getConsultationforPatient(id);
    }
}
