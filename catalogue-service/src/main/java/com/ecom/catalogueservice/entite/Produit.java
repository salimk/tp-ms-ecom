package com.ecom.catalogueservice.entite;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "produit")
public class Produit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private String nom;
    private String description;
    private String sku;
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal prix = BigDecimal.ZERO;
    private int qteStock;

    public Produit() {
    }

    public Produit(Long id, String nom, String description, String sku,BigDecimal prix,int qteStock) {
        this.id = id;
        this.prix = prix;
        this.sku = sku;
        this.description = description;
        this.nom = nom;
        this.qteStock=qteStock;

    }

    public int getQteStock() {
        return qteStock;
    }

    public void setQteStock(int qteStock) {
        this.qteStock = qteStock;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public BigDecimal getPrix() {
        return prix;
    }

    public void setPrix(BigDecimal prix) {
        this.prix = prix;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
