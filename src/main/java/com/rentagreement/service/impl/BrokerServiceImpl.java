package com.rentagreement.service.impl;

import com.rentagreement.dto.auth.BrokerRegisterRequestDTO;
import com.rentagreement.dto.auth.BrokerResponseDTO;
import com.rentagreement.dto.broker.BrokerListResponseDTO;
import com.rentagreement.dto.broker.BrokerUpdateRequestDTO;
import com.rentagreement.dto.broker.BrokerUserCreateRequestDTO;
import com.rentagreement.entity.Broker;
import com.rentagreement.enums.BrokerStatus;
import com.rentagreement.enums.Role;
import com.rentagreement.exception.ResourceNotFoundException;
import com.rentagreement.repository.BrokerRepository;
import com.rentagreement.service.BrokerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrokerServiceImpl implements BrokerService {

    private final BrokerRepository brokerRepository;
    private final PasswordEncoder passwordEncoder;

    public BrokerServiceImpl(
            BrokerRepository brokerRepository,
            PasswordEncoder passwordEncoder
    ) {

        this.brokerRepository = brokerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ==========================================================
    // Existing Admin Broker Management
    // ==========================================================

    @Override
    public BrokerResponseDTO addBroker(
            BrokerRegisterRequestDTO request
    ) {

        if (brokerRepository.existsByUsername(
                request.getUsername()
        )) {
            throw new RuntimeException(
                    "Username already exists"
            );
        }

        if (brokerRepository.existsByEmail(
                request.getEmail()
        )) {
            throw new RuntimeException(
                    "Email already exists"
            );
        }

        Broker broker = Broker.builder()
                .brokerName(request.getBrokerName())
                .companyName(request.getCompanyName())
                .email(request.getEmail())
                .mobileNumber(request.getMobileNumber())
                .username(request.getUsername())
                .password(
                        passwordEncoder.encode(
                                request.getPassword()
                        )
                )
                .officeAddress(
                        request.getOfficeAddress()
                )
                .role(Role.ROLE_BROKER)
                .status(BrokerStatus.INACTIVE)
                .deleted(false)
                .parentBroker(null)
                .build();

        Broker savedBroker =
                brokerRepository.save(broker);

        return mapToBrokerResponse(savedBroker);
    }

    @Override
    public Page<BrokerListResponseDTO> getAllBrokers(
            int page,
            int size,
            String keyword,
            String sortBy,
            String direction
    ) {

        Sort sort =
                direction.equalsIgnoreCase("asc")
                        ? Sort.by(sortBy).ascending()
                        : Sort.by(sortBy).descending();

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        sort
                );

        Page<Broker> brokerPage;

        if (keyword == null ||
                keyword.isBlank()) {

            brokerPage =
                    brokerRepository
                            .findByDeletedFalse(
                                    pageable
                            );

        } else {

            brokerPage =
                    brokerRepository
                            .findByDeletedFalseAndBrokerNameContainingIgnoreCaseOrDeletedFalseAndCompanyNameContainingIgnoreCaseOrDeletedFalseAndEmailContainingIgnoreCase(
                                    keyword,
                                    keyword,
                                    keyword,
                                    pageable
                            );
        }

        return brokerPage.map(
                this::mapToDTO
        );
    }

    @Override
    public BrokerListResponseDTO getBrokerById(
            Long id
    ) {

        Broker broker =
                brokerRepository
                        .findById(id)
                        .filter(b -> !b.isDeleted())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Broker not found"
                                )
                        );

        return mapToDTO(broker);
    }

    @Override
    public BrokerListResponseDTO updateBroker(
            Long id,
            BrokerUpdateRequestDTO request
    ) {

        Broker broker =
                brokerRepository
                        .findById(id)
                        .filter(b -> !b.isDeleted())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Broker not found"
                                )
                        );

        broker.setBrokerName(
                request.getBrokerName()
        );

        broker.setCompanyName(
                request.getCompanyName()
        );

        broker.setEmail(
                request.getEmail()
        );

        broker.setMobileNumber(
                request.getMobileNumber()
        );

        broker.setOfficeAddress(
                request.getOfficeAddress()
        );

        Broker updatedBroker =
                brokerRepository.save(broker);

        return mapToDTO(updatedBroker);
    }

    @Override
    public void activateBroker(
            Long id
    ) {

        Broker broker =
                brokerRepository
                        .findById(id)
                        .filter(b -> !b.isDeleted())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Broker not found"
                                )
                        );

        broker.setStatus(
                BrokerStatus.ACTIVE
        );

        brokerRepository.save(broker);
    }

    @Override
    public void deactivateBroker(
            Long id
    ) {

        Broker broker =
                brokerRepository
                        .findById(id)
                        .filter(b -> !b.isDeleted())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Broker not found"
                                )
                        );

        broker.setStatus(
                BrokerStatus.INACTIVE
        );

        brokerRepository.save(broker);
    }

    @Override
    public void deleteBroker(
            Long id
    ) {

        Broker broker =
                brokerRepository
                        .findById(id)
                        .filter(b -> !b.isDeleted())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Broker not found"
                                )
                        );

        broker.setDeleted(true);

        brokerRepository.save(broker);
    }

    // ==========================================================
    // Main Broker -> User Management
    // ==========================================================

    @Override
    public BrokerResponseDTO createUser(
            BrokerUserCreateRequestDTO request
    ) {

        Broker mainBroker =
                getMainBroker();

        if (!mainBroker.getId().equals(
                getCurrentBroker().getId()
        )) {

            throw new RuntimeException(
                    "Only the main broker can manage users"
            );
        }

        if (brokerRepository.existsByUsername(
                request.getUsername()
        )) {

            throw new RuntimeException(
                    "Username already exists"
            );
        }

        if (brokerRepository.existsByEmail(
                request.getEmail()
        )) {

            throw new RuntimeException(
                    "Email already exists"
            );
        }

        if (brokerRepository.existsByMobileNumber(
                request.getMobileNumber()
        )) {

            throw new RuntimeException(
                    "Mobile number already exists"
            );
        }

        Broker user = Broker.builder()
                .brokerName(
                        request.getBrokerName()
                )
                .companyName(
                        mainBroker.getCompanyName()
                )
                .email(
                        request.getEmail()
                )
                .mobileNumber(
                        request.getMobileNumber()
                )
                .username(
                        request.getUsername()
                )
                .password(
                        passwordEncoder.encode(
                                request.getPassword()
                        )
                )
                .officeAddress(
                        mainBroker.getOfficeAddress()
                )
                .role(
                        Role.ROLE_BROKER
                )
                .status(
                        BrokerStatus.ACTIVE
                )
                .deleted(false)
                .parentBroker(
                        mainBroker
                )
                .build();

        Broker savedUser =
                brokerRepository.save(user);

        return mapToBrokerResponse(
                savedUser
        );
    }

    @Override
    public List<BrokerListResponseDTO> getMyUsers() {

        Broker mainBroker =
                getMainBroker();

        ensureMainBroker();

        return brokerRepository
                .findByParentBrokerIdAndDeletedFalse(
                        mainBroker.getId()
                )
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public void activateUser(
            Long userId
    ) {

        Broker mainBroker =
                getMainBroker();

        ensureMainBroker();

        Broker user =
                getUserBelongingToMainBroker(
                        userId,
                        mainBroker
                );

        user.setStatus(
                BrokerStatus.ACTIVE
        );

        brokerRepository.save(user);
    }

    @Override
    public void deactivateUser(
            Long userId
    ) {

        Broker mainBroker =
                getMainBroker();

        ensureMainBroker();

        Broker user =
                getUserBelongingToMainBroker(
                        userId,
                        mainBroker
                );

        user.setStatus(
                BrokerStatus.INACTIVE
        );

        brokerRepository.save(user);
    }

    @Override
    public void deleteUser(
            Long userId
    ) {

        Broker mainBroker =
                getMainBroker();

        ensureMainBroker();

        Broker user =
                getUserBelongingToMainBroker(
                        userId,
                        mainBroker
                );

        user.setDeleted(true);

        brokerRepository.save(user);
    }

    @Override
    public boolean isMainBroker() {

        return getCurrentBroker()
                .getParentBroker() == null;
    }

    // ==========================================================
    // Organization Helpers
    // ==========================================================

    private Broker getCurrentBroker() {

        String username =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        return brokerRepository
                .findByUsernameAndDeletedFalse(
                        username
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Logged-in broker not found"
                        )
                );
    }

    private Broker getMainBroker() {

        Broker currentBroker =
                getCurrentBroker();

        if (currentBroker.getParentBroker() == null) {
            return currentBroker;
        }

        return currentBroker.getParentBroker();
    }

    private void ensureMainBroker() {

        if (!isMainBroker()) {

            throw new RuntimeException(
                    "Only the main broker can manage users"
            );
        }
    }

    private Broker getUserBelongingToMainBroker(
            Long userId,
            Broker mainBroker
    ) {

        return brokerRepository
                .findById(userId)
                .filter(user -> !user.isDeleted())
                .filter(user ->
                        user.getParentBroker() != null
                                && user.getParentBroker()
                                .getId()
                                .equals(mainBroker.getId())
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );
    }

    // ==========================================================
    // DTO Mapping
    // ==========================================================

    private BrokerListResponseDTO mapToDTO(
            Broker broker
    ) {

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

    private BrokerResponseDTO mapToBrokerResponse(
            Broker broker
    ) {

        return BrokerResponseDTO.builder()
                .id(broker.getId())
                .brokerName(broker.getBrokerName())
                .companyName(broker.getCompanyName())
                .email(broker.getEmail())
                .mobileNumber(broker.getMobileNumber())
                .username(broker.getUsername())
                .officeAddress(broker.getOfficeAddress())
                .role(broker.getRole())
                .status(broker.getStatus())
                .build();
    }
}


