package com.rentagreement.scheduler;

import com.rentagreement.entity.Broker;
import com.rentagreement.entity.RentAgreement;
import com.rentagreement.enums.NotificationRecipientType;
import com.rentagreement.enums.NotificationType;
import com.rentagreement.repository.RentAgreementRepository;
import com.rentagreement.service.AgreementEmailService;
import com.rentagreement.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class AgreementExpiryScheduler {

    private static final Logger logger =
            LoggerFactory.getLogger(
                    AgreementExpiryScheduler.class
            );

    private final RentAgreementRepository agreementRepository;

    private final NotificationService notificationService;

    private final AgreementEmailService agreementEmailService;


    public AgreementExpiryScheduler(
            RentAgreementRepository agreementRepository,
            NotificationService notificationService,
            AgreementEmailService agreementEmailService
    ) {

        this.agreementRepository = agreementRepository;

        this.notificationService = notificationService;

        this.agreementEmailService =
                agreementEmailService;

    }


    // ==========================================
    // Daily Agreement Expiry Check
    // ==========================================

    @Scheduled(
            cron = "0 0 9 * * *",
            zone = "Asia/Kolkata"
    )
    public void checkExpiringAgreements() {

        LocalDate targetDate =
                LocalDate.now().plusMonths(1);

        logger.info(
                "Checking agreements expiring one month from today on: {}",
                targetDate
        );


        List<RentAgreement> agreements =
                agreementRepository
                        .findByEndDateAndDeletedFalse(
                                targetDate
                        );


        if (agreements.isEmpty()) {

            logger.info(
                    "No agreements expiring one month from today."
            );

            return;

        }


        logger.info(
                "{} agreement(s) found expiring one month from today.",
                agreements.size()
        );


        for (RentAgreement agreement : agreements) {

            processAgreement(agreement);

        }

    }


    // ==========================================
    // Process Agreement
    // ==========================================

    private void processAgreement(
            RentAgreement agreement
    ) {

        try {

            sendBrokerNotification(agreement);

        } catch (Exception exception) {

            logger.error(
                    "Failed to send broker notification for agreement ID: {}",
                    agreement.getId(),
                    exception
            );

        }


        try {

            sendOwnerNotification(agreement);

        } catch (Exception exception) {

            logger.error(
                    "Failed to send owner notification for agreement ID: {}",
                    agreement.getId(),
                    exception
            );

        }


        try {

            sendTenantNotification(agreement);

        } catch (Exception exception) {

            logger.error(
                    "Failed to send tenant notification for agreement ID: {}",
                    agreement.getId(),
                    exception
            );

        }

    }


    // ==========================================
    // Broker Notification
    // ==========================================

    private void sendBrokerNotification(
            RentAgreement agreement
    ) {

        Broker broker =
                agreement.getBuilding()
                        .getBroker();


        String email =
                broker.getEmail();

        String name =
                broker.getBrokerName();


        // ======================================
        // Existing Email Logic - DO NOT CHANGE
        // ======================================

        agreementEmailService
                .sendExpiryNotificationEmail(
                        agreement,
                        email,
                        name
                );


        // ======================================
        // In-App Notification
        // ======================================

        notificationService.createNotification(

                agreement,

                NotificationRecipientType.BROKER,

                email,

                NotificationType
                        .AGREEMENT_EXPIRING_SOON,

                buildNotificationTitle(
                        agreement
                ),

                buildBrokerNotificationMessage(
                        agreement
                )

//                buildNotificationMessage(
//                        agreement,
//                        name
//                )

        );

    }


    // ==========================================
    // Owner Notification
    // ==========================================

    private void sendOwnerNotification(
            RentAgreement agreement
    ) {

        String email =
                agreement.getOwnerEmail();

        String name =
                agreement.getOwnerName();


        // ======================================
        // Existing Email Logic - DO NOT CHANGE
        // ======================================

        agreementEmailService
                .sendExpiryNotificationEmail(
                        agreement,
                        email,
                        name
                );


        // ======================================
        // In-App Notification
        // ======================================

        notificationService.createNotification(

                agreement,

                NotificationRecipientType.OWNER,

                email,

                NotificationType
                        .AGREEMENT_EXPIRING_SOON,

                buildNotificationTitle(
                        agreement
                ),

                buildNotificationMessage(
                        agreement,
                        name
                )

        );

    }


    // ==========================================
    // Tenant Notification
    // ==========================================

    private void sendTenantNotification(
            RentAgreement agreement
    ) {

        String email =
                agreement.getTenantEmail();

        String name =
                agreement.getTenantName();


        // ======================================
        // Existing Email Logic - DO NOT CHANGE
        // ======================================

        agreementEmailService
                .sendExpiryNotificationEmail(
                        agreement,
                        email,
                        name
                );


        // ======================================
        // In-App Notification
        // ======================================

        notificationService.createNotification(

                agreement,

                NotificationRecipientType.TENANT,

                email,

                NotificationType
                        .AGREEMENT_EXPIRING_SOON,

                buildNotificationTitle(
                        agreement
                ),

                buildNotificationMessage(
                        agreement,
                        name
                )

        );

    }


    // ==========================================
    // Notification Title
    // ==========================================

    private String buildNotificationTitle(
            RentAgreement agreement
    ) {

        String buildingName =
                agreement.getBuilding()
                        .getBuildingName();


        return "Rent Agreement Expiring Soon — "
                + buildingName;

    }

    // ==========================================
// Broker In-App Notification Message
// ==========================================

    private String buildBrokerNotificationMessage(
            RentAgreement agreement
    ) {

        String buildingName =
                agreement.getBuilding()
                        .getBuildingName();

        Broker broker =
                agreement.getBuilding()
                        .getBroker();

        long daysRemaining =
                LocalDate.now()
                        .until(
                                agreement.getEndDate()
                        )
                        .getDays();

        return
                "Society / Building: "
                        + buildingName
                        + "\n"

                        + "Broker: "
                        + broker.getBrokerName()
                        + "\n"

                        + "Owner: "
                        + agreement.getOwnerName()
                        + "\n"

                        + "Tenant: "
                        + agreement.getTenantName()
                        + "\n"

                        + "Agreement End Date: "
                        + agreement.getEndDate()
                        + "\n"

                        + "Days Remaining: "
                        + daysRemaining
                        + "\n\n"

                        + "Please take the necessary action "
                        + "regarding the agreement before its expiry.";
    }


    // ==========================================
    // Notification Message
    // ==========================================

    private String buildNotificationMessage(
            RentAgreement agreement,
            String recipientName
    ) {

        String buildingName =
                agreement.getBuilding()
                        .getBuildingName();


        LocalDate endDate =
                agreement.getEndDate();


        long daysRemaining =
                LocalDate.now()
                        .until(
                                endDate
                        )
                        .getDays();


        return
                "Dear " + recipientName + ",\n\n"

                        + "This is a reminder that the rent agreement "
                        + "for " + buildingName
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
                        + daysRemaining
                        + "\n"

                        + "Monthly Rent: ₹"
                        + agreement.getMonthlyRent()
                        + "\n\n"

                        + "Please take the necessary action "
                        + "regarding the agreement before its expiry.\n\n"

                        + "Regards,\n"
                        + "Rent Agreement Management System";

    }

}


