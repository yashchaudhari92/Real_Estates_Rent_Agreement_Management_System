package com.rentagreement.dto.agreement;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class AgreementUpdateRequestDTO {

    @NotNull
    private Long buildingId;

    // Owner
    @NotBlank
    private String ownerName;

    @NotBlank
    private String ownerMobile;

    @Email
    @NotBlank
    private String ownerEmail;

    // Tenant
    @NotBlank
    private String tenantName;

    @NotBlank
    private String tenantMobile;

    @Email
    @NotBlank
    private String tenantEmail;

    // Agreement
    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    @NotNull
    private BigDecimal deposit;

    @NotNull
    private BigDecimal monthlyRent;

    @NotNull
    private BigDecimal feesPaid;

    // Residential
    private Integer bhk;

    private String wing;

    private Integer floor;

    private String flatNumber;

    // Commercial
    private Double area;

    private String commercialCategory;

}