package com.rentagreement.service.impl;

import com.rentagreement.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;


    public EmailServiceImpl(
            JavaMailSender mailSender
    ) {

        this.mailSender = mailSender;

    }


    @Override
    public void sendEmail(
            String recipientEmail,
            String subject,
            String message
    ) {

        SimpleMailMessage mailMessage =
                new SimpleMailMessage();

        mailMessage.setFrom(senderEmail);

        mailMessage.setTo(recipientEmail);

        mailMessage.setSubject(subject);

        mailMessage.setText(message);

        mailSender.send(mailMessage);

    }

}