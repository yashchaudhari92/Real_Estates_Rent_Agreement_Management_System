package com.rentagreement.dto.notification;

import com.rentagreement.enums.NotificationRecipientType;
import com.rentagreement.enums.NotificationType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationResponseDTO {

    private Long id;

    private Long agreementId;

    private String title;

    private String message;

    private NotificationType notificationType;

    private NotificationRecipientType recipientType;

    private boolean read;

    private LocalDateTime createdAt;

}