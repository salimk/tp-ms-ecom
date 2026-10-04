package com.ecom.catalogueservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.PositiveOrZero;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * DTO for {@link com.ecom.catalogueservice.entite.Produit}
 */
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

    @JsonCreator
    public ProduitDto(@JsonProperty("id") Long id, @JsonProperty("nom") String nom,
                      @JsonProperty("description") String description, @JsonProperty("sku") String sku,
                      @JsonProperty("prix") BigDecimal prix, @JsonProperty("qteStock") Integer qteStock) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.sku = sku;
        this.prix = prix;
        this.qteStock = qteStock;
    }

    public Long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getDescription() {
        return description;
    }

    public String getSku() {
        return sku;
    }

    public BigDecimal getPrix() {
        return prix;
    }

    public Integer getQteStock() {
        return qteStock;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProduitDto entity = (ProduitDto) o;
        return Objects.equals(this.id, entity.id) &&
                Objects.equals(this.nom, entity.nom) &&
                Objects.equals(this.description, entity.description) &&
                Objects.equals(this.sku, entity.sku) &&
                Objects.equals(this.prix, entity.prix) &&
                Objects.equals(this.qteStock, entity.qteStock);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nom, description, sku, prix, qteStock);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "nom = " + nom + ", " +
                "description = " + description + ", " +
                "sku = " + sku + ", " +
                "prix = " + prix + ", " +
                "qteStock = " + qteStock + ")";
    }
}
