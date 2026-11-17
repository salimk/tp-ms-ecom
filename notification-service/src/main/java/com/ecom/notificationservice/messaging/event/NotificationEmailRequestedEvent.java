package com.ecom.notificationservice.messaging.event;

import java.util.UUID;

public record NotificationEmailRequestedEvent(
        /** Identifiant unique de l'événement (généré côté producteur). */
        UUID eventId,
        /** Version du contrat. Initialement 1. */
        int eventVersion,
        /** Horodatage UTC de création de l'événement (format ISO-8601). */
        String occurredAt,
        /** Identifiant de corrélation : idempotencyKey de la commande. */
        String correlationId,
        Long commandeId,
        String email,
        String sujet,
        String contenu
) {}
