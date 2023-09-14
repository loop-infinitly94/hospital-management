package com.learning.hospitalmanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

//    @ExceptionHandler(SlotNotAvailable.class)
//    public ResponseEntity<String> handleResourceNotFoundException(SlotNotAvailable ex) {
//        return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                .body(ex.getMessage());
//    }

    // Add handlers for other custom exceptions as needed
}

