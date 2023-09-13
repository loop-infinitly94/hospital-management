package com.learning.hospitalmanagement.dto;

import com.learning.hospitalmanagement.model.WorkingHours;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProviderResponse {
    @Id
    Integer id;
    String name;
    String specialization;
    String email;
    String phone;
    Integer duration;
    List<WorkingHours> workinghours;
}
