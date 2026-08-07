package com.rentagreement.service.impl;

import com.rentagreement.dto.broker.BrokerListResponseDTO;
import com.rentagreement.dto.broker.BrokerUpdateRequestDTO;
import com.rentagreement.entity.Broker;
import com.rentagreement.exception.ResourceNotFoundException;
import com.rentagreement.repository.BrokerRepository;
import com.rentagreement.service.BrokerService;
import org.springframework.stereotype.Service;

import com.rentagreement.dto.auth.BrokerRegisterRequestDTO;
import com.rentagreement.dto.auth.BrokerResponseDTO;
import com.rentagreement.enums.BrokerStatus;
import com.rentagreement.enums.Role;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

@Service
public class BrokerServiceImpl implements BrokerService {

    private final BrokerRepository brokerRepository;
    private final PasswordEncoder passwordEncoder;

    public BrokerServiceImpl(BrokerRepository brokerRepository,
                             PasswordEncoder passwordEncoder) {

        this.brokerRepository = brokerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public BrokerResponseDTO addBroker(BrokerRegisterRequestDTO request) {

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

    @Override
    public Page<BrokerListResponseDTO> getAllBrokers(
            int page,
            int size,
            String keyword,
            String sortBy,
            String direction
    ) {

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(
                page,
                size,
                sort
        );

        Page<Broker> brokerPage;

        if (keyword == null || keyword.isBlank()) {

            brokerPage = brokerRepository.findByDeletedFalse(pageable);

        } else {

            brokerPage = brokerRepository
                    .findByDeletedFalseAndBrokerNameContainingIgnoreCaseOrDeletedFalseAndCompanyNameContainingIgnoreCaseOrDeletedFalseAndEmailContainingIgnoreCase(
                            keyword,
                            keyword,
                            keyword,
                            pageable
                    );

        }

        return brokerPage.map(this::mapToDTO);

    }


    @Override
    public BrokerListResponseDTO getBrokerById(Long id) {

        Broker broker = brokerRepository.findById(id)
                .filter(b -> !b.isDeleted())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Broker not found"));

        return mapToDTO(broker);

    }

    @Override
    public BrokerListResponseDTO updateBroker(Long id,
                                              BrokerUpdateRequestDTO request) {

        Broker broker = brokerRepository.findById(id)
                .filter(b -> !b.isDeleted())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Broker not found"));

        broker.setBrokerName(request.getBrokerName());
        broker.setCompanyName(request.getCompanyName());
        broker.setEmail(request.getEmail());
        broker.setMobileNumber(request.getMobileNumber());
        broker.setOfficeAddress(request.getOfficeAddress());

        Broker updatedBroker = brokerRepository.save(broker);

        return mapToDTO(updatedBroker);

    }

    @Override
    public void activateBroker(Long id) {

        Broker broker = brokerRepository.findById(id)
                .filter(b -> !b.isDeleted())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Broker not found"));

        broker.setStatus(com.rentagreement.enums.BrokerStatus.ACTIVE);

        brokerRepository.save(broker);

    }

    @Override
    public void deactivateBroker(Long id) {

        Broker broker = brokerRepository.findById(id)
                .filter(b -> !b.isDeleted())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Broker not found"));

        broker.setStatus(com.rentagreement.enums.BrokerStatus.INACTIVE);

        brokerRepository.save(broker);

    }

    @Override
    public void deleteBroker(Long id) {

        Broker broker = brokerRepository.findById(id)
                .filter(b -> !b.isDeleted())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Broker not found"));

        broker.setDeleted(true);

        brokerRepository.save(broker);

    }

    private BrokerListResponseDTO mapToDTO(Broker broker) {

        return BrokerListResponseDTO.builder()
                .id(broker.getId())
                .brokerName(broker.getBrokerName())
                .companyName(broker.getCompanyName())
                .email(broker.getEmail())
                .mobileNumber(broker.getMobileNumber())
                .username(broker.getUsername())
                .officeAddress(broker.getOfficeAddress())
                .status(broker.getStatus())
                .build();

    }

}