package com.ecom.catalogueservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CatalogueServiceApplicationTests {
    @Autowired
    MockMvc mockMvc;

    @Test
    void donneesInitialesEtProduitIntrouvable() throws Exception {
        mockMvc.perform(get("/produits")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(15));
        mockMvc.perform(get("/produits/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Apple iPhone 14 Pro"));
        mockMvc.perform(get("/produits/9999")).andExpect(status().isNotFound());
    }

    @Test
    void creationModificationSuppression() throws Exception {
        String reponse = mockMvc.perform(post("/produits/new").contentType(MediaType.APPLICATION_JSON)
                .content("{\"nom\":\"Clavier\",\"sku\":\"TEST\",\"prix\":0.10,\"qteStock\":5}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.prix").value(0.10))
                .andReturn().getResponse().getHeader("Location");
        mockMvc.perform(put(reponse).contentType(MediaType.APPLICATION_JSON)
                .content("{\"nom\":\"Clavier\",\"sku\":\"TEST\",\"prix\":0.30,\"qteStock\":6}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.prix").value(0.30));
        mockMvc.perform(delete(reponse)).andExpect(status().isNoContent());
        mockMvc.perform(get(reponse)).andExpect(status().isNotFound());
    }

    @Test
    void reservationEtStockInsuffisant() throws Exception {
        mockMvc.perform(post("/produits/5/stock/reserver").contentType(MediaType.APPLICATION_JSON)
                .content("{\"quantite\":2}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.qteStock").value(23));
        mockMvc.perform(post("/produits/5/stock/reserver").contentType(MediaType.APPLICATION_JSON)
                .content("{\"quantite\":100}"))
                .andExpect(status().isConflict());
        mockMvc.perform(get("/produits/5")).andExpect(jsonPath("$.qteStock").value(23));
    }

    @Test
    void donneesInvalides() throws Exception {
        mockMvc.perform(post("/produits/new").contentType(MediaType.APPLICATION_JSON)
                .content("{\"nom\":\"Clavier\",\"sku\":\"TEST\",\"prix\":0.123,\"qteStock\":5}"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/produits/5/stock/reserver").contentType(MediaType.APPLICATION_JSON)
                .content("{\"quantite\":0}"))
                .andExpect(status().isBadRequest());
    }
}
