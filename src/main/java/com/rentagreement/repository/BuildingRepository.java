package com.rentagreement.repository;

import com.rentagreement.entity.Building;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BuildingRepository
        extends JpaRepository<Building, Long> {

    Page<Building> findByBrokerIdAndDeletedFalse(
            Long brokerId,
            Pageable pageable
    );

    Page<Building> findByBrokerIdAndBuildingNameContainingIgnoreCaseAndDeletedFalse(
            Long brokerId,
            String buildingName,
            Pageable pageable
    );

    java.util.Optional<Building> findByIdAndBrokerIdAndDeletedFalse(
            Long id,
            Long brokerId
    );

}


//package com.rentagreement.repository;
//
//import com.rentagreement.entity.Building;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//
//import java.util.List;
//
//public interface BuildingRepository extends JpaRepository<Building, Long> {
//
//    Page<Building> findAllByDeletedFalse(Pageable pageable);
//
//    Page<Building> findByBuildingNameContainingIgnoreCaseAndDeletedFalse(
//            String buildingName,
//            Pageable pageable
//    );
//
//}