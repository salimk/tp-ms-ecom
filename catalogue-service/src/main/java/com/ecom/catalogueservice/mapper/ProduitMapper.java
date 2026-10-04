package com.ecom.catalogueservice.mapper;

import com.ecom.catalogueservice.dto.ProduitDto;
import com.ecom.catalogueservice.entite.Produit;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProduitMapper {
    Produit toEntity(ProduitDto produitDto);

    ProduitDto toDto(Produit produit);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Produit partialUpdate(ProduitDto produitDto, @MappingTarget Produit produit);
}