package com.rentagreement.repository;

import com.rentagreement.entity.RentAgreement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RentAgreementRepository
        extends JpaRepository<RentAgreement, Long> {

    Page<RentAgreement>
    findByBuildingBrokerIdAndDeletedFalse(
            Long brokerId,
            Pageable pageable
    );

    Page<RentAgreement>
    findByBuildingBrokerIdAndOwnerNameContainingIgnoreCaseAndDeletedFalse(
            Long brokerId,
            String ownerName,
            Pageable pageable
    );

    Optional<RentAgreement>
    findByIdAndBuildingBrokerIdAndDeletedFalse(
            Long id,
            Long brokerId
    );


    // Broker Dashboard Queries
    long countByBuildingBrokerIdAndDeletedFalse(
            Long brokerId
    );

    long countByBuildingBrokerIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndDeletedFalse(
            Long brokerId,
            LocalDate startDate,
            LocalDate endDate
    );

    long countByBuildingBrokerIdAndStartDateLessThanEqualAndEndDateGreaterThanAndEndDateLessThanEqualAndDeletedFalse(
            Long brokerId,
            LocalDate startDate,
            LocalDate endDateStart,
            LocalDate endDateEnd
    );

    long countByBuildingBrokerIdAndEndDateBeforeAndDeletedFalse(
            Long brokerId,
            LocalDate endDate
    );

    Page<RentAgreement> findByBuildingBrokerIdAndDeletedFalseOrderByIdDesc(
            Long brokerId,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {
            "building",
            "building.broker"
    })
    List<RentAgreement> findByEndDateAndDeletedFalse(
            LocalDate endDate
    );

}
