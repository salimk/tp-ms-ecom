package com.ecom.notificationservice.messaging;

import com.ecom.notificationservice.dto.NotificationEmailDto;
import com.ecom.notificationservice.messaging.event.NotificationEmailRequestedEvent;
import com.ecom.notificationservice.service.NotificationService;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class NotificationConsumer {

    private final NotificationService notificationService;
    private final Validator validator;

    @KafkaListener(topics = "${app.kafka.topic.notification}")
    public void recevoir(
            @Payload NotificationEmailRequestedEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        log.info("[Kafka] Message reçu : eventId={} commandeId={} partition={} offset={}",
                event.eventId(), event.commandeId(), partition, offset);
        // Conversion en DTO de validation (réutilise les contraintes existantes)
        NotificationEmailDto dto = new NotificationEmailDto(
                event.email(),
                event.sujet(),
                event.contenu()
        );
        var violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException("Événement invalide eventId=" + event.eventId(), violations);
        }
        // Envoi de DTO contenant les details du courrier au serveur mail
        notificationService.envoyerEmail(dto);
        log.info("[Kafka] E-mail envoyé : eventId={} destinataire={}", event.eventId(), event.email());
    }
}


