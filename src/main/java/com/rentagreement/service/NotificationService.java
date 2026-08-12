package com.rentagreement.service;

import com.rentagreement.dto.notification.NotificationResponseDTO;
import com.rentagreement.entity.RentAgreement;
import com.rentagreement.enums.NotificationRecipientType;
import com.rentagreement.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {

    void createNotification(
            RentAgreement agreement,
            NotificationRecipientType recipientType,
            String recipientEmail,
            NotificationType notificationType,
            String title,
            String message
    );

    Page<NotificationResponseDTO> getBrokerNotifications(
            String brokerEmail,
            Pageable pageable
    );

    long getUnreadCount(
            String brokerEmail
    );

    void markAsRead(
            Long notificationId,
            String brokerEmail
    );

    void markAllAsRead(
            String brokerEmail
    );

}