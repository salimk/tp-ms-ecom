package com.ecom.commandesservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
public class CreationCommandeNonDisponibleException extends RuntimeException {
    public CreationCommandeNonDisponibleException() {
        super("Création avec validation interservices : à réaliser dans le TP Feign.");
    }
}
