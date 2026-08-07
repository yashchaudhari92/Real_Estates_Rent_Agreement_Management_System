package com.rentagreement.controller;

import com.rentagreement.dto.auth.BrokerRegisterRequestDTO;
import com.rentagreement.dto.auth.BrokerResponseDTO;
import com.rentagreement.entity.Broker;
import com.rentagreement.dto.auth.LoginRequestDTO;
import com.rentagreement.dto.auth.LoginResponseDTO;
import com.rentagreement.response.ApiResponse;
import com.rentagreement.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> login(
            @Valid @RequestBody LoginRequestDTO request
    ) {

        LoginResponseDTO response =
                authService.login(request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Login Successful",
                        response
                )
        );
    }

    @PostMapping("/register-broker")
    public ResponseEntity<ApiResponse<BrokerResponseDTO>> registerBroker(
                                                                         @Valid @RequestBody BrokerRegisterRequestDTO request
    ) {

        BrokerResponseDTO broker = authService.registerBroker(request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Broker Registered Successfully",
                        broker
                )
        );
    }

}