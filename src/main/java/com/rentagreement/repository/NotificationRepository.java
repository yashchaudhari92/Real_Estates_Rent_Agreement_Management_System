package com.rentagreement.repository;

import com.rentagreement.entity.Notification;
import com.rentagreement.enums.NotificationRecipientType;
import com.rentagreement.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    // ==========================
    // Broker Notifications
    // ==========================

    Page<Notification>
    findByRecipientTypeAndRecipientEmailOrderByCreatedAtDesc(
            NotificationRecipientType recipientType,
            String recipientEmail,
            Pageable pageable
    );


    // ==========================
    // Unread Count
    // ==========================

    long countByRecipientTypeAndRecipientEmailAndReadFalse(
            NotificationRecipientType recipientType,
            String recipientEmail
    );


    // ==========================
    // Duplicate Prevention
    // ==========================

    boolean existsByAgreementIdAndRecipientTypeAndNotificationType(
            Long agreementId,
            NotificationRecipientType recipientType,
            NotificationType notificationType
    );


    // ==========================
    // Find Specific Notification
    // ==========================

    Optional<Notification>
    findByIdAndRecipientTypeAndRecipientEmail(
            Long id,
            NotificationRecipientType recipientType,
            String recipientEmail
    );

}