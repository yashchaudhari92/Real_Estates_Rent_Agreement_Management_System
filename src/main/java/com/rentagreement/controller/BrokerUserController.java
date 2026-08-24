package com.rentagreement.controller;

import com.rentagreement.dto.auth.BrokerResponseDTO;
import com.rentagreement.dto.broker.BrokerListResponseDTO;
import com.rentagreement.dto.broker.BrokerUserCreateRequestDTO;
import com.rentagreement.response.ApiResponse;
import com.rentagreement.service.BrokerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/broker/users")
@CrossOrigin(origins = "http://localhost:5173")
public class BrokerUserController {

    private final BrokerService brokerService;

    public BrokerUserController(
            BrokerService brokerService
    ) {

        this.brokerService = brokerService;
    }

    // ==========================================================
    // Check whether logged-in broker is Main Broker
    // ==========================================================

    @GetMapping("/access")
    public ResponseEntity<ApiResponse<Boolean>> checkAccess() {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Broker Access Checked Successfully",
                        brokerService.isMainBroker()
                )
        );
    }

    // ==========================================================
    // Create Sub User
    // ==========================================================

    @PostMapping
    public ResponseEntity<ApiResponse<BrokerResponseDTO>>
    createUser(
            @Valid @RequestBody
            BrokerUserCreateRequestDTO request
    ) {

        BrokerResponseDTO user =
                brokerService.createUser(request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "User Created Successfully",
                        user
                )
        );
    }

    // ==========================================================
    // Get My Users
    // ==========================================================

    @GetMapping
    public ResponseEntity<
            ApiResponse<List<BrokerListResponseDTO>>
            > getMyUsers() {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Users Fetched Successfully",
                        brokerService.getMyUsers()
                )
        );
    }

    // ==========================================================
    // Activate User
    // ==========================================================

    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<String>>
    activateUser(
            @PathVariable Long id
    ) {

        brokerService.activateUser(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "User Activated Successfully",
                        null
                )
        );
    }

    // ==========================================================
    // Deactivate User
    // ==========================================================

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<String>>
    deactivateUser(
            @PathVariable Long id
    ) {

        brokerService.deactivateUser(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "User Deactivated Successfully",
                        null
                )
        );
    }

    // ==========================================================
    // Delete User
    // ==========================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>>
    deleteUser(
            @PathVariable Long id
    ) {

        brokerService.deleteUser(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "User Deleted Successfully",
                        null
                )
        );
    }
}