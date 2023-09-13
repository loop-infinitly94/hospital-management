package com.learning.hospitalmanagement.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.learning.hospitalmanagement.dto.AppointmentRequest;
import com.learning.hospitalmanagement.dto.AppointmentResponse;
import com.learning.hospitalmanagement.model.AppointmentModel;
import com.learning.hospitalmanagement.service.AppointmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@RestController
@CrossOrigin
@Slf4j
public class AppointmentManagement {

    @Autowired
    AppointmentService appointmentService;

    /**
     * POST request
     */
    @PostMapping("/appointment")
    @ResponseStatus(HttpStatus.CREATED)
    public void addAppointment(@RequestBody AppointmentRequest appointmentRequest) throws ParseException, JsonProcessingException {
        appointmentService.addAppointment(appointmentRequest);
    }

    /**
     * GET request
     */
    @GetMapping("/appointment/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AppointmentResponse getAppointment(@RequestParam Integer id){
        return appointmentService.getAppointments(id);
    }

    /**
     * PUT request
     */
    @PutMapping("/appointment/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void putAppointment(@RequestParam Integer id, @RequestBody AppointmentRequest appointmentRequest) throws JsonProcessingException {
        appointmentService.updateAppointment(id, appointmentRequest);
    }

    /**
     * DELETE request
     */
    @DeleteMapping("/appointment/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteAppointment(@RequestParam Integer id) {
        appointmentService.deleteAppointment(id);
    }
}
