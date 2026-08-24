package com.rentagreement.dto.fees;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class AgreementFeesCollectionResponseDTO {

    private BigDecimal thisMonth;

    private BigDecimal thisYear;

    private BigDecimal customTotal;

    private LocalDate customFrom;

    private LocalDate customTo;

}