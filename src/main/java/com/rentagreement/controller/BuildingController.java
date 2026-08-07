package com.rentagreement.controller;

import com.rentagreement.dto.building.BuildingRequestDTO;
import com.rentagreement.dto.building.BuildingResponseDTO;
import com.rentagreement.dto.building.BuildingUpdateRequestDTO;
import com.rentagreement.response.ApiResponse;
import com.rentagreement.service.BuildingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RestController
@RequestMapping("/api/broker/buildings")
@CrossOrigin(origins = "http://localhost:5173")
public class BuildingController {

    private final BuildingService buildingService;

    public BuildingController(BuildingService buildingService) {
        this.buildingService = buildingService;
    }

    // Add Building
    @PostMapping
    public ResponseEntity<ApiResponse<BuildingResponseDTO>> addBuilding(
            @Valid @RequestBody BuildingRequestDTO request
    ) {

        BuildingResponseDTO building =
                buildingService.addBuilding(request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Building Added Successfully",
                        building
                )
        );
    }

    // View All Buildings
    @GetMapping
    public ResponseEntity<ApiResponse<Page<BuildingResponseDTO>>> getAllBuildings(

            @RequestParam(defaultValue = "") String keyword,

            @RequestParam(defaultValue = "0") int page,

            @RequestParam(defaultValue = "10") int size

    ) {

        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(

                new ApiResponse<>(

                        true,

                        "Building List Fetched Successfully",

                        buildingService.getAllBuildings(
                                keyword,
                                pageable
                        )

                )

        );

    }

    // View Building By Id
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BuildingResponseDTO>> getBuildingById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Building Details",
                        buildingService.getBuildingById(id)
                )
        );
    }

    // Update Building
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BuildingResponseDTO>> updateBuilding(
            @PathVariable Long id,
            @Valid @RequestBody BuildingUpdateRequestDTO request
    ) {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Building Updated Successfully",
                        buildingService.updateBuilding(id, request)
                )
        );
    }

    // Delete Building (Soft Delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteBuilding(
            @PathVariable Long id
    ) {

        buildingService.deleteBuilding(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Building Deleted Successfully",
                        null
                )
        );
    }

}