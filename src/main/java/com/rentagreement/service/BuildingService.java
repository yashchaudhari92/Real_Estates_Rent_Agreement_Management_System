package com.rentagreement.service;

import com.rentagreement.dto.building.BuildingRequestDTO;
import com.rentagreement.dto.building.BuildingResponseDTO;
import com.rentagreement.dto.building.BuildingUpdateRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BuildingService {

    BuildingResponseDTO addBuilding(
            BuildingRequestDTO request
    );

    Page<BuildingResponseDTO> getAllBuildings(
            String keyword,
            Pageable pageable
    );

    BuildingResponseDTO getBuildingById(
            Long id
    );

    BuildingResponseDTO updateBuilding(
            Long id,
            BuildingUpdateRequestDTO request
    );

    void deleteBuilding(
            Long id
    );

}