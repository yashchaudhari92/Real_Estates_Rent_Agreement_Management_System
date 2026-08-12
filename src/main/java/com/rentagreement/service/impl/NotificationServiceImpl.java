package com.rentagreement.service.impl;

import com.rentagreement.dto.notification.NotificationResponseDTO;
import com.rentagreement.entity.Notification;
import com.rentagreement.entity.RentAgreement;
import com.rentagreement.enums.NotificationRecipientType;
import com.rentagreement.enums.NotificationType;
import com.rentagreement.exception.ResourceNotFoundException;
import com.rentagreement.repository.NotificationRepository;
import com.rentagreement.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class NotificationServiceImpl
        implements NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(
            NotificationRepository notificationRepository
    ) {

        this.notificationRepository =
                notificationRepository;

    }


    // ==========================
    // Create Notification
    // ==========================

    @Override
    public void createNotification(
            RentAgreement agreement,
            NotificationRecipientType recipientType,
            String recipientEmail,
            NotificationType notificationType,
            String title,
            String message
    ) {

        boolean alreadyExists =
                notificationRepository
                        .existsByAgreementIdAndRecipientTypeAndNotificationType(
                                agreement.getId(),
                                recipientType,
                                notificationType
                        );

        if (alreadyExists) {

            return;

        }

        Notification notification =
                Notification.builder()

                        .agreement(agreement)

                        .recipientType(recipientType)

                        .recipientEmail(recipientEmail)

                        .notificationType(notificationType)

                        .title(title)

                        .message(message)

                        .build();

        notificationRepository.save(notification);

    }


    // ==========================
    // Get Broker Notifications
    // ==========================

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponseDTO> getBrokerNotifications(
            String brokerEmail,
            Pageable pageable
    ) {

        return notificationRepository
                .findByRecipientTypeAndRecipientEmailOrderByCreatedAtDesc(
                        NotificationRecipientType.BROKER,
                        brokerEmail,
                        pageable
                )
                .map(this::mapToDTO);

    }


    // ==========================
    // Unread Count
    // ==========================

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(
            String brokerEmail
    ) {

        return notificationRepository
                .countByRecipientTypeAndRecipientEmailAndReadFalse(
                        NotificationRecipientType.BROKER,
                        brokerEmail
                );

    }


    // ==========================
    // Mark One as Read
    // ==========================

    @Override
    public void markAsRead(
            Long notificationId,
            String brokerEmail
    ) {

        Notification notification =
                notificationRepository
                        .findByIdAndRecipientTypeAndRecipientEmail(
                                notificationId,
                                NotificationRecipientType.BROKER,
                                brokerEmail
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Notification not found"
                                ));

        notification.setRead(true);

        notificationRepository.save(notification);

    }


    // ==========================
    // Mark All as Read
    // ==========================

    @Override
    public void markAllAsRead(
            String brokerEmail
    ) {

        Page<Notification> notifications =
                notificationRepository
                        .findByRecipientTypeAndRecipientEmailOrderByCreatedAtDesc(
                                NotificationRecipientType.BROKER,
                                brokerEmail,
                                Pageable.unpaged()
                        );

        notifications.forEach(notification -> {

            if (!notification.isRead()) {

                notification.setRead(true);

            }

        });

        notificationRepository.saveAll(
                notifications.getContent()
        );

    }


    // ==========================
    // DTO Mapping
    // ==========================

    private NotificationResponseDTO mapToDTO(
            Notification notification
    ) {

        return NotificationResponseDTO.builder()

                .id(notification.getId())

                .agreementId(
                        notification.getAgreement().getId()
                )

                .title(notification.getTitle())

                .message(notification.getMessage())

                .notificationType(
                        notification.getNotificationType()
                )

                .recipientType(
                        notification.getRecipientType()
                )

                .read(notification.isRead())

                .createdAt(
                        notification.getCreatedAt()
                )

                .build();

    }

}