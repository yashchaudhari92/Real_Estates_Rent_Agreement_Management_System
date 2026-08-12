package com.rentagreement.service;

public interface EmailService {

    void sendEmail(
            String recipientEmail,
            String subject,
            String message
    );

}