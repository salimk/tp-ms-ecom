package com.ecom.commandesservice.repository;

import com.ecom.commandesservice.entite.Commande;
import com.ecom.commandesservice.entite.LigneCommande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LigneCommandeRepository extends JpaRepository<LigneCommande, Long> {
    List<LigneCommande> findLigneCommandeByCommandeId(Long commandeId);
    List<LigneCommande> findLigneCommandeByCommande(Commande commande);
}