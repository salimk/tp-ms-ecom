package com.ecom.commandesservice.messaging;


import com.ecom.commandesservice.config.RabbitConfig;
import com.ecom.commandesservice.dto.NotificationEmailDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationPublisher {
    private final RabbitTemplate rabbitTemplate;

    public NotificationPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void envoyer(NotificationEmailDto notification) {
        rabbitTemplate.convertAndSend(RabbitConfig.QUEUE_EMAIL, notification);
    }
}
