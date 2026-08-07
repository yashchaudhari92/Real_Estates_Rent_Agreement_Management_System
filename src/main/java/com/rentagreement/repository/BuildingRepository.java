package com.rentagreement.repository;

import com.rentagreement.entity.Building;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BuildingRepository extends JpaRepository<Building, Long> {

    Page<Building> findAllByDeletedFalse(Pageable pageable);

    Page<Building> findByBuildingNameContainingIgnoreCaseAndDeletedFalse(
            String buildingName,
            Pageable pageable
    );

}