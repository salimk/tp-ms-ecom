package com.ecom.clientsservice;

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
class ClientsServiceApplicationTests {
    @Autowired
    MockMvc mockMvc;

    @Test
    void donneesInitialesEtClientIntrouvable() throws Exception {
        mockMvc.perform(get("/clients")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(4));
        mockMvc.perform(get("/clients/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Amin jamali"))
                .andExpect(jsonPath("$.email").value("amine@email.com"));
        mockMvc.perform(get("/clients/9999")).andExpect(status().isNotFound());
    }

    @Test
    void creationModificationSuppression() throws Exception {
        String reponse = mockMvc.perform(post("/clients/new").contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":1,\"name\":\"Test\",\"email\":\"test@email.com\",\"etat\":1}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getHeader("Location");
        mockMvc.perform(put(reponse).contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Modifie\",\"email\":\"test@email.com\",\"etat\":2}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.name").value("Modifie"));
        mockMvc.perform(delete(reponse)).andExpect(status().isNoContent());
        mockMvc.perform(get(reponse)).andExpect(status().isNotFound());
    }

    @Test
    void donneesInvalides() throws Exception {
        mockMvc.perform(post("/clients/new").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Test\",\"email\":\"incorrect\",\"etat\":1}"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/clients/new").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Test\",\"email\":\"test@email.com\"}"))
                .andExpect(status().isBadRequest());
    }
}
