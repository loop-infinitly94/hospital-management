package com.learning.hospitalmanagement.repository;

import com.learning.hospitalmanagement.model.ProviderModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProviderRepository extends JpaRepository<ProviderModel, Integer> {
}
