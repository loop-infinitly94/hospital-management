package com.learning.hospitalmanagement.repository;

import com.learning.hospitalmanagement.model.AppointmentModel;
import com.learning.hospitalmanagement.model.ConsultationModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConsultationRepository extends JpaRepository<ConsultationModel, Integer> {
    List<ConsultationModel> findAllByPatientModel_Id(Integer patientId);
}
