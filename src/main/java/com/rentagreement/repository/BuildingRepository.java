package com.rentagreement.repository;

import com.rentagreement.entity.Building;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BuildingRepository
        extends JpaRepository<Building, Long> {

    Page<Building> findByBrokerIdAndDeletedFalse(
            Long brokerId,
            Pageable pageable
    );

    @Query("""
            SELECT b
            FROM Building b
            WHERE b.broker.id = :brokerId
              AND b.deleted = false
              AND (
                    LOWER(b.buildingName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(b.location) LIKE LOWER(CONCAT('%', :keyword, '%'))
                  )
            """)
    Page<Building> findByBrokerIdAndKeyword(
            @Param("brokerId") Long brokerId,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    java.util.Optional<Building> findByIdAndBrokerIdAndDeletedFalse(
            Long id,
            Long brokerId
    );

    long countByBrokerIdAndDeletedFalse(Long brokerId);

}


//package com.rentagreement.repository;
//
//import com.rentagreement.entity.Building;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//public interface BuildingRepository
//        extends JpaRepository<Building, Long> {
//
//    Page<Building> findByBrokerIdAndDeletedFalse(
//            Long brokerId,
//            Pageable pageable
//    );
//
//    Page<Building> findByBrokerIdAndBuildingNameContainingIgnoreCaseAndDeletedFalse(
//            Long brokerId,
//            String buildingName,
//            Pageable pageable
//    );
//
//    java.util.Optional<Building> findByIdAndBrokerIdAndDeletedFalse(
//            Long id,
//            Long brokerId
//    );
//
//    long countByBrokerIdAndDeletedFalse(Long brokerId);
//
//}
