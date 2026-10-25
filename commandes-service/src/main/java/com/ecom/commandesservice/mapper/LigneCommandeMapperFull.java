package com.ecom.commandesservice.mapper;

import com.ecom.commandesservice.dto.LigneCommandeDtoFull;
import com.ecom.commandesservice.entite.LigneCommande;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface LigneCommandeMapperFull {
    @Mapping(target = "produitNom", ignore = true)
    @Mapping(target = "produitDescription", ignore = true)
    @Mapping(target = "produitsku", ignore = true)
    LigneCommandeDtoFull toDto(LigneCommande ligneCommande);
}