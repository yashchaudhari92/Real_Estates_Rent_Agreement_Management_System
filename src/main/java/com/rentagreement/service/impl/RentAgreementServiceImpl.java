package com.rentagreement.service.impl;

import com.rentagreement.dto.agreement.AgreementRequestDTO;
import com.rentagreement.dto.agreement.AgreementResponseDTO;
import com.rentagreement.dto.agreement.AgreementUpdateRequestDTO;
import com.rentagreement.service.RentAgreementService;
import com.rentagreement.entity.Building;
import com.rentagreement.entity.RentAgreement;
import com.rentagreement.enums.CommercialCategory;
import com.rentagreement.enums.PropertyType;
import com.rentagreement.exception.ResourceNotFoundException;
import com.rentagreement.repository.BuildingRepository;
import com.rentagreement.repository.RentAgreementRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.rentagreement.entity.Broker;
import com.rentagreement.repository.BrokerRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import com.rentagreement.repository.AgreementDocumentRepository;
import com.rentagreement.dto.agreement.AgreementRenewalRequestDTO;

import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RentAgreementServiceImpl implements RentAgreementService {

    private final RentAgreementRepository agreementRepository;

    private final BuildingRepository buildingRepository;

    private final BrokerRepository brokerRepository;

    private final AgreementDocumentRepository documentRepository;

    private final PasswordEncoder passwordEncoder;

    public RentAgreementServiceImpl(
            RentAgreementRepository agreementRepository,
            BuildingRepository buildingRepository,
            BrokerRepository brokerRepository,
            AgreementDocumentRepository documentRepository,
            PasswordEncoder passwordEncoder
    ) {

        this.agreementRepository = agreementRepository;
        this.buildingRepository = buildingRepository;
        this.brokerRepository = brokerRepository;
        this.documentRepository = documentRepository;
        this.passwordEncoder = passwordEncoder;

    }

    @Override
    public AgreementResponseDTO createAgreement(
            AgreementRequestDTO request
    ) {

        // Broker broker = getCurrentBroker();
        Broker currentBroker = getCurrentBroker();
        Broker broker = getMainBroker();

        Building building =
                buildingRepository
                        .findByIdAndBrokerIdAndDeletedFalse(
                                request.getBuildingId(),
                                broker.getId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Building not found"
                                ));

        validatePropertyDetails(building, request);

        RentAgreement agreement = RentAgreement.builder()

                .building(building)

                .createdByBroker(currentBroker)

                // Owner
                .ownerName(request.getOwnerName())
                .ownerMobile(request.getOwnerMobile())
                .ownerEmail(request.getOwnerEmail())

                // Tenant
                .tenantName(request.getTenantName())
                .tenantMobile(request.getTenantMobile())
                .tenantEmail(request.getTenantEmail())

                // Agreement
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .deposit(request.getDeposit())
                .monthlyRent(request.getMonthlyRent())
                .feesPaid(request.getFeesPaid())

                // Residential
                .bhk(request.getBhk())
                .wing(request.getWing())
                .floor(request.getFloor())
                .flatNumber(request.getFlatNumber())

                // Commercial
                .area(request.getArea())
                .commercialCategory(

                        request.getCommercialCategory() == null

                                ? null

                                : CommercialCategory.valueOf(
                                request.getCommercialCategory()
                        )

                )

                .build();

        if (building.getPropertyType() == PropertyType.RESIDENTIAL) {

            agreement.setArea(null);
            agreement.setCommercialCategory(null);

        } else {

            agreement.setBhk(null);
            agreement.setWing(null);
            agreement.setFloor(null);
            agreement.setFlatNumber(null);

        }

        RentAgreement savedAgreement =
                agreementRepository.save(agreement);

        return mapToDTO(savedAgreement);

    }

    private Broker getCurrentBroker() {

        String username =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        return brokerRepository
                .findByUsernameAndDeletedFalse(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Logged-in broker not found"
                        ));
    }

    @Override
    public Page<AgreementResponseDTO> getAllAgreements(
            String keyword,
            Pageable pageable
    ) {

        // Broker broker = getCurrentBroker();
        Broker broker = getMainBroker();

        if (keyword != null &&
                !keyword.trim().isEmpty()) {

            return agreementRepository
                    .findCurrentAgreementsByBrokerIdAndOwnerName(
                            broker.getId(),
                            keyword,
                            pageable
                    )
                    .map(this::mapToDTO);

        }

        return agreementRepository
                .findCurrentAgreementsByBrokerId(
                        broker.getId(),
                        pageable
                )
                .map(this::mapToDTO);

    }

    @Override
    public AgreementResponseDTO getAgreementById(
            Long id
    ) {

        // Broker broker = getCurrentBroker();
        Broker broker = getMainBroker();

        RentAgreement agreement =
                agreementRepository
                        .findByIdAndBuildingBrokerIdAndDeletedFalse(
                                id,
                                broker.getId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Agreement not found"
                                ));

        return mapToDTO(agreement);

    }

    @Override
    @Transactional(readOnly = true)
    public List<AgreementResponseDTO> getAgreementHistory(
            Long id
    ) {

//        RentAgreement currentAgreement =
//                agreementRepository.findById(id)
//                        .filter(a -> !a.isDeleted())
//                        .orElseThrow(() ->
//                                new ResourceNotFoundException(
//                                        "Agreement not found"
//                                )
//                        );

        Broker mainBroker = getMainBroker();

        RentAgreement currentAgreement =
                agreementRepository
                        .findByIdAndBuildingBrokerIdAndDeletedFalse(
                                id,
                                mainBroker.getId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Agreement not found"
                                )
                        );

        List<AgreementResponseDTO> history =
                new ArrayList<>();

        Set<Long> visitedIds =
                new HashSet<>();

        RentAgreement previousAgreement =
                currentAgreement.getPreviousAgreement();

        while (previousAgreement != null) {

            // Prevent accidental circular relationship
            if (!visitedIds.add(previousAgreement.getId())) {
                break;
            }

            if (!previousAgreement.isDeleted()) {

                history.add(
                        mapToDTO(previousAgreement)
                );

            }

            previousAgreement =
                    previousAgreement.getPreviousAgreement();
        }

        return history;
    }

    @Override
    @Transactional
    public AgreementResponseDTO renewAgreement(
            Long id,
            AgreementRenewalRequestDTO request
    ) {

        // Broker currentBroker = getCurrentBroker();
        Broker currentBroker = getMainBroker();

        // ==========================================
        // Find Existing Agreement
        // ==========================================

        RentAgreement oldAgreement =
                agreementRepository
                        .findByIdAndBuildingBrokerIdAndDeletedFalse(
                                id,
                                currentBroker.getId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Agreement not found"
                                )
                        );

        // ==========================================
        // Validate Renewal Dates
        // ==========================================

        if (request.getEndDate()
                .isBefore(request.getStartDate())) {

            throw new IllegalArgumentException(
                    "End date cannot be before start date."
            );
        }

        if (!request.getStartDate()
                .isAfter(oldAgreement.getEndDate())) {

            throw new IllegalArgumentException(
                    "Renewal start date must be after the current agreement end date."
            );
        }

        // ==========================================
        // Create New Agreement
        // ==========================================

        RentAgreement newAgreement =
                RentAgreement.builder()

                        // Building
                        .building(
                                oldAgreement.getBuilding()
                        )

                        // Link to Previous Agreement
                        .previousAgreement(
                                oldAgreement
                        )

                        // Owner
                        .ownerName(
                                oldAgreement.getOwnerName()
                        )
                        .ownerMobile(
                                oldAgreement.getOwnerMobile()
                        )
                        .ownerEmail(
                                oldAgreement.getOwnerEmail()
                        )

                        // Tenant
                        .tenantName(
                                oldAgreement.getTenantName()
                        )
                        .tenantMobile(
                                oldAgreement.getTenantMobile()
                        )
                        .tenantEmail(
                                oldAgreement.getTenantEmail()
                        )

                        // New Agreement Details
                        .startDate(
                                request.getStartDate()
                        )
                        .endDate(
                                request.getEndDate()
                        )
                        .deposit(
                                request.getDeposit()
                        )
                        .monthlyRent(
                                request.getMonthlyRent()
                        )
                        .feesPaid(
                                request.getFeesPaid()
                        )

                        // Residential
                        .bhk(
                                oldAgreement.getBhk()
                        )
                        .wing(
                                oldAgreement.getWing()
                        )
                        .floor(
                                oldAgreement.getFloor()
                        )
                        .flatNumber(
                                oldAgreement.getFlatNumber()
                        )

                        // Commercial
                        .area(
                                oldAgreement.getArea()
                        )
                        .commercialCategory(
                                oldAgreement.getCommercialCategory()
                        )

                        .build();

        // ==========================================
        // Save New Agreement
        // ==========================================

        RentAgreement renewedAgreement =
                agreementRepository.save(
                        newAgreement
                );

        // ==========================================
        // Return New Agreement
        // ==========================================

        return mapToDTO(
                renewedAgreement
        );
    }

    @Override
    public AgreementResponseDTO updateAgreement(
            Long id,
            AgreementUpdateRequestDTO request
    ) {

        // Broker currentBroker = getCurrentBroker();
        Broker currentBroker = getMainBroker();

        RentAgreement agreement =
                agreementRepository
                        .findByIdAndBuildingBrokerIdAndDeletedFalse(
                                id,
                                currentBroker.getId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Agreement not found"
                                ));

        Building building =
                buildingRepository
                        .findByIdAndBrokerIdAndDeletedFalse(
                                request.getBuildingId(),
                                currentBroker.getId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Building not found"
                                ));

        validatePropertyDetails(building, request);

        agreement.setBuilding(building);

        // Owner
        agreement.setOwnerName(request.getOwnerName());
        agreement.setOwnerMobile(request.getOwnerMobile());
        agreement.setOwnerEmail(request.getOwnerEmail());

        // Tenant
        agreement.setTenantName(request.getTenantName());
        agreement.setTenantMobile(request.getTenantMobile());
        agreement.setTenantEmail(request.getTenantEmail());

        // Agreement
        agreement.setStartDate(request.getStartDate());
        agreement.setEndDate(request.getEndDate());
        agreement.setDeposit(request.getDeposit());
        agreement.setMonthlyRent(request.getMonthlyRent());
        agreement.setFeesPaid(request.getFeesPaid());

        // Residential
        agreement.setBhk(request.getBhk());
        agreement.setWing(request.getWing());
        agreement.setFloor(request.getFloor());
        agreement.setFlatNumber(request.getFlatNumber());

        // Commercial
        agreement.setArea(request.getArea());

        agreement.setCommercialCategory(

                request.getCommercialCategory() == null

                        ? null

                        : CommercialCategory.valueOf(
                        request.getCommercialCategory()
                )

        );

        if (building.getPropertyType() == PropertyType.RESIDENTIAL) {

            agreement.setArea(null);
            agreement.setCommercialCategory(null);

        } else {

            agreement.setBhk(null);
            agreement.setWing(null);
            agreement.setFloor(null);
            agreement.setFlatNumber(null);

        }

        RentAgreement updatedAgreement =
                agreementRepository.save(agreement);

        return mapToDTO(updatedAgreement);

    }

    @Override
    public void deleteAgreement(
            Long id,
            String deletePassword
    ) {

        Broker currentBroker = getCurrentBroker();

        /*
         * Only Main Broker can delete agreements.
         */
        if (currentBroker.getParentBroker() != null) {

            throw new IllegalArgumentException(
                    "You cannot delete any agreement. These agreements are only deleted by your Main Admin."
            );
        }

        /*
         * Check whether main broker has created
         * a separate delete password.
         */
        if (currentBroker.getDeletePassword() == null
                || currentBroker.getDeletePassword().isBlank()) {

            throw new IllegalArgumentException(
                    "Please create your agreement delete password first"
            );
        }

        /*
         * Verify the separate delete password.
         */
        if (!passwordEncoder.matches(
                deletePassword,
                currentBroker.getDeletePassword()
        )) {

            throw new IllegalArgumentException(
                    "Invalid agreement delete password"
            );
        }

        /*
         * Existing agreement ownership logic remains unchanged.
         */
        RentAgreement agreement =
                agreementRepository
                        .findByIdAndBuildingBrokerIdAndDeletedFalse(
                                id,
                                currentBroker.getId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Agreement not found"
                                )
                        );

        /*
         * Existing soft-delete logic remains unchanged.
         */
        agreement.setDeleted(true);

        agreementRepository.save(agreement);
    }

