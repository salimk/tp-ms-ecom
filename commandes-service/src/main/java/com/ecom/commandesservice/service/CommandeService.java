package com.ecom.commandesservice.service;

import com.ecom.commandesservice.dto.CommandeDto;
import com.ecom.commandesservice.dto.CommandeDtoDetail;
import com.ecom.commandesservice.dto.CommandeDtoFull;
import com.ecom.commandesservice.entite.Commande;
import com.ecom.commandesservice.entite.LigneCommande;
import com.ecom.commandesservice.repository.CommandeRepository;
import com.ecom.commandesservice.exception.RessourceIntrouvableException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommandeService {
    private final CommandeRepository commandeRepository;

    public CommandeService(CommandeRepository commandeRepository) {
        this.commandeRepository = commandeRepository;
    }

    @Transactional
    public void ajouterligneCommande(Commande commande, LigneCommande ligneCommande) {
        ligneCommande.setCommande(commande);
        commande.getLignesCommande().add(ligneCommande);
        commandeRepository.save(commande);
    }

    public List<Commande> listallCommande() {
        return commandeRepository.findAll();
    }

    public Commande getCommandeById(Long id) {
        return commandeRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Commande introuvable"));
    }
    public CommandeDtoFull getCommandeFullById(Long id) {
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Commande introuvable"));
        throw new IllegalArgumentException();
    }

    public Commande createCommande(Commande commande, String idempotencyKey) {
        throw new IllegalArgumentException();
    }
}
