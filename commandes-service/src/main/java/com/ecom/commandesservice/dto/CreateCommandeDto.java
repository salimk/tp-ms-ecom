package com.ecom.commandesservice.dto;

import lombok.Value;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for {@link com.ecom.commandesservice.entite.Commande}
 */
@Value
public class CreateCommandeDto implements Serializable {
    @NotNull
    @Positive
    Long clientId;
    @NotEmpty
    @Valid
    List<@NotNull LigneCommandeDto1> lignesCommande;

    @JsonCreator
    public CreateCommandeDto(@JsonProperty("clientId") Long clientId,
                             @JsonProperty("lignesCommande") List<LigneCommandeDto1> lignesCommande) {
        this.clientId = clientId;
        this.lignesCommande = lignesCommande;
    }

    /**
     * DTO for {@link com.ecom.commandesservice.entite.LigneCommande}
     */
    @Value
    public static class LigneCommandeDto1 implements Serializable {
        @NotNull
        @Positive
        Long produitId;
        @Positive
        int quantite;

        @JsonCreator
        public LigneCommandeDto1(@JsonProperty("produitId") Long produitId,
                                 @JsonProperty("quantite") int quantite) {
            this.produitId = produitId;
            this.quantite = quantite;
        }
    }
}
