package com.ecom.catalogueservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Positive;

public class ReservationStockDto {
    @Positive
    private final int quantite;

    @JsonCreator
    public ReservationStockDto(@JsonProperty("quantite") int quantite) {
        this.quantite = quantite;
    }

    public int getQuantite() {
        return quantite;
    }
}
