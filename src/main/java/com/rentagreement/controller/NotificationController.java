package com.rentagreement.controller;

import com.rentagreement.dto.notification.NotificationResponseDTO;
import com.rentagreement.entity.Broker;
import com.rentagreement.repository.BrokerRepository;
import com.rentagreement.response.ApiResponse;
import com.rentagreement.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/broker/notifications")
@CrossOrigin(origins = "http://localhost:5173")
public class NotificationController {

    private final NotificationService notificationService;

    private final BrokerRepository brokerRepository;


    public NotificationController(
            NotificationService notificationService,
            BrokerRepository brokerRepository
    ) {

        this.notificationService = notificationService;

        this.brokerRepository = brokerRepository;

    }


    // ==========================
    // Get Notifications
    // ==========================

    @GetMapping
    public ResponseEntity<
            ApiResponse<Page<NotificationResponseDTO>>
            > getNotifications(

            @RequestParam(
                    defaultValue = "0"
            )
            int page,

            @RequestParam(
                    defaultValue = "10"
            )
            int size

    ) {

        Broker broker = getCurrentBroker();

        Pageable pageable =
                PageRequest.of(page, size);

        Page<NotificationResponseDTO> notifications =
                notificationService.getBrokerNotifications(
                        broker.getEmail(),
                        pageable
                );

        return ResponseEntity.ok(

                new ApiResponse<>(
                        true,
                        "Notifications Fetched Successfully",
                        notifications
                )

        );

    }


    // ==========================
    // Unread Count
    // ==========================

    @GetMapping("/unread-count")
    public ResponseEntity<
            ApiResponse<Long>
            > getUnreadCount() {

        Broker broker = getCurrentBroker();

        long unreadCount =
                notificationService.getUnreadCount(
                        broker.getEmail()
                );

        return ResponseEntity.ok(

                new ApiResponse<>(
                        true,
                        "Unread Notification Count Fetched Successfully",
                        unreadCount
                )

        );

    }


    // ==========================
    // Mark One as Read
    // ==========================

    @PutMapping("/{id}/read")
    public ResponseEntity<
            ApiResponse<Void>
            > markAsRead(
            @PathVariable Long id
    ) {

        Broker broker = getCurrentBroker();

        notificationService.markAsRead(
                id,
                broker.getEmail()
        );

        return ResponseEntity.ok(

                new ApiResponse<>(
                        true,
                        "Notification Marked As Read",
                        null
                )

        );

    }


    // ==========================
    // Mark All as Read
    // ==========================

    @PutMapping("/read-all")
    public ResponseEntity<
            ApiResponse<Void>
            > markAllAsRead() {

        Broker broker = getCurrentBroker();

        notificationService.markAllAsRead(
                broker.getEmail()
        );

        return ResponseEntity.ok(

                new ApiResponse<>(
                        true,
                        "All Notifications Marked As Read",
                        null
                )

        );

    }


    // ==========================
    // Current Broker
    // ==========================

    private Broker getCurrentBroker() {

        String username =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        return brokerRepository
                .findByUsernameAndDeletedFalse(username)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Logged-in broker not found"
                        ));

    }

}