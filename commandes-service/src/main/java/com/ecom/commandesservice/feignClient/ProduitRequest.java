package com.ecom.commandesservice.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "catalogue-service")
public interface ProduitRequest {
    @GetMapping("/produits/{id}")
    ProduitDto getProduitById(@PathVariable("id") Long id);

    @PostMapping("/produits/updatestock")
    ResponseEntity<?> updatestock(@RequestBody ReservationStockDto reservation);
}
