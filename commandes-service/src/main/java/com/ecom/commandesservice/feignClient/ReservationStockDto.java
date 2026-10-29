package com.ecom.commandesservice.feignClient;

import lombok.Data;

import java.util.Map;

@Data
public class ReservationStockDto {
    private final Map<Long, Integer> listeProduitIdQuantite;

}
