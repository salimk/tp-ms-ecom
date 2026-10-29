package com.ecom.notificationservice.service;

import com.ecom.notificationservice.dto.NotificationEmailDto;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final JavaMailSender mailSender;

    public NotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    public void envoyerEmail(NotificationEmailDto notificationEmailDto) {
       // model de message
        SimpleMailMessage message=new SimpleMailMessage();
        message.setFrom("noreply@ecom.com");
        message.setTo(notificationEmailDto.destinataire());
        message.setText(notificationEmailDto.contenu());
        message.setSubject(notificationEmailDto.objet());
        mailSender.send(message);
    }
}
