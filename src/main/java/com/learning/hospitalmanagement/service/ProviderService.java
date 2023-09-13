package com.learning.hospitalmanagement.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.hospitalmanagement.dto.ProviderRequest;
import com.learning.hospitalmanagement.dto.ProviderResponse;
import com.learning.hospitalmanagement.model.PatientModel;
import com.learning.hospitalmanagement.model.ProviderModel;
import com.learning.hospitalmanagement.model.WorkingHours;
import com.learning.hospitalmanagement.repository.ProviderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ProviderService {
    @Autowired
    private ProviderRepository providerRepository;

    public void createProvider(ProviderRequest providerRequest) throws IOException {

        ProviderModel provider = ProviderModel.builder()
                .name(providerRequest.getName())
                .phone(providerRequest.getPhone())
                .email(providerRequest.getEmail())
                .specialization(providerRequest.getSpecialization())
                .duration(providerRequest.getDuration())
                .workinghours(new ObjectMapper().writeValueAsString(providerRequest.getWorkinghours()))
                .build();
        providerRepository.save(provider);

        log.info("Provider {} saved", provider.getId());
    }

    public ProviderResponse getProvider(Integer id) throws JsonProcessingException {
        Optional<ProviderModel> providerDetail = providerRepository.findById(id);

        if (!providerDetail.isPresent()) {
            throw new RuntimeException("Error fetching id");
        }
        ProviderModel provider = providerDetail.get();
        ProviderResponse providerById = mapToProvider(provider);
        return providerById;
    }

    public ProviderResponse mapToProvider(ProviderModel provider) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<WorkingHours> workingHoursList = objectMapper.readValue(provider.getWorkinghours(), new TypeReference<List<WorkingHours>>() {});
        return ProviderResponse.builder()
                .id(provider.getId())
                .name(provider.getName())
                .email(provider.getEmail())
                .phone(provider.getPhone())
                .specialization(provider.getSpecialization())
                .duration(provider.getDuration())
                .workinghours(workingHoursList)
                .build();
    }

    public void updateProvider(Integer id, ProviderRequest providerRequest) throws JsonProcessingException {
        Optional<ProviderModel> provider = providerRepository.findById(id);
        if(provider.isPresent()){
            ProviderModel updatedProvider = provider.get();
            updatedProvider.setName(providerRequest.getName());
            updatedProvider.setEmail(providerRequest.getEmail());
            updatedProvider.setPhone(providerRequest.getPhone());
            updatedProvider.setSpecialization(providerRequest.getSpecialization());
            updatedProvider.setDuration(providerRequest.getDuration());
            updatedProvider.setWorkinghours(new ObjectMapper().writeValueAsString(providerRequest.getWorkinghours()));
            providerRepository.save(updatedProvider);
            log.info("Provider {} updated", updatedProvider.getId());
        }
    }

    public void deleteProvider(Integer id){
        Optional<ProviderModel> provider = providerRepository.findById(id);
        if(provider.isPresent()){
            providerRepository.deleteById(id);
        }

    }
}
