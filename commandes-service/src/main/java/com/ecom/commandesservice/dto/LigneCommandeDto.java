package com.ecom.commandesservice.dto;

import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO for {@link com.ecom.commandesservice.entite.LigneCommande}
 */
@Value
public class LigneCommandeDto implements Serializable {
    Long id;
    Long produitId;
    int quantite;
    BigDecimal prixUnitaire;
    BigDecimal sousTotal;
}
