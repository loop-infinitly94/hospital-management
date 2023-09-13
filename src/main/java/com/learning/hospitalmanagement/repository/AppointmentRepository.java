package com.learning.hospitalmanagement.repository;

import com.learning.hospitalmanagement.model.AppointmentModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<AppointmentModel, Integer> {
    List<AppointmentModel> findByProviderModel_Id(Integer providerModel);

    List<AppointmentModel> findByProviderModel_IdAndDateAndStatus(Integer providerModel, String date, Integer status);
}
