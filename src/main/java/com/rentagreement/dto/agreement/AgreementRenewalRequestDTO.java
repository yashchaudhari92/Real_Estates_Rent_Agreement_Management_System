package com.rentagreement.dto.agreement;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public class AgreementRenewalRequestDTO {

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @NotNull(message = "Deposit is required")
    @DecimalMin(
            value = "0.0",
            inclusive = true,
            message = "Deposit cannot be negative"
    )
    private BigDecimal deposit;

    @NotNull(message = "Monthly rent is required")
    @DecimalMin(
            value = "0.0",
            inclusive = true,
            message = "Monthly rent cannot be negative"
    )
    private BigDecimal monthlyRent;

    @NotNull(message = "Fees paid is required")
    @DecimalMin(
            value = "0.0",
            inclusive = true,
            message = "Fees paid cannot be negative"
    )
    private BigDecimal feesPaid;

    public AgreementRenewalRequestDTO() {
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getDeposit() {
        return deposit;
    }

    public void setDeposit(BigDecimal deposit) {
        this.deposit = deposit;
    }

    public BigDecimal getMonthlyRent() {
        return monthlyRent;
    }

    public void setMonthlyRent(BigDecimal monthlyRent) {
        this.monthlyRent = monthlyRent;
    }

    public BigDecimal getFeesPaid() {
        return feesPaid;
    }

    public void setFeesPaid(BigDecimal feesPaid) {
        this.feesPaid = feesPaid;
    }
}