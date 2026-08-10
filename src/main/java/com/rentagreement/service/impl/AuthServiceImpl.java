package com.rentagreement.service.impl;

import com.rentagreement.dto.auth.BrokerResponseDTO;
import com.rentagreement.dto.auth.LoginRequestDTO;
import com.rentagreement.dto.auth.LoginResponseDTO;
import com.rentagreement.entity.Broker;
import com.rentagreement.exception.InvalidCredentialsException;
import com.rentagreement.repository.BrokerRepository;
import com.rentagreement.dto.auth.BrokerRegisterRequestDTO;
import com.rentagreement.enums.BrokerStatus;
import com.rentagreement.enums.Role;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.rentagreement.security.jwt.JwtUtil;
import com.rentagreement.service.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final BrokerRepository brokerRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.username}")
    private String adminUsername;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           BrokerRepository brokerRepository,
                           JwtUtil jwtUtil,
                           PasswordEncoder passwordEncoder) {

        this.authenticationManager = authenticationManager;
        this.brokerRepository = brokerRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {

        try {

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

        } catch (Exception e) {

            throw new InvalidCredentialsException(
                    "Invalid Username or Password"
            );
        }

        String role;
        Broker broker = null;

        if (request.getUsername().equals(adminUsername)) {

            role = "ROLE_ADMIN";

        } else {

            broker = brokerRepository
                    .findByUsernameAndDeletedFalse(request.getUsername())
                    .orElseThrow(() ->
                            new InvalidCredentialsException(
                                    "Invalid Username or Password"
                            ));

            // Check Broker Status
            if (broker.getStatus() == BrokerStatus.INACTIVE) {
                throw new InvalidCredentialsException(
                        "Broker Account is Inactive"
                );
            }

            role = broker.getRole().name();
        }

        String token = jwtUtil.generateToken(
                request.getUsername(),
                role
        );

        return LoginResponseDTO.builder()
                .id(broker != null ? broker.getId() : null)
                .token(token)
                .username(request.getUsername())
                .role(role)
                .build();
    }

    @Override
    public BrokerResponseDTO registerBroker(BrokerRegisterRequestDTO request) {

        if (brokerRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (brokerRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        Broker broker = Broker.builder()
                .brokerName(request.getBrokerName())
                .companyName(request.getCompanyName())
                .email(request.getEmail())
                .mobileNumber(request.getMobileNumber())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .officeAddress(request.getOfficeAddress())
                .role(Role.ROLE_BROKER)
                .status(BrokerStatus.INACTIVE)
                .deleted(false)
                .build();

        Broker savedBroker = brokerRepository.save(broker);

        return BrokerResponseDTO.builder()
                .id(savedBroker.getId())
                .brokerName(savedBroker.getBrokerName())
                .companyName(savedBroker.getCompanyName())
                .email(savedBroker.getEmail())
                .mobileNumber(savedBroker.getMobileNumber())
                .username(savedBroker.getUsername())
                .officeAddress(savedBroker.getOfficeAddress())
                .role(savedBroker.getRole())
                .status(savedBroker.getStatus())
                .build();
    }
}