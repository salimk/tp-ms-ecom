package com.ecom.catalogueservice.service;

import com.ecom.catalogueservice.entite.Produit;
import com.ecom.catalogueservice.repository.ProduitRepository;
import com.ecom.catalogueservice.exception.DonneesInvalidesException;
import com.ecom.catalogueservice.exception.StockInsuffisantException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import com.ecom.catalogueservice.exception.RessourceIntrouvableException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ProduitService {
    private final ProduitRepository produitRepository;

    public ProduitService(ProduitRepository produitRepository) {
        this.produitRepository = produitRepository;
    }

    public List<Produit> getAllProduits() {
        return produitRepository.findAll();
    }

    public Produit getProduitById(Long id) {
        return produitRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Identifiant introuvable"));
    }

    public Produit AjouterProduit(Produit produit) {
        return produitRepository.save(produit);
    }

    @Transactional
    public Produit modifierProduit(Long id, Produit nouveauProduit) {
        Produit produit = produitRepository.findById(id).orElseThrow(() -> new RessourceIntrouvableException("Produit introuvable"));
        produit.setNom(nouveauProduit.getNom());
        produit.setDescription(nouveauProduit.getDescription());
        produit.setSku(nouveauProduit.getSku());
        produit.setPrix(nouveauProduit.getPrix());
        produit.setQteStock(nouveauProduit.getQteStock());
        return produitRepository.save(produit);
    }

    @Transactional
    public void supprimerProduit(Long id) {
        produitRepository.deleteById(id);
    }

    @Transactional
    public ResponseEntity reserverStock(Map<Long, Integer> listeProduitIdQuantite) {
        for(Map.Entry<Long, Integer> entry : listeProduitIdQuantite.entrySet()) {
            Long id=entry.getKey();
            int quantite=entry.getValue();
            if (quantite <= 0) {
                throw new DonneesInvalidesException("La quantité doit être positive");
            }
            Produit produit = produitRepository.findById(id).orElseThrow(() -> new RessourceIntrouvableException("Produit introuvable"));
            if (produit.getQteStock() < quantite) {
                throw new StockInsuffisantException();
            }
            produit.setQteStock(produit.getQteStock() - quantite);
        }
        return ResponseEntity.status(204).build();
    }
}
