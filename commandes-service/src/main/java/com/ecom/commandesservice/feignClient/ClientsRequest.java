package com.ecom.commandesservice.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "clients-service")
public interface ClientsRequest {
        @GetMapping("/clients/{id}")
        ClientDto getClientById(@PathVariable("id") Long id);
}

