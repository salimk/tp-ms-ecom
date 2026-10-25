package com.ecom.commandesservice.dto;

import com.ecom.commandesservice.enums.StatutCommande;
import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * DTO for {@link com.ecom.commandesservice.entite.Commande}
 */
@Value
public class CommandeDtoDetail implements Serializable {
    Long id;
    Long clientId;
    Date dateCommande;
    List<LigneCommandeDto> lignesCommande;
    BigDecimal prixTotal;
    StatutCommande statut;
}