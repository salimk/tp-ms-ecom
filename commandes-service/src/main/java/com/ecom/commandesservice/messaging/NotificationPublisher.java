package com.ecom.commandesservice.messaging;


import com.ecom.commandesservice.messaging.event.NotificationEmailRequestedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@Slf4j
public class NotificationPublisher {

    private final KafkaTemplate<String, NotificationEmailRequestedEvent> kafkaTemplate;
    private final String topicNotification;

    public NotificationPublisher(
            KafkaTemplate<String, NotificationEmailRequestedEvent> kafkaTemplate,
            @Value("${app.kafka.topic.notification}") String topicNotification) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicNotification = topicNotification;
    }

    /** Cette méthode est appelée APRÈS le commit de la transaction locale
     * (via TransactionSynchronizationManager.afterCommit()).
     * LIMITE V7 : si Kafka est indisponible au moment du commit,
     * l'événement est perdu. Cette limite sera résolue par le pattern
     * Transactional Outbox au jalon V8.
     */
    public void envoyer(Long commandeId, String correlationId,
                        String email, String sujet, String contenu) {

        NotificationEmailRequestedEvent event = new NotificationEmailRequestedEvent(
                UUID.randomUUID(),
                1,
                Instant.now().toString(),
                correlationId,
                commandeId,
                email,
                sujet,
                contenu
        );

        // La clé Kafka est commandeId.toString() :
        // → tous les événements d'une même commande vont dans la même partition où l'ordre est garanti
        String messageKey = commandeId.toString();

        kafkaTemplate.send(topicNotification, messageKey, event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        // Si la publication a échoué après le commit de la transaction.
                        // L'événement est perdu. Ce cas sera géré par l'Outbox en V8.
                        log.error("[Kafka] Échec de publication de l'événement notification " +
                                        "pour commandeId={} eventId={} : {}",
                                commandeId, event.eventId(), ex.getMessage());
                    } else {
                        log.info("[Kafka] Événement notification publié : commandeId={} " +
                                        "partition={} offset={} eventId={}",
                                commandeId,
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset(),
                                event.eventId());
                    }
                });
    }
}