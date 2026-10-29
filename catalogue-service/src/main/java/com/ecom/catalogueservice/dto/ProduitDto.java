package com.ecom.catalogueservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@Data @AllArgsConstructor
public class ProduitDto implements Serializable {
    private final Long id;
    @NotBlank
    @Size(max = 255)
    private final String nom;
    @Size(max = 255)
    private final String description;
    @NotBlank
    @Size(max = 255)
    private final String sku;
    @PositiveOrZero
    @NotNull
    @Digits(integer = 17, fraction = 2)
    private final BigDecimal prix;
    @PositiveOrZero
    @NotNull
    private final Integer qteStock;
}
