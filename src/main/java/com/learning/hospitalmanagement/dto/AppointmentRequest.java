package com.learning.hospitalmanagement.dto;

import com.learning.hospitalmanagement.utils.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentRequest {
    Integer id;
    Integer patientid;
    Integer providerid;
    String date;
    String time;
    AppointmentStatus status;
    String notes;
}
