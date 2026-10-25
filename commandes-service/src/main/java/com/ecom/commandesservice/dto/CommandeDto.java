package com.ecom.commandesservice.dto;

import com.ecom.commandesservice.enums.StatutCommande;
import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * DTO for {@link com.ecom.commandesservice.entite.Commande}
 */
@Value
public class CommandeDto implements Serializable {
    Long id;
    String idempotencyKey;
    Long clientId;
    Date dateCommande;
    BigDecimal prixTotal;
    StatutCommande statut;
}