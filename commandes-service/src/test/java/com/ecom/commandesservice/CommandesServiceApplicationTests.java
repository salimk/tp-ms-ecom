package com.ecom.commandesservice;

import com.ecom.commandesservice.repository.CommandeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CommandesServiceApplicationTests {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    CommandeRepository commandeRepository;

    @Test
    void donneesInitialesEtMontants() throws Exception {
        mockMvc.perform(get("/commandes/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("CONFIRMEE"))
                .andExpect(jsonPath("$.lignesCommande.length()").value(3))
                .andExpect(jsonPath("$.prixTotal").value(8297.48));
        var commande = commandeRepository.findById(1L).orElseThrow();
        BigDecimal total = commande.getLignesCommande().stream()
                .map(ligne -> ligne.getPrixUnitaire().multiply(BigDecimal.valueOf(ligne.getQuantite())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        assertEquals(0, total.compareTo(commande.getPrixTotal()));
        mockMvc.perform(get("/commandes/9999")).andExpect(status().isNotFound());
    }

    @Test
    void creationReserveeAuTpFeign() throws Exception {
        mockMvc.perform(post("/commandes/new").header("idempotencyKey", "test")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"clientId\":1,\"lignesCommande\":[{\"produitId\":5,\"quantite\":2}]}"))
                .andExpect(status().isNotImplemented());
    }

    @Test
    void donneesInvalidesAvantMapping() throws Exception {
        for (String corps : new String[]{"{\"clientId\":1}",
                "{\"clientId\":1,\"lignesCommande\":[null]}",
                "{\"clientId\":1,\"lignesCommande\":[{\"produitId\":5,\"quantite\":0}]}"}) {
            mockMvc.perform(post("/commandes/new").header("idempotencyKey", "test")
                    .contentType(MediaType.APPLICATION_JSON).content(corps))
                    .andExpect(status().isBadRequest());
        }
        mockMvc.perform(post("/commandes/new").header("idempotencyKey", " ")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"clientId\":1,\"lignesCommande\":[{\"produitId\":5,\"quantite\":2}]}"))
                .andExpect(status().isBadRequest());
    }
}
