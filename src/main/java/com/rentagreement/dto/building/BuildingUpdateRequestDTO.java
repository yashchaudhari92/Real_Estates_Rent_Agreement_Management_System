package com.rentagreement.dto.building;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BuildingUpdateRequestDTO {

    @NotBlank
    private String buildingName;

    @NotNull
    private Long brokerId;

    @NotBlank
    private String propertyType;

}