package com.ecom.commandesservice.mapper;

import com.ecom.commandesservice.dto.CommandeDto;
import com.ecom.commandesservice.dto.CreateCommandeDto;
import com.ecom.commandesservice.entite.Commande;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING,
uses = {LigneCommandeMapper.class})
public interface CommandeMapper {
    Commande toEntity(CommandeDto commandeDto);

    CommandeDto toDto(Commande commande);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Commande partialUpdate(CommandeDto commandeDto, @MappingTarget Commande commande);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "prixTotal", ignore = true)
    @Mapping(target = "statut", ignore = true)
    Commande toEntity(CreateCommandeDto createCommandeDto);

    @AfterMapping
    default void linkLignesCommande(@MappingTarget Commande commande) {
        if (commande.getLignesCommande() == null) {
            return;
        }
        commande.getLignesCommande().forEach(lignesCommande -> lignesCommande.setCommande(commande));
    }
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Commande partialUpdate(CreateCommandeDto createCommandeDto, @MappingTarget Commande commande);
}