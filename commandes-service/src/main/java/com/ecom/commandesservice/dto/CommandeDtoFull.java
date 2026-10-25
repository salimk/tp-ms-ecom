package com.ecom.commandesservice.dto;

import com.ecom.commandesservice.enums.StatutCommande;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * DTO for {@link com.ecom.commandesservice.entite.Commande}
 */
@Data
public class CommandeDtoFull implements Serializable {
    Long id;
    Long clientId;
    String clientName;
    String clientEmail;
    Date dateCommande;
    List<LigneCommandeDtoFull> lignesCommande;
    BigDecimal prixTotal;
    StatutCommande statut;
}