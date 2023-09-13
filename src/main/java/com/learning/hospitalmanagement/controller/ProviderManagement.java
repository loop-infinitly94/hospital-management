package com.learning.hospitalmanagement.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.learning.hospitalmanagement.dto.ProviderRequest;
import com.learning.hospitalmanagement.dto.ProviderResponse;
import com.learning.hospitalmanagement.service.ProviderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin
@Slf4j
public class ProviderManagement {

    @Autowired
    private ProviderService providerService;

    /**
     * POST provider
     */

    @PostMapping("/provider")
    @ResponseStatus(HttpStatus.CREATED)
    public void postProvider(@RequestBody ProviderRequest providerRequest) throws IOException {
        providerService.createProvider(providerRequest);
    }

    /**
     * GET provider
     */
    @GetMapping("/provider/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProviderResponse getProvider(@RequestParam Integer id) throws JsonProcessingException {
        return providerService.getProvider(id);
    }

    /**
     * PUT provider
     */
    @PutMapping("/provider/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateProvider(@RequestParam Integer id, @RequestBody ProviderRequest providerRequest) throws JsonProcessingException {
        providerService.updateProvider(id, providerRequest);
    }

    /**
     * DELETE provider
     */
    @DeleteMapping("/provider/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteProvider(@RequestParam Integer id) {
        providerService.deleteProvider(id);
    }
}
