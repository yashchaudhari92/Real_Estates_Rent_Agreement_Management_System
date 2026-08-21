package com.rentagreement.repository;

import com.rentagreement.entity.RentAgreement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RentAgreementRepository
        extends JpaRepository<RentAgreement, Long> {


    // ==========================================================
    // Main Agreement List
    // Show only CURRENT agreements.
    // Exclude agreements which have already been renewed.
    // ==========================================================

    @Query("""
            SELECT a
            FROM RentAgreement a
            WHERE a.building.broker.id = :brokerId
              AND a.deleted = false
              AND NOT EXISTS (
                    SELECT 1
                    FROM RentAgreement newer
                    WHERE newer.previousAgreement.id = a.id
                      AND newer.deleted = false
              )
            """)
    Page<RentAgreement> findCurrentAgreementsByBrokerId(
            @Param("brokerId") Long brokerId,
            Pageable pageable
    );


    // ==========================================================
    // Main Agreement Search
    // Show only CURRENT agreements.
    // ==========================================================

    @Query("""
            SELECT a
            FROM RentAgreement a
            WHERE a.building.broker.id = :brokerId
              AND a.ownerName LIKE CONCAT('%', :ownerName, '%')
              AND a.deleted = false
              AND NOT EXISTS (
                    SELECT 1
                    FROM RentAgreement newer
                    WHERE newer.previousAgreement.id = a.id
                      AND newer.deleted = false
              )
            """)
    Page<RentAgreement> findCurrentAgreementsByBrokerIdAndOwnerName(
            @Param("brokerId") Long brokerId,
            @Param("ownerName") String ownerName,
            Pageable pageable
    );


    // ==========================================================
    // Get Agreement By ID
    // ==========================================================

    Optional<RentAgreement>
    findByIdAndBuildingBrokerIdAndDeletedFalse(
            Long id,
            Long brokerId
    );


    // ==========================================================
    // Broker Dashboard Queries
    // ==========================================================

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

    long countByBuildingBrokerIdAndEndDateLessThanEqualAndDeletedFalse(
            Long brokerId,
            LocalDate endDate
    );


//    long countByBuildingBrokerIdAndEndDateBeforeAndDeletedFalse(
//            Long brokerId,
//            LocalDate endDate
//    );


    Page<RentAgreement> findByBuildingBrokerIdAndDeletedFalseOrderByIdDesc(
            Long brokerId,
            Pageable pageable
    );

    @Query("""
        SELECT a
        FROM RentAgreement a
        WHERE a.building.broker.id = :brokerId
          AND a.deleted = false
          AND a.endDate >= :today
          AND a.endDate >= :monthStart
          AND a.endDate <= :monthEnd
        ORDER BY a.endDate ASC
        """)
    List<RentAgreement> findExpiringAgreementsByBrokerIdAndMonth(
            @Param("brokerId") Long brokerId,
            @Param("today") LocalDate today,
            @Param("monthStart") LocalDate monthStart,
            @Param("monthEnd") LocalDate monthEnd
    );


    // ==========================================================
    // Agreement Expiry Scheduler
    // ==========================================================

    @EntityGraph(attributePaths = {
            "building",
            "building.broker"
    })
    List<RentAgreement> findByEndDateAndDeletedFalse(
            LocalDate endDate
    );

}