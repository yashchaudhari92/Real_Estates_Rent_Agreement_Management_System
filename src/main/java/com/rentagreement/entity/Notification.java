package com.rentagreement.entity;

import com.rentagreement.enums.NotificationRecipientType;
import com.rentagreement.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // ==========================
    // Agreement
    // ==========================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "agreement_id",
            nullable = false
    )
    private RentAgreement agreement;


    // ==========================
    // Recipient
    // ==========================

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationRecipientType recipientType;

    @Column(nullable = false)
    private String recipientEmail;


    // ==========================
    // Notification
    // ==========================

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType notificationType;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String message;


    // ==========================
    // Read / Unread
    // ==========================

    @Column(name = "is_read", nullable = false)
    private boolean read;


    // ==========================
    // Audit
    // ==========================

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;


    @PrePersist
    public void prePersist() {

        createdAt = LocalDateTime.now();

        read = false;

    }

}