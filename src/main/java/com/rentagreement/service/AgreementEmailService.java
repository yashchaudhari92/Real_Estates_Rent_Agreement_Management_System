package com.rentagreement.service;

import com.rentagreement.entity.RentAgreement;

public interface AgreementEmailService {

    void sendExpiryNotificationEmail(
            RentAgreement agreement,
            String recipientEmail,
            String recipientName
    );

}