//    @Override
//    public void deleteAgreement(
//            Long id,
//            String deletePassword
//    ) {
//
//        Broker broker = getCurrentBroker();
//
//        /*
//         * Check whether broker has created
//         * a separate delete password.
//         */
//        if (broker.getDeletePassword() == null
//                || broker.getDeletePassword().isBlank()) {
//
//            throw new IllegalArgumentException(
//                    "Please create your agreement delete password first"
//            );
//        }
//
//        /*
//         * Verify the separate delete password.
//         *
//         * Login password is NOT used here.
//         */
//        if (!passwordEncoder.matches(
//                deletePassword,
//                broker.getDeletePassword()
//        )) {
//
//            throw new IllegalArgumentException(
//                    "Invalid agreement delete password"
//            );
//        }
//
//        /*
//         * Find agreement belonging to
//         * the currently logged-in broker.
//         */
////
//        Broker mainBroker = getMainBroker();
//
//        RentAgreement agreement =
//                agreementRepository
//                        .findByIdAndBuildingBrokerIdAndDeletedFalse(
//                                id,
//                                mainBroker.getId()
//                        )
//                        .orElseThrow(() ->
//                                new ResourceNotFoundException(
//                                        "Agreement not found"
//                                )
//                        );
//
//        /*
//         * Existing soft-delete logic remains unchanged.
//         */
//        agreement.setDeleted(true);
//
//        agreementRepository.save(agreement);
//
//    }

    private Broker getMainBroker() {

        Broker currentBroker =
                getCurrentBroker();

        if (currentBroker.getParentBroker() == null) {
            return currentBroker;
        }

        return currentBroker.getParentBroker();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AgreementResponseDTO> getAgreementsByUser(
            Long userId
    ) {

        Broker currentBroker = getCurrentBroker();

        /*
         * Only Main Broker can view
         * agreements by sub user.
         */
        if (currentBroker.getParentBroker() != null) {

            throw new IllegalArgumentException(
                    "Only Main Broker can view user agreements"
            );
        }

        /*
         * Verify that selected user belongs
         * to the logged-in Main Broker.
         */
        Broker user =
                brokerRepository
                        .findById(userId)
                        .filter(broker ->
                                !broker.isDeleted()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found"
                                )
                        );

        if (user.getParentBroker() == null
                || !user.getParentBroker()
                .getId()
                .equals(currentBroker.getId())) {

            throw new IllegalArgumentException(
                    "User does not belong to your organization"
            );
        }

        return agreementRepository
                .findCurrentAgreementsByCreatedByBrokerId(
                        currentBroker.getId(),
                        userId
                )
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    private void validatePropertyDetails(
            Building building,
            AgreementRequestDTO request
    ) {

        if (building.getPropertyType() == PropertyType.RESIDENTIAL) {

            if (request.getBhk() == null
                    || request.getWing() == null
                    || request.getWing().isBlank()
                    || request.getFloor() == null
                    || request.getFlatNumber() == null
                    || request.getFlatNumber().isBlank()) {

                throw new IllegalArgumentException(
                        "Residential details are required."
                );
            }

        } else {

            if (request.getArea() == null
                    || request.getCommercialCategory() == null
                    || request.getCommercialCategory().isBlank()) {

                throw new IllegalArgumentException(
                        "Commercial details are required."
                );
            }

        }

    }

    private void validatePropertyDetails(
            Building building,
            AgreementUpdateRequestDTO request
    ) {

        if (building.getPropertyType() == PropertyType.RESIDENTIAL) {

            if (request.getBhk() == null
                    || request.getWing() == null
                    || request.getWing().isBlank()
                    || request.getFloor() == null
                    || request.getFlatNumber() == null
                    || request.getFlatNumber().isBlank()) {

                throw new IllegalArgumentException(
                        "Residential details are required."
                );
            }

        } else {

            if (request.getArea() == null
                    || request.getCommercialCategory() == null
                    || request.getCommercialCategory().isBlank()) {

                throw new IllegalArgumentException(
                        "Commercial details are required."
                );
            }

        }

    }

    private AgreementResponseDTO mapToDTO(
            RentAgreement agreement
    ) {

        return AgreementResponseDTO.builder()

                .id(agreement.getId())

                .buildingId(
                        agreement.getBuilding().getId()
                )

                .buildingName(
                        agreement.getBuilding().getBuildingName()
                )

                .propertyType(
                        agreement.getBuilding().getPropertyType()
                )

                // Owner
                .ownerName(agreement.getOwnerName())
                .ownerMobile(agreement.getOwnerMobile())
                .ownerEmail(agreement.getOwnerEmail())

                // Tenant
                .tenantName(agreement.getTenantName())
                .tenantMobile(agreement.getTenantMobile())
                .tenantEmail(agreement.getTenantEmail())

                // Agreement
                .startDate(agreement.getStartDate())
                .endDate(agreement.getEndDate())
                .deposit(agreement.getDeposit())
                .monthlyRent(agreement.getMonthlyRent())
                .feesPaid((agreement.getFeesPaid()))

                // Residential
                .bhk(agreement.getBhk())
                .wing(agreement.getWing())
                .floor(agreement.getFloor())
                .flatNumber(agreement.getFlatNumber())

                // Commercial
                .area(agreement.getArea())
                .commercialCategory(
                        agreement.getCommercialCategory()
                )

                // Document
                .documentName(
                        documentRepository
                                .findByAgreementId(agreement.getId())
                                .map(document ->
                                        document.getFileName()
                                )
                                .orElse(null)
                )

                // Audit
                .createdDate(
                        agreement.getCreatedDate()
                )

                .build();

    }
}