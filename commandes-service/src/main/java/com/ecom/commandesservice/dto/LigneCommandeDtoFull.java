package com.ecom.commandesservice.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO for {@link com.ecom.commandesservice.entite.LigneCommande}
 */
@Data
public class LigneCommandeDtoFull implements Serializable {
    Long id;
    Long produitId;
    String produitNom;
    String produitDescription;
    String produitsku;
    int quantite;
    BigDecimal prixUnitaire;
    BigDecimal sousTotal;
}
