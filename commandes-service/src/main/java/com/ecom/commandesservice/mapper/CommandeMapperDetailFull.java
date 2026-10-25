package com.ecom.commandesservice.mapper;

import com.ecom.commandesservice.dto.CommandeDtoFull;
import com.ecom.commandesservice.entite.Commande;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING,
uses={LigneCommandeMapperFull.class})

public interface CommandeMapperDetailFull {
    @AfterMapping
    default void linkListligneCommande(@MappingTarget Commande commande) {
        if (commande.getLignesCommande() == null) {return;}
        commande.getLignesCommande().forEach(ligneCommande -> ligneCommande.setCommande(commande));
    }
    @Mapping(target = "clientName", ignore = true)
    @Mapping(target = "clientEmail", ignore = true)
    CommandeDtoFull toDto(Commande commande);

}