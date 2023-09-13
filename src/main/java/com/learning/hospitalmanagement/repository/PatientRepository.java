package com.learning.hospitalmanagement.repository;
import com.learning.hospitalmanagement.model.PatientModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<PatientModel, Integer> {

}