//package com.rentagreement.service.impl;
//
//import com.rentagreement.dto.broker.BrokerListResponseDTO;
//import com.rentagreement.dto.broker.BrokerUpdateRequestDTO;
//import com.rentagreement.entity.Broker;
//import com.rentagreement.exception.ResourceNotFoundException;
//import com.rentagreement.repository.BrokerRepository;
//import com.rentagreement.service.BrokerService;
//import org.springframework.stereotype.Service;
//
//import com.rentagreement.dto.auth.BrokerRegisterRequestDTO;
//import com.rentagreement.dto.auth.BrokerResponseDTO;
//import com.rentagreement.enums.BrokerStatus;
//import com.rentagreement.enums.Role;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//
//import java.util.List;
//
//@Service
//public class BrokerServiceImpl implements BrokerService {
//
//    private final BrokerRepository brokerRepository;
//    private final PasswordEncoder passwordEncoder;
//
//    public BrokerServiceImpl(BrokerRepository brokerRepository,
//                             PasswordEncoder passwordEncoder) {
//
//        this.brokerRepository = brokerRepository;
//        this.passwordEncoder = passwordEncoder;
//    }
//
//    @Override
//    public BrokerResponseDTO addBroker(BrokerRegisterRequestDTO request) {
//
//        if (brokerRepository.existsByUsername(request.getUsername())) {
//            throw new RuntimeException("Username already exists");
//        }
//
//        if (brokerRepository.existsByEmail(request.getEmail())) {
//            throw new RuntimeException("Email already exists");
//        }
//
//        Broker broker = Broker.builder()
//                .brokerName(request.getBrokerName())
//                .companyName(request.getCompanyName())
//                .email(request.getEmail())
//                .mobileNumber(request.getMobileNumber())
//                .username(request.getUsername())
//                .password(passwordEncoder.encode(request.getPassword()))
//                .officeAddress(request.getOfficeAddress())
//                .role(Role.ROLE_BROKER)
//                .status(BrokerStatus.INACTIVE)
//                .deleted(false)
//                .build();
//
//        Broker savedBroker = brokerRepository.save(broker);
//
//        return BrokerResponseDTO.builder()
//                .id(savedBroker.getId())
//                .brokerName(savedBroker.getBrokerName())
//                .companyName(savedBroker.getCompanyName())
//                .email(savedBroker.getEmail())
//                .mobileNumber(savedBroker.getMobileNumber())
//                .username(savedBroker.getUsername())
//                .officeAddress(savedBroker.getOfficeAddress())
//                .role(savedBroker.getRole())
//                .status(savedBroker.getStatus())
//                .build();
//    }
//
//    @Override
//    public Page<BrokerListResponseDTO> getAllBrokers(
//            int page,
//            int size,
//            String keyword,
//            String sortBy,
//            String direction
//    ) {
//
//        Sort sort = direction.equalsIgnoreCase("asc")
//                ? Sort.by(sortBy).ascending()
//                : Sort.by(sortBy).descending();
//
//        Pageable pageable = PageRequest.of(
//                page,
//                size,
//                sort
//        );
//
//        Page<Broker> brokerPage;
//
//        if (keyword == null || keyword.isBlank()) {
//
//            brokerPage = brokerRepository.findByDeletedFalse(pageable);
//
//        } else {
//
//            brokerPage = brokerRepository
//                    .findByDeletedFalseAndBrokerNameContainingIgnoreCaseOrDeletedFalseAndCompanyNameContainingIgnoreCaseOrDeletedFalseAndEmailContainingIgnoreCase(
//                            keyword,
//                            keyword,
//                            keyword,
//                            pageable
//                    );
//
//        }
//
//        return brokerPage.map(this::mapToDTO);
//
//    }
//
//
//    @Override
//    public BrokerListResponseDTO getBrokerById(Long id) {
//
//        Broker broker = brokerRepository.findById(id)
//                .filter(b -> !b.isDeleted())
//                .orElseThrow(() ->
//                        new ResourceNotFoundException("Broker not found"));
//
//        return mapToDTO(broker);
//
//    }
//
//    @Override
//    public BrokerListResponseDTO updateBroker(Long id,
//                                              BrokerUpdateRequestDTO request) {
//
//        Broker broker = brokerRepository.findById(id)
//                .filter(b -> !b.isDeleted())
//                .orElseThrow(() ->
//                        new ResourceNotFoundException("Broker not found"));
//
//        broker.setBrokerName(request.getBrokerName());
//        broker.setCompanyName(request.getCompanyName());
//        broker.setEmail(request.getEmail());
//        broker.setMobileNumber(request.getMobileNumber());
//        broker.setOfficeAddress(request.getOfficeAddress());
//
//        Broker updatedBroker = brokerRepository.save(broker);
//
//        return mapToDTO(updatedBroker);
//
//    }
//
//    @Override
//    public void activateBroker(Long id) {
//
//        Broker broker = brokerRepository.findById(id)
//                .filter(b -> !b.isDeleted())
//                .orElseThrow(() ->
//                        new ResourceNotFoundException("Broker not found"));
//
//        broker.setStatus(com.rentagreement.enums.BrokerStatus.ACTIVE);
//
//        brokerRepository.save(broker);
//
//    }
//
//    @Override
//    public void deactivateBroker(Long id) {
//
//        Broker broker = brokerRepository.findById(id)
//                .filter(b -> !b.isDeleted())
//                .orElseThrow(() ->
//                        new ResourceNotFoundException("Broker not found"));
//
//        broker.setStatus(com.rentagreement.enums.BrokerStatus.INACTIVE);
//
//        brokerRepository.save(broker);
//
//    }
//
//    @Override
//    public void deleteBroker(Long id) {
//
//        Broker broker = brokerRepository.findById(id)
//                .filter(b -> !b.isDeleted())
//                .orElseThrow(() ->
//                        new ResourceNotFoundException("Broker not found"));
//
//        broker.setDeleted(true);
//
//        brokerRepository.save(broker);
//
//    }
//
//    private BrokerListResponseDTO mapToDTO(Broker broker) {
//
//        return BrokerListResponseDTO.builder()
//                .id(broker.getId())
//                .brokerName(broker.getBrokerName())
//                .companyName(broker.getCompanyName())
//                .email(broker.getEmail())
//                .mobileNumber(broker.getMobileNumber())
//                .username(broker.getUsername())
//                .officeAddress(broker.getOfficeAddress())
//                .status(broker.getStatus())
//                .build();
//
//    }
//
//}