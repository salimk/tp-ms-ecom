package com.ecom.catalogueservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Data;


import java.util.Map;

@Data
public class ReservationStockDto {
    private final Map<Long, Integer> listeProduitIdQuantite;

}
