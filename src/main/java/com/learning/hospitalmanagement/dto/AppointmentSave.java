package com.learning.hospitalmanagement.dto;

import com.learning.hospitalmanagement.model.PatientModel;
import com.learning.hospitalmanagement.model.ProviderModel;
import com.learning.hospitalmanagement.utils.AppointmentStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppointmentSave {
    AppointmentRequest appointmentRequest;
    PatientModel patientModel;
    ProviderModel providerModel;
    Integer status;
}
