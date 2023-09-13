package com.learning.hospitalmanagement.dto;

import lombok.Data;

@Data
public class ConsultationRequest {
    Integer id;
    Integer patientid;
    String notes;
}
