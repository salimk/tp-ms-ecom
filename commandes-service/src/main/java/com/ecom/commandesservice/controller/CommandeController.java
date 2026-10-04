package com.ecom.commandesservice.controller;

import com.ecom.commandesservice.dto.CommandeDto;
import com.ecom.commandesservice.dto.CommandeDtoDetail;
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


@PostMapping("/new")
public ResponseEntity<CommandeDtoDetail> createCommande(@Valid @RequestBody CreateCommandeDto createCommandeDto,
                                        @NotBlank @Size(max = 255) @RequestHeader(name="idempotencyKey") String idempotency ){
        Commande commande = commandeService.createCommande(commandeMapper.toEntity(createCommandeDto),idempotency);
        return ResponseEntity.status(HttpStatus.CREATED).body(commandeMapperDetail.toDto(commande));
}

    @GetMapping
    public ResponseEntity<List<CommandeDto>> getlistCommandes(){
        return ResponseEntity.ok(commandeService.listallCommande().stream().map(commandeMapper::toDto).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommandeDtoDetail> getCommandeById(@PathVariable Long id){
        Commande commande = commandeService.getCommandeById(id);
        return ResponseEntity.ok(commandeMapperDetail.toDto(commande));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> donneesInvalides(ConstraintViolationException exception) {
        return ResponseEntity.badRequest().body("Données invalides");
    }


}
