package com.rentagreement.controller;

import com.rentagreement.dto.broker.DeletePasswordRequestDTO;
import com.rentagreement.response.ApiResponse;
import com.rentagreement.service.BrokerSecurityService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/broker/security")
@CrossOrigin(origins = "http://localhost:5173")
public class BrokerSecurityController {

    private final BrokerSecurityService brokerSecurityService;

    public BrokerSecurityController(
            BrokerSecurityService brokerSecurityService
    ) {
        this.brokerSecurityService = brokerSecurityService;
    }

    @GetMapping("/delete-password/status")
    public ResponseEntity<ApiResponse<Boolean>> getDeletePasswordStatus() {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Delete Password Status Fetched Successfully",
                        brokerSecurityService.hasDeletePassword()
                )
        );
    }

    @PutMapping("/delete-password")
    public ResponseEntity<ApiResponse<String>> setOrChangeDeletePassword(
            @Valid @RequestBody DeletePasswordRequestDTO request
    ) {

        brokerSecurityService.setOrChangeDeletePassword(request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Delete Password Saved Successfully",
                        null
                )
        );
    }

}