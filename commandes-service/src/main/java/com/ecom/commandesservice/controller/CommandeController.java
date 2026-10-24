package com.ecom.commandesservice.controller;

import com.ecom.commandesservice.dto.CommandeDto;
import com.ecom.commandesservice.dto.CommandeDtoDetail;
import com.ecom.commandesservice.dto.CommandeDtoFull;
import com.ecom.commandesservice.dto.CreateCommandeDto;
import com.ecom.commandesservice.entite.Commande;
import com.ecom.commandesservice.mapper.CommandeMapper;
import com.ecom.commandesservice.mapper.CommandeMapperDetail;
import com.ecom.commandesservice.service.CommandeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.ConstraintViolationException;

import java.util.List;

@RestController
@Validated
@RequestMapping("/commandes")
public class CommandeController {
    private final CommandeService commandeService;
    private final CommandeMapper commandeMapper;
    private final CommandeMapperDetail commandeMapperDetail;

    public CommandeController(CommandeService commandeService, CommandeMapper commandeMapper, CommandeMapperDetail commandeMapperDetail){
        this.commandeService = commandeService;
        this.commandeMapper = commandeMapper;
        this.commandeMapperDetail = commandeMapperDetail;
    }

    @GetMapping
    public ResponseEntity<List<CommandeDto>> getlistCommandes(){
        return ResponseEntity.ok(commandeService.listallCommande().stream().map(commandeMapper::toDto).toList());
    }
    // Ajouter une nouvelle commande
    @PostMapping("/new")
    public ResponseEntity<CommandeDtoDetail> createCommande(@Valid @RequestBody CreateCommandeDto createCommandeDto,
                                            @NotBlank @Size(max = 255) @RequestHeader(name="idempotencyKey") String idempotency ){
            Commande commande = commandeService.createCommande(commandeMapper.toEntity(createCommandeDto),idempotency);
            return ResponseEntity.status(HttpStatus.CREATED).body(commandeMapperDetail.toDto(commande));
    }

    //Récupérer une commande par son id y inclure les lignes de commande (que les références client et produits)
    @GetMapping("/{id}")
    public ResponseEntity<CommandeDtoDetail> getCommandeById(@PathVariable Long id){
        CommandeDtoDetail commandeDtoDetail = commandeMapperDetail.toDto(commandeService.getCommandeById(id));
        return ResponseEntity.ok(commandeDtoDetail);
    }
    //Récupérer une commande par son id y inclure les lignes de commande les details du client et details des produits
    @GetMapping("/{id}/detail")
    public ResponseEntity<CommandeDtoFull> getCommandeRichById(@PathVariable Long id){
        CommandeDtoFull commandeDtoFull= commandeService.getCommandeFullById(id);
        return ResponseEntity.ok(commandeDtoFull);
    }



}
