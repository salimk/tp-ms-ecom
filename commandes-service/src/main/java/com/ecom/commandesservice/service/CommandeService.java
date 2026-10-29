package com.ecom.commandesservice.service;

import com.ecom.commandesservice.dto.CommandeDtoFull;
import com.ecom.commandesservice.dto.LigneCommandeDtoFull;
import com.ecom.commandesservice.dto.NotificationEmailDto;
import com.ecom.commandesservice.entite.Commande;
import com.ecom.commandesservice.entite.LigneCommande;
import com.ecom.commandesservice.enums.StatutCommande;
import com.ecom.commandesservice.exception.RessourceIntrouvableException;
import com.ecom.commandesservice.feignClient.*;
import com.ecom.commandesservice.mapper.CommandeMapperDetailFull;
import com.ecom.commandesservice.messaging.NotificationPublisher;
import com.ecom.commandesservice.repository.CommandeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.math.BigDecimal;
import java.util.*;

@Service
public class CommandeService {
    private final CommandeRepository commandeRepository;
    private final ClientsRequest clientsRequest;
    private final ProduitRequest produitRequest;
    private final CommandeMapperDetailFull commandeMapperDetailFull;
    private final NotificationPublisher notificationPublisher;


    public CommandeService(CommandeRepository commandeRepository, ClientsRequest clientsRequest, ProduitRequest produitRequest, CommandeMapperDetailFull commandeMapperDetailFull,NotificationPublisher notificationPublisher) {
        this.commandeRepository = commandeRepository;
        this.clientsRequest = clientsRequest;
        this.produitRequest = produitRequest;
        this.commandeMapperDetailFull = commandeMapperDetailFull;
        this.notificationPublisher = notificationPublisher;
    }

    public List<Commande> listallCommande() {
        return commandeRepository.findAll();
    }

    public Commande getCommandeById(Long id) {
        return commandeRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Commande introuvable"));
    }
    @Transactional(readOnly = true)
    public CommandeDtoFull getCommandeFullById(Long id) {
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Commande introuvable"));
        CommandeDtoFull commandeDtoFull = commandeMapperDetailFull.toDto(commande);

        ClientDto client = clientsRequest.getClientById(commande.getClientId());
        commandeDtoFull.setClientEmail(client.getEmail());
        commandeDtoFull.setClientName(client.getName());
        for (LigneCommandeDtoFull ligne : commandeDtoFull.getLignesCommande()) {
            ProduitDto produit = produitRequest.getProduitById(ligne.getProduitId());
            ligne.setProduitsku(produit.getSku());
            ligne.setProduitNom(produit.getNom());
            ligne.setProduitDescription(produit.getDescription());
        }
        return commandeDtoFull;
    }



    @Transactional
    public Commande createCommande(Commande commande, String idempotencyKey) {
        if (idempotencyKey == null || idempotencyKey.isBlank()
                || idempotencyKey.length() > 255) {
            throw new IllegalArgumentException("Clé d’idempotence invalide");
        }

        Optional<Commande> commandeExistante =commandeRepository.findCommandeByIdempotencyKey(idempotencyKey);

        if (commandeExistante.isPresent()) {
            throw new IllegalArgumentException("Opération déjà traitée");
        }
        commande.setIdempotencyKey(idempotencyKey);

        ClientDto client = clientsRequest.getClientById(commande.getClientId());
        if (!Integer.valueOf(1).equals(client.getEtat())) {
            throw new IllegalArgumentException("Client suspendu");
        }
        Map<Long, Integer> listeProduitIdQuantite= new HashMap<>();
        BigDecimal prixTotal = BigDecimal.ZERO;
        for (LigneCommande ligne : commande.getLignesCommande()) {
            ProduitDto produit = produitRequest.getProduitById(ligne.getProduitId());

            if (produit.getQteStock().longValue() < ligne.getQuantite()) {
                throw new IllegalArgumentException("Stock insuffisant pour le produit "+ ligne.getProduitId());
            }
            BigDecimal prixUnitaire = produit.getPrix();
            BigDecimal sousTotal = prixUnitaire.multiply(BigDecimal.valueOf(ligne.getQuantite()));

            ligne.setPrixUnitaire(prixUnitaire);
            ligne.setSousTotal(sousTotal);
            ligne.setCommande(commande);
            prixTotal = prixTotal.add(sousTotal);
            listeProduitIdQuantite.put(ligne.getProduitId(), ligne.getQuantite());
        }
        commande.setPrixTotal(prixTotal);
        commande.setDateCommande(new Date());
        commande.setStatut(StatutCommande.EN_ATTENTE);
        ReservationStockDto reservation = new ReservationStockDto(listeProduitIdQuantite);
        NotificationEmailDto emailDto = new NotificationEmailDto(
                client.getEmail(),
                "Commande n° " + commande.getId() + " enregistrée",
                "Bonjour " + client.getName()
                        + ", votre commande n° " + commande.getId()
                        + " a été enregistrée. Montant : "
                        + commande.getPrixTotal() + "."
        );


        afterCommit(() -> produitRequest.updatestock(reservation));
        afterCommit(() -> notificationPublisher.envoyer(emailDto));

        return  commandeRepository.save(commande);
    }

    private void afterCommit(Runnable action) {
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        action.run();
                    }
                }
        );
    }
}
