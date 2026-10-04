package com.ecom.commandesservice.repository;

import com.ecom.commandesservice.entite.Commande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommandeRepository extends JpaRepository<Commande, Long> {
    Commande findCommandeById(Long numCommande);
    Commande findCommandeByIdempotencyKey(String idempotencyKey);
}