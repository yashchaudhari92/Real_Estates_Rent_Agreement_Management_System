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
import org.springframework.stereotype.Service;
import com.rentagreement.entity.Broker;
import com.rentagreement.repository.BrokerRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import com.rentagreement.repository.AgreementDocumentRepository;

@Service
public class RentAgreementServiceImpl implements RentAgreementService {

    private final RentAgreementRepository agreementRepository;

    private final BuildingRepository buildingRepository;

    private final BrokerRepository brokerRepository;

    private final AgreementDocumentRepository documentRepository;

    public RentAgreementServiceImpl(
            RentAgreementRepository agreementRepository,
            BuildingRepository buildingRepository,
            BrokerRepository brokerRepository,
            AgreementDocumentRepository documentRepository
    ) {

        this.agreementRepository = agreementRepository;
        this.buildingRepository = buildingRepository;
        this.brokerRepository = brokerRepository;
        this.documentRepository = documentRepository;

    }

    @Override
    public AgreementResponseDTO createAgreement(
            AgreementRequestDTO request
    ) {

        Broker broker = getCurrentBroker();

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

        Broker broker = getCurrentBroker();

        if (keyword != null &&
                !keyword.trim().isEmpty()) {

            return agreementRepository
                    .findByBuildingBrokerIdAndOwnerNameContainingIgnoreCaseAndDeletedFalse(
                            broker.getId(),
                            keyword,
                            pageable
                    )
                    .map(this::mapToDTO);

        }

        return agreementRepository
                .findByBuildingBrokerIdAndDeletedFalse(
                        broker.getId(),
                        pageable
                )
                .map(this::mapToDTO);

    }

    @Override
    public AgreementResponseDTO getAgreementById(
            Long id
    ) {

        Broker broker = getCurrentBroker();

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
    public AgreementResponseDTO updateAgreement(
            Long id,
            AgreementUpdateRequestDTO request
    ) {

        Broker currentBroker = getCurrentBroker();

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
            Long id
    ) {

        Broker broker = getCurrentBroker();

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

        agreement.setDeleted(true);

        agreementRepository.save(agreement);

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