//package com.rentagreement.scheduler;
//
//import com.rentagreement.entity.Broker;
//import com.rentagreement.entity.RentAgreement;
//import com.rentagreement.enums.NotificationRecipientType;
//import com.rentagreement.enums.NotificationType;
//import com.rentagreement.repository.RentAgreementRepository;
//import com.rentagreement.service.AgreementEmailService;
//import com.rentagreement.service.NotificationService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDate;
//import java.util.List;
//
//@Component
//public class AgreementExpiryScheduler {
//
//    private static final Logger logger =
//            LoggerFactory.getLogger(
//                    AgreementExpiryScheduler.class
//            );
//
//    private final RentAgreementRepository agreementRepository;
//
//    private final NotificationService notificationService;
//
//    private final AgreementEmailService agreementEmailService;
//
//
//    public AgreementExpiryScheduler(
//            RentAgreementRepository agreementRepository,
//            NotificationService notificationService,
//            AgreementEmailService agreementEmailService
//    ) {
//
//        this.agreementRepository = agreementRepository;
//
//        this.notificationService = notificationService;
//
//        this.agreementEmailService =
//                agreementEmailService;
//
//    }
//
//
//    // ==========================================
//    // Daily Agreement Expiry Check
//    // ==========================================
//
//    @Scheduled(
//            cron = "0 0 9 * * *",
//            zone = "Asia/Kolkata"
//    )
//    public void checkExpiringAgreements() {
//
//        LocalDate targetDate =
//                LocalDate.now().plusDays(6);
//
//        logger.info(
//                "Checking agreements expiring on: {}",
//                targetDate
//        );
//
//
//        List<RentAgreement> agreements =
//                agreementRepository
//                        .findByEndDateAndDeletedFalse(
//                                targetDate
//                        );
//
//
//        if (agreements.isEmpty()) {
//
//            logger.info(
//                    "No agreements expiring in 6 days."
//            );
//
//            return;
//
//        }
//
//
//        logger.info(
//                "{} agreement(s) found expiring in 6 days.",
//                agreements.size()
//        );
//
//
//        for (RentAgreement agreement : agreements) {
//
//            processAgreement(agreement);
//
//        }
//
//    }
//
//
//    // ==========================================
//    // Process Agreement
//    // ==========================================
//
//    private void processAgreement(
//            RentAgreement agreement
//    ) {
//
//        try {
//
//            sendBrokerNotification(agreement);
//
//        } catch (Exception exception) {
//
//            logger.error(
//                    "Failed to send broker notification for agreement ID: {}",
//                    agreement.getId(),
//                    exception
//            );
//
//        }
//
//
//        try {
//
//            sendOwnerNotification(agreement);
//
//        } catch (Exception exception) {
//
//            logger.error(
//                    "Failed to send owner notification for agreement ID: {}",
//                    agreement.getId(),
//                    exception
//            );
//
//        }
//
//
//        try {
//
//            sendTenantNotification(agreement);
//
//        } catch (Exception exception) {
//
//            logger.error(
//                    "Failed to send tenant notification for agreement ID: {}",
//                    agreement.getId(),
//                    exception
//            );
//
//        }
//
//    }
//
//
//    // ==========================================
//    // Broker Notification
//    // ==========================================
//
//    private void sendBrokerNotification(
//            RentAgreement agreement
//    ) {
//
//        Broker broker =
//                agreement.getBuilding()
//                        .getBroker();
//
//
//        String email =
//                broker.getEmail();
//
//        String name =
//                broker.getBrokerName();
//
//
//        agreementEmailService
//                .sendExpiryNotificationEmail(
//                        agreement,
//                        email,
//                        name
//                );
//
//
//        notificationService.createNotification(
//
//                agreement,
//
//                NotificationRecipientType.BROKER,
//
//                email,
//
//                NotificationType
//                        .AGREEMENT_EXPIRING_SOON,
//
//                "Agreement Expiring Soon",
//
//                "The rent agreement for "
//                        + agreement
//                        .getBuilding()
//                        .getBuildingName()
//                        + " will expire in 6 days."
//
//        );
//
//    }
//
//
//    // ==========================================
//    // Owner Notification
//    // ==========================================
//
//    private void sendOwnerNotification(
//            RentAgreement agreement
//    ) {
//
//        String email =
//                agreement.getOwnerEmail();
//
//        String name =
//                agreement.getOwnerName();
//
//
//        agreementEmailService
//                .sendExpiryNotificationEmail(
//                        agreement,
//                        email,
//                        name
//                );
//
//
//        notificationService.createNotification(
//
//                agreement,
//
//                NotificationRecipientType.OWNER,
//
//                email,
//
//                NotificationType
//                        .AGREEMENT_EXPIRING_SOON,
//
//                "Agreement Expiring Soon",
//
//                "The rent agreement for "
//                        + agreement
//                        .getBuilding()
//                        .getBuildingName()
//                        + " will expire in 6 days."
//
//        );
//
//    }
//
//
//    // ==========================================
//    // Tenant Notification
//    // ==========================================
//
//    private void sendTenantNotification(
//            RentAgreement agreement
//    ) {
//
//        String email =
//                agreement.getTenantEmail();
//
//        String name =
//                agreement.getTenantName();
//
//
//        agreementEmailService
//                .sendExpiryNotificationEmail(
//                        agreement,
//                        email,
//                        name
//                );
//
//
//        notificationService.createNotification(
//
//                agreement,
//
//                NotificationRecipientType.TENANT,
//
//                email,
//
//                NotificationType
//                        .AGREEMENT_EXPIRING_SOON,
//
//                "Agreement Expiring Soon",
//
//                "The rent agreement for "
//                        + agreement
//                        .getBuilding()
//                        .getBuildingName()
//                        + " will expire in 6 days."
//
//        );
//
//    }
//
//}