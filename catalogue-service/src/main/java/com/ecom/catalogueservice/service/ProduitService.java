package com.ecom.catalogueservice.service;

import com.ecom.catalogueservice.entite.Produit;
import com.ecom.catalogueservice.repository.ProduitRepository;
import com.ecom.catalogueservice.exception.DonneesInvalidesException;
import com.ecom.catalogueservice.exception.StockInsuffisantException;
import org.springframework.transaction.annotation.Transactional;
import com.ecom.catalogueservice.exception.RessourceIntrouvableException;
import org.springframework.stereotype.Service;

import java.util.List;

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
        Produit produit = getProduitPourModification(id);
        produit.setNom(nouveauProduit.getNom());
        produit.setDescription(nouveauProduit.getDescription());
        produit.setSku(nouveauProduit.getSku());
        produit.setPrix(nouveauProduit.getPrix());
        produit.setQteStock(nouveauProduit.getQteStock());
        return produitRepository.save(produit);
    }

    @Transactional
    public void supprimerProduit(Long id) {
        produitRepository.delete(getProduitPourModification(id));
    }

    @Transactional
    public Produit reserverStock(Long id, int quantite) {
        if (quantite <= 0) {
            throw new DonneesInvalidesException("La quantité doit être positive");
        }
        Produit produit = getProduitPourModification(id);
        if (produit.getQteStock() < quantite) {
            throw new StockInsuffisantException();
        }
        produit.setQteStock(produit.getQteStock() - quantite);
        return produitRepository.save(produit);
    }

    private Produit getProduitPourModification(Long id) {
        return produitRepository.findProduitPourModification(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Produit introuvable"));
    }
}
