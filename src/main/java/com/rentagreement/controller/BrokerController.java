package com.rentagreement.controller;

import com.rentagreement.dto.auth.BrokerRegisterRequestDTO;
import com.rentagreement.dto.auth.BrokerResponseDTO;
import com.rentagreement.dto.broker.BrokerListResponseDTO;
import com.rentagreement.dto.broker.BrokerUpdateRequestDTO;
import com.rentagreement.response.ApiResponse;
import com.rentagreement.service.BrokerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

import java.util.List;

@RestController
@RequestMapping("/api/admin/brokers")
@CrossOrigin(origins = "http://localhost:5173")
public class BrokerController {

    private final BrokerService brokerService;

    public BrokerController(BrokerService brokerService) {
        this.brokerService = brokerService;
    }

    // Add Broker
    @PostMapping
    public ResponseEntity<ApiResponse<BrokerResponseDTO>> addBroker(
            @Valid @RequestBody BrokerRegisterRequestDTO request
    ) {

        BrokerResponseDTO broker =
                brokerService.addBroker(request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Broker Added Successfully",
                        broker
                )
        );
    }

    // View All Brokers
    @GetMapping
    public ResponseEntity<ApiResponse<Page<BrokerListResponseDTO>>> getAllBrokers(

            @RequestParam(defaultValue = "0") int page,

            @RequestParam(defaultValue = "10") int size,

            @RequestParam(defaultValue = "") String keyword,

            @RequestParam(defaultValue = "id") String sortBy,

            @RequestParam(defaultValue = "desc") String direction

    ) {

        return ResponseEntity.ok(

                new ApiResponse<>(

                        true,

                        "Broker List Fetched Successfully",

                        brokerService.getAllBrokers(
                                page,
                                size,
                                keyword,
                                sortBy,
                                direction
                        )

                )

        );

    }

    // View Broker By Id
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BrokerListResponseDTO>> getBrokerById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Broker Details",
                        brokerService.getBrokerById(id)
                )
        );
    }

    // Update Broker
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BrokerListResponseDTO>> updateBroker(
            @PathVariable Long id,
            @Valid @RequestBody BrokerUpdateRequestDTO request
    ) {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Broker Updated Successfully",
                        brokerService.updateBroker(id, request)
                )
        );
    }

    // Activate Broker
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<String>> activateBroker(
            @PathVariable Long id
    ) {

        brokerService.activateBroker(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Broker Activated Successfully",
                        null
                )
        );
    }

    // Deactivate Broker
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<String>> deactivateBroker(
            @PathVariable Long id
    ) {

        brokerService.deactivateBroker(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Broker Deactivated Successfully",
                        null
                )
        );
    }

    // Soft Delete Broker
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteBroker(
            @PathVariable Long id
    ) {

        brokerService.deleteBroker(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Broker Deleted Successfully",
                        null
                )
        );
    }

}