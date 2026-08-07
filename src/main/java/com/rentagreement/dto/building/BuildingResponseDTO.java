package com.rentagreement.dto.building;

import com.rentagreement.enums.PropertyType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BuildingResponseDTO {

    private Long id;

    private String buildingName;

    private PropertyType propertyType;

    private Long brokerId;

    private String brokerName;

    private LocalDateTime createdDate;

}