package com.ecom.commandesservice.mapper;

import com.ecom.commandesservice.dto.LigneCommandeDto;
import com.ecom.commandesservice.entite.LigneCommande;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface LigneCommandeMapper {
    @Mapping(target = "commande", ignore = true)
    LigneCommande toEntity(LigneCommandeDto ligneCommandeDto);

    LigneCommandeDto toDto(LigneCommande ligneCommande);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "commande", ignore = true)
    LigneCommande partialUpdate(LigneCommandeDto ligneCommandeDto, @MappingTarget LigneCommande ligneCommande);
}