package com.rentagreement.service.impl;

import com.rentagreement.dto.building.BuildingRequestDTO;
import com.rentagreement.dto.building.BuildingResponseDTO;
import com.rentagreement.dto.building.BuildingUpdateRequestDTO;
import com.rentagreement.entity.Broker;
import com.rentagreement.entity.Building;
import com.rentagreement.enums.PropertyType;
import com.rentagreement.exception.ResourceNotFoundException;
import com.rentagreement.repository.BrokerRepository;
import com.rentagreement.repository.BuildingRepository;
import com.rentagreement.service.BuildingService;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Service
public class BuildingServiceImpl implements BuildingService {

    private final BuildingRepository buildingRepository;
    private final BrokerRepository brokerRepository;

    public BuildingServiceImpl(
            BuildingRepository buildingRepository,
            BrokerRepository brokerRepository
    ) {

        this.buildingRepository = buildingRepository;
        this.brokerRepository = brokerRepository;

    }

    @Override
    public BuildingResponseDTO addBuilding(
            BuildingRequestDTO request
    ) {

        Broker broker = brokerRepository.findById(request.getBrokerId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Broker not found"));

        Building building = Building.builder()
                .buildingName(request.getBuildingName())
                .propertyType(PropertyType.valueOf(request.getPropertyType()))
                .broker(broker)
                .build();

        Building savedBuilding = buildingRepository.save(building);

        return mapToDTO(savedBuilding);

    }

    @Override
    public Page<BuildingResponseDTO> getAllBuildings(
            String keyword,
            Pageable pageable
    ) {

        if (keyword != null && !keyword.trim().isEmpty()) {

            return buildingRepository
                    .findByBuildingNameContainingIgnoreCaseAndDeletedFalse(
                            keyword,
                            pageable
                    )
                    .map(this::mapToDTO);

        }

        return buildingRepository
                .findAllByDeletedFalse(pageable)
                .map(this::mapToDTO);

    }

    @Override
    public BuildingResponseDTO getBuildingById(Long id) {

        Building building = buildingRepository.findById(id)
                .filter(b -> !b.isDeleted())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Building not found"));

        return mapToDTO(building);

    }

    @Override
    public BuildingResponseDTO updateBuilding(
            Long id,
            BuildingUpdateRequestDTO request
    ) {

        Building building = buildingRepository.findById(id)
                .filter(b -> !b.isDeleted())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Building not found"));

        Broker broker = brokerRepository.findById(request.getBrokerId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Broker not found"));

        building.setBuildingName(request.getBuildingName());
        building.setPropertyType(
                PropertyType.valueOf(request.getPropertyType())
        );
        building.setBroker(broker);

        Building updatedBuilding = buildingRepository.save(building);

        return mapToDTO(updatedBuilding);

    }

    @Override
    public void deleteBuilding(Long id) {

        Building building = buildingRepository.findById(id)
                .filter(b -> !b.isDeleted())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Building not found"));

        building.setDeleted(true);

        buildingRepository.save(building);

    }

    private BuildingResponseDTO mapToDTO(
            Building building
    ) {

        return BuildingResponseDTO.builder()
                .id(building.getId())
                .buildingName(building.getBuildingName())
                .propertyType(building.getPropertyType())
                .brokerId(building.getBroker().getId())
                .brokerName(building.getBroker().getBrokerName())
                .createdDate(building.getCreatedDate())
                .build();

    }

}