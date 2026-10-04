package com.ecom.commandesservice.mapper;

import com.ecom.commandesservice.dto.CommandeDtoDetail;
import com.ecom.commandesservice.entite.Commande;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING,
uses={LigneCommandeMapper.class})

public interface CommandeMapperDetail {
    Commande toEntity(CommandeDtoDetail commandeDtoDetail);

    @AfterMapping
    default void linkListligneCommande(@MappingTarget Commande commande) {
        if (commande.getLignesCommande() == null) {
            return;
        }
        commande.getLignesCommande().forEach(ligneCommande -> ligneCommande.setCommande(commande));
    }

    CommandeDtoDetail toDto(Commande commande);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Commande partialUpdate(CommandeDtoDetail commandeDtoDetail, @MappingTarget Commande commande);
}