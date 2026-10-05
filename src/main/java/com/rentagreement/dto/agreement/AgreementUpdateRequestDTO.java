package com.rentagreement.dto.agreement;

import jakarta.validation.constraints.*;
import lombok.Data;
import com.rentagreement.enums.AgreementStatus;
import com.rentagreement.enums.AgreementFeeStatus;

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

    private String tokenNo;

    private String executiveName;

    private String source;

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

    private AgreementFeeStatus feeStatus;

    @NotNull
    private AgreementStatus status;

    // Residential
    private Integer bhk;

    private String wing;

    private Integer floor;

    private String flatNumber;

    // Commercial
    private Double area;

    private String commercialCategory;

}