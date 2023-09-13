package com.learning.hospitalmanagement.dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PatientRequest {
    @Id
    private Integer id;
    private String name;
    private String gender;
    private String dob;
    private String email;
    private String phone;
    private String medicalHistory;
}
