package com.ecom.commandesservice.service;

import com.ecom.commandesservice.entite.Commande;
import com.ecom.commandesservice.entite.LigneCommande;
import com.ecom.commandesservice.repository.CommandeRepository;
import com.ecom.commandesservice.exception.RessourceIntrouvableException;
import com.ecom.commandesservice.exception.CreationCommandeNonDisponibleException;
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

    public Commande ajouterCommande(Commande commande) {
        return commandeRepository.save(commande);
    }

    // TODO TP Feign : valider le client, vérifier les produits et calculer les prix.
    public Commande createCommande(Commande commande, String idempotencyKey) {
        throw new CreationCommandeNonDisponibleException();
    }
}
