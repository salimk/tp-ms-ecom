package com.ecom.commandesservice.entite;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;


@Data
@AllArgsConstructor @NoArgsConstructor
@Entity
@Table(name = "lignecommande")
public class LigneCommande {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idcommande", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Commande commande;
    @Column(nullable = false)
    private Long produitId;
    private int quantite;
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal prixUnitaire = BigDecimal.ZERO;
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal sousTotal = BigDecimal.ZERO;
}

