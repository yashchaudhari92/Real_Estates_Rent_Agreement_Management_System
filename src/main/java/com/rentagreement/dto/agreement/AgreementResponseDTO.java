package com.rentagreement.dto.agreement;

import com.rentagreement.enums.CommercialCategory;
import com.rentagreement.enums.PropertyType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class AgreementResponseDTO {

    private Long id;

    private Long buildingId;

    private String buildingName;

    private PropertyType propertyType;

    // Owner
    private String ownerName;
    private String ownerMobile;
    private String ownerEmail;

    // Tenant
    private String tenantName;
    private String tenantMobile;
    private String tenantEmail;

    // Agreement
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal deposit;
    private BigDecimal monthlyRent;

    // Residential
    private Integer bhk;
    private String wing;
    private Integer floor;
    private String flatNumber;

    // Commercial
    private Double area;
    private CommercialCategory commercialCategory;

    // Document
    private String documentName;

    // Audit
    private LocalDateTime createdDate;

}