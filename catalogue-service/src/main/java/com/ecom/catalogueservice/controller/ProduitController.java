package com.ecom.catalogueservice.controller;

import com.ecom.catalogueservice.dto.ProduitDto;
import com.ecom.catalogueservice.dto.ReservationStockDto;
import com.ecom.catalogueservice.entite.Produit;
import com.ecom.catalogueservice.mapper.ProduitMapper;
import com.ecom.catalogueservice.service.ProduitService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/produits")
public class ProduitController {
    private final ProduitService produitService;
    private final ProduitMapper produitMapper;

    public ProduitController(ProduitService produitService, ProduitMapper produitMapper) {
        this.produitService = produitService;
        this.produitMapper = produitMapper;
    }

    @GetMapping
    public ResponseEntity<List<ProduitDto>> getAllProduits() {
        return ResponseEntity.ok(produitService.getAllProduits().stream().map(produitMapper::toDto).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProduitDto> getProduitById(@PathVariable Long id) {
        return ResponseEntity.ok(produitMapper.toDto(produitService.getProduitById(id)));
    }

    @PostMapping("/new")
    public ResponseEntity<ProduitDto> ajouterProduit(@Valid @RequestBody ProduitDto produitDto) {
        Produit produit = produitMapper.toEntity(produitDto);
        produit.setId(null);
        produit = produitService.AjouterProduit(produit);
        return ResponseEntity.created(URI.create("/produits/" + produit.getId()))
                .body(produitMapper.toDto(produit));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProduitDto> modifierProduit(@PathVariable Long id,
                                                         @Valid @RequestBody ProduitDto produitDto) {
        Produit produit = produitService.modifierProduit(id, produitMapper.toEntity(produitDto));
        return ResponseEntity.ok(produitMapper.toDto(produit));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerProduit(@PathVariable Long id) {
        produitService.supprimerProduit(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/stock/reserver")
    public ResponseEntity<ProduitDto> reserverStock(@PathVariable Long id,
                                                   @Valid @RequestBody ReservationStockDto reservation) {
        return ResponseEntity.ok(produitMapper.toDto(produitService.reserverStock(id, reservation.getQuantite())));
    }
}
