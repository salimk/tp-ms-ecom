package com.ecom.notificationservice.controller;

import com.ecom.notificationservice.dto.NotificationEmailDto;
import com.ecom.notificationservice.service.NotificationService;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
    private final NotificationService notificationService;
    public NotificationController(NotificationService notificationService, Validator validator) {
        this.notificationService = notificationService;
    }

    @PostMapping("/email")
    public ResponseEntity<Void>

    envoyer(@Valid @RequestBody NotificationEmailDto notification) {
        notificationService.envoyerEmail(notification);
        return ResponseEntity.noContent().build();
    }
}
