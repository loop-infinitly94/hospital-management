package com.learning.hospitalmanagement.dto;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientResponse {
    @Id
    private Integer id;
    private String name;
    private String gender;
    private String dob;
    private String email;
    private String phone;
    private String medicalHistory;
}
