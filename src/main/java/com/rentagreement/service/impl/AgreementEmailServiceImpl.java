package com.rentagreement.service.impl;

import com.rentagreement.entity.RentAgreement;
import com.rentagreement.service.AgreementEmailService;
import com.rentagreement.service.EmailService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class AgreementEmailServiceImpl
        implements AgreementEmailService {

    private final EmailService emailService;


    public AgreementEmailServiceImpl(
            EmailService emailService
    ) {

        this.emailService = emailService;

    }


    @Override
    public void sendExpiryNotificationEmail(
            RentAgreement agreement,
            String recipientEmail,
            String recipientName
    ) {

        long remainingDays =
                ChronoUnit.DAYS.between(
                        LocalDate.now(),
                        agreement.getEndDate()
                );


        String buildingName =
                agreement.getBuilding()
                        .getBuildingName();


        String subject =
                "Rent Agreement Expiry Reminder - "
                        + buildingName;


        String message =
                "Dear "
                        + recipientName
                        + ",\n\n"

                        + "This is a reminder, your rent agreement "
                        + "for "
                        + buildingName
                        + " is expiring soon.\n\n"

                        + "Agreement Details:\n"

                        + "Building: "
                        + buildingName
                        + "\n"

                        + "Agreement Start Date: "
                        + agreement.getStartDate()
                        + "\n"

                        + "Agreement End Date: "
                        + agreement.getEndDate()
                        + "\n"

                        + "Days Remaining: "
                        + remainingDays
                        + "\n"

                        + "Monthly Rent: ₹"
                        + agreement.getMonthlyRent()
                        + "\n\n"

                        + "Please take the necessary action "
                        + "regarding the agreement before its expiry.\n\n"

                        + "Regards,\n"
                        + "Rent Agreement Management System";


        emailService.sendEmail(
                recipientEmail,
                subject,
                message
        );

    }

}