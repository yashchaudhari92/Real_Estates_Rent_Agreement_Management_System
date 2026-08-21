package com.rentagreement.service.impl;

import com.rentagreement.dto.agreement.AgreementResponseDTO;
import com.rentagreement.dto.dashboard.BrokerDashboardResponseDTO;
import com.rentagreement.entity.Broker;
import com.rentagreement.entity.RentAgreement;
import com.rentagreement.repository.BrokerRepository;
import com.rentagreement.repository.BuildingRepository;
import com.rentagreement.repository.RentAgreementRepository;
import com.rentagreement.service.BrokerDashboardService;
import com.rentagreement.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.List;

@Service
public class BrokerDashboardServiceImpl
        implements BrokerDashboardService {

    private final BuildingRepository buildingRepository;

    private final RentAgreementRepository agreementRepository;

    private final BrokerRepository brokerRepository;


    public BrokerDashboardServiceImpl(
            BuildingRepository buildingRepository,
            RentAgreementRepository agreementRepository,
            BrokerRepository brokerRepository
    ) {

        this.buildingRepository = buildingRepository;

        this.agreementRepository = agreementRepository;

        this.brokerRepository = brokerRepository;

    }


    @Override
    public BrokerDashboardResponseDTO getDashboardStats() {

        Broker broker = getCurrentBroker();

        Long brokerId = broker.getId();

        LocalDate today = LocalDate.now();

        LocalDate expiringSoonDate =
                today.plusDays(30);


        // Total Buildings

        long totalBuildings =
                buildingRepository
                        .countByBrokerIdAndDeletedFalse(
                                brokerId
                        );


        // Total Agreements

        long totalAgreements =
                agreementRepository
                        .countByBuildingBrokerIdAndDeletedFalse(
                                brokerId
                        );


        // Active Agreements

        long activeAgreements =
                agreementRepository
                        .countByBuildingBrokerIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndDeletedFalse(
                                brokerId,
                                today,
                                today
                        );


        // Expiring Soon

        long expiringSoon =
                agreementRepository
                        .countByBuildingBrokerIdAndStartDateLessThanEqualAndEndDateGreaterThanAndEndDateLessThanEqualAndDeletedFalse(
                                brokerId,
                                today,
                                today,
                                expiringSoonDate
                        );


        // Expired Agreements

        long expiredAgreements =
                agreementRepository
                        .countByBuildingBrokerIdAndEndDateLessThanEqualAndDeletedFalse(
                                brokerId,
                                today
                        );


        // Recent Agreements

        Page<AgreementResponseDTO> recentPage =
                agreementRepository
                        .findByBuildingBrokerIdAndDeletedFalseOrderByIdDesc(
                                brokerId,
                                PageRequest.of(0, 5)
                        )
                        .map(this::mapToDTO);


        List<AgreementResponseDTO> recentAgreements =
                recentPage.getContent();


        return BrokerDashboardResponseDTO.builder()

                .brokerName(broker.getBrokerName())

                .companyName(broker.getCompanyName())

                .totalBuildings(totalBuildings)

                .totalAgreements(totalAgreements)

                .activeAgreements(activeAgreements)

                .expiringSoon(expiringSoon)

                .expiredAgreements(expiredAgreements)

                .recentAgreements(recentAgreements)

                .build();

    }


    @Override
    public List<AgreementResponseDTO> getExpiringAgreementsByMonth(
            int year,
            int month
    ) {

        if (month < 1 || month > 12) {

            throw new IllegalArgumentException(
                    "Month must be between 1 and 12"
            );

        }

        Broker broker = getCurrentBroker();

        LocalDate monthStart =
                LocalDate.of(
                        year,
                        month,
                        1
                );

        LocalDate monthEnd =
                monthStart
                        .withDayOfMonth(
                                monthStart.lengthOfMonth()
                        );

        LocalDate today = LocalDate.now();

        return agreementRepository
                .findExpiringAgreementsByBrokerIdAndMonth(
                        broker.getId(),
                        today,
                        monthStart,
                        monthEnd
                )
                .stream()
                .map(this::mapToDTO)
                .toList();

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
                .ownerName(
                        agreement.getOwnerName()
                )

                .ownerMobile(
                        agreement.getOwnerMobile()
                )

                .ownerEmail(
                        agreement.getOwnerEmail()
                )

                // Tenant
                .tenantName(
                        agreement.getTenantName()
                )

                .tenantMobile(
                        agreement.getTenantMobile()
                )

                .tenantEmail(
                        agreement.getTenantEmail()
                )

                // Agreement
                .startDate(
                        agreement.getStartDate()
                )

                .endDate(
                        agreement.getEndDate()
                )

                .deposit(
                        agreement.getDeposit()
                )

                .monthlyRent(
                        agreement.getMonthlyRent()
                )

                // Residential
                .bhk(
                        agreement.getBhk()
                )

                .wing(
                        agreement.getWing()
                )

                .floor(
                        agreement.getFloor()
                )

                .flatNumber(
                        agreement.getFlatNumber()
                )

                // Commercial
                .area(
                        agreement.getArea()
                )

                .commercialCategory(
                        agreement.getCommercialCategory()
                )

                // Document
                .documentName(
                        agreement.getDocumentName()
                )

                // Audit
                .createdDate(
                        agreement.getCreatedDate()
                )

                .build();

    }

}


//package com.rentagreement.service.impl;
//
//import com.rentagreement.dto.agreement.AgreementResponseDTO;
//import com.rentagreement.dto.dashboard.BrokerDashboardResponseDTO;
//import com.rentagreement.entity.Broker;
//import com.rentagreement.repository.BrokerRepository;
//import com.rentagreement.repository.BuildingRepository;
//import com.rentagreement.repository.RentAgreementRepository;
//import com.rentagreement.service.BrokerDashboardService;
//import com.rentagreement.exception.ResourceNotFoundException;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.stereotype.Service;
//import org.springframework.security.core.context.SecurityContextHolder;
//
//import java.time.LocalDate;
//import java.util.List;
//
//@Service
//public class BrokerDashboardServiceImpl
//        implements BrokerDashboardService {
//
//    private final BuildingRepository buildingRepository;
//
//    private final RentAgreementRepository agreementRepository;
//
//    private final BrokerRepository brokerRepository;
//
//
//    public BrokerDashboardServiceImpl(
//            BuildingRepository buildingRepository,
//            RentAgreementRepository agreementRepository,
//            BrokerRepository brokerRepository
//    ) {
//
//        this.buildingRepository = buildingRepository;
//
//        this.agreementRepository = agreementRepository;
//
//        this.brokerRepository = brokerRepository;
//
//    }
//
//
//    @Override
//    public BrokerDashboardResponseDTO getDashboardStats() {
//
//        Broker broker = getCurrentBroker();
//
//        Long brokerId = broker.getId();
//
//        LocalDate today = LocalDate.now();
//
//        LocalDate expiringSoonDate =
//                today.plusDays(30);
//
//
//        // Total Buildings
//
//        long totalBuildings =
//                buildingRepository
//                        .countByBrokerIdAndDeletedFalse(
//                                brokerId
//                        );
//
//
//        // Total Agreements
//
//        long totalAgreements =
//                agreementRepository
//                        .countByBuildingBrokerIdAndDeletedFalse(
//                                brokerId
//                        );
//
//
//        // Active Agreements
//
//        long activeAgreements =
//                agreementRepository
//                        .countByBuildingBrokerIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndDeletedFalse(
//                                brokerId,
//                                today,
//                                today
//                        );
//
//
//        // Expiring Soon
//
//        long expiringSoon =
//                agreementRepository
//                        .countByBuildingBrokerIdAndStartDateLessThanEqualAndEndDateGreaterThanAndEndDateLessThanEqualAndDeletedFalse(
//                                brokerId,
//                                today,
//                                today,
//                                expiringSoonDate
//                        );
//
//
//        // Expired Agreements
//
//        long expiredAgreements =
//                agreementRepository
//                        .countByBuildingBrokerIdAndEndDateBeforeAndDeletedFalse(
//                                brokerId,
//                                today
//                        );
//
//
//        // Recent Agreements
//
//        Page<AgreementResponseDTO> recentPage =
//                agreementRepository
//                        .findByBuildingBrokerIdAndDeletedFalseOrderByIdDesc(
//                                brokerId,
//                                PageRequest.of(0, 5)
//                        )
//                        .map(this::mapToDTO);
//
//
//        List<AgreementResponseDTO> recentAgreements =
//                recentPage.getContent();
//
//
//        return BrokerDashboardResponseDTO.builder()
//
//                .brokerName(broker.getBrokerName())
//
//                .companyName(broker.getCompanyName())
//
//                .totalBuildings(totalBuildings)
//
//                .totalAgreements(totalAgreements)
//
//                .activeAgreements(activeAgreements)
//
//                .expiringSoon(expiringSoon)
//
//                .expiredAgreements(expiredAgreements)
//
//                .recentAgreements(recentAgreements)
//
//                .build();
//
//    }
//
//
//    private Broker getCurrentBroker() {
//
//        String username =
//                SecurityContextHolder
//                        .getContext()
//                        .getAuthentication()
//                        .getName();
//
//
//        return brokerRepository
//                .findByUsernameAndDeletedFalse(username)
//                .orElseThrow(() ->
//                        new ResourceNotFoundException(
//                                "Logged-in broker not found"
//                        ));
//
//    }
//
//
//    private AgreementResponseDTO mapToDTO(
//            com.rentagreement.entity.RentAgreement agreement
//    ) {
//
//        return AgreementResponseDTO.builder()
//
//                .id(agreement.getId())
//
//                .buildingId(
//                        agreement.getBuilding().getId()
//                )
//
//                .buildingName(
//                        agreement.getBuilding().getBuildingName()
//                )
//
//                .propertyType(
//                        agreement.getBuilding().getPropertyType()
//                )
//
//                // Owner
//                .ownerName(
//                        agreement.getOwnerName()
//                )
//
//                .ownerMobile(
//                        agreement.getOwnerMobile()
//                )
//
//                .ownerEmail(
//                        agreement.getOwnerEmail()
//                )
//
//
//                // Tenant
//                .tenantName(
//                        agreement.getTenantName()
//                )
//
//                .tenantMobile(
//                        agreement.getTenantMobile()
//                )
//
//                .tenantEmail(
//                        agreement.getTenantEmail()
//                )
//
//
//                // Agreement
//                .startDate(
//                        agreement.getStartDate()
//                )
//
//                .endDate(
//                        agreement.getEndDate()
//                )
//
//                .deposit(
//                        agreement.getDeposit()
//                )
//
//                .monthlyRent(
//                        agreement.getMonthlyRent()
//                )
//
//
//                // Residential
//                .bhk(
//                        agreement.getBhk()
//                )
//
//                .wing(
//                        agreement.getWing()
//                )
//
//                .floor(
//                        agreement.getFloor()
//                )
//
//                .flatNumber(
//                        agreement.getFlatNumber()
//                )
//
//
//                // Commercial
//                .area(
//                        agreement.getArea()
//                )
//
//                .commercialCategory(
//                        agreement.getCommercialCategory()
//                )
//
//
//                // Document
//                .documentName(
//                        agreement.getDocumentName()
//                )
//
//
//                // Audit
//                .createdDate(
//                        agreement.getCreatedDate()
//                )
//
//                .build();
//
//    }
//
//}