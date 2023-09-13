package com.learning.hospitalmanagement.service;

import com.learning.hospitalmanagement.dto.ConsultationRequest;
import com.learning.hospitalmanagement.dto.ConsultationResponse;
import com.learning.hospitalmanagement.model.ConsultationModel;
import com.learning.hospitalmanagement.model.PatientModel;
import com.learning.hospitalmanagement.model.ProviderModel;
import com.learning.hospitalmanagement.repository.ConsultationRepository;
import com.learning.hospitalmanagement.repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ConsultationService {

    @Autowired
    PatientRepository patientRepository;

    @Autowired
    ConsultationRepository consultationRepository;

    public void addConsultation(ConsultationRequest consultationRequest){
        Integer patientId = consultationRequest.getPatientid();

        PatientModel patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found with ID"));

        ConsultationModel consultationModel = ConsultationModel.builder()
                .patientModel(patient)
                .notes(consultationRequest.getNotes())
                .build();
        consultationRepository.save(consultationModel);
    }

    public List<ConsultationResponse> getConsultationforPatient(Integer id){
        List<ConsultationModel> records = consultationRepository.findAllByPatientModel_Id(id);

        if(records.size() == 0){
            throw new RuntimeException("No consultation records");
        }
        List<ConsultationResponse> response = records.stream().map((eachConsultation) ->  ConsultationResponse.builder()
                .id(eachConsultation.getId())
                .patientid(eachConsultation.getPatientModel().getId())
                .notes(eachConsultation.getNotes())
                .build()).toList();
        return response;
    }
}
