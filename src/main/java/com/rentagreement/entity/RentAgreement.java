package com.rentagreement.entity;

import com.rentagreement.enums.CommercialCategory;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "rent_agreements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentAgreement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ==========================
    // Agreement Renewal / History
    // ==========================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "previous_agreement_id")
    private RentAgreement previousAgreement;

    // ==========================
    // Building
    // ==========================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id", nullable = false)
    private Building building;

    // ==========================
    // Owner Details
    // ==========================

    @Column(nullable = false)
    private String ownerName;

    @Column(nullable = false)
    private String ownerMobile;

    @Column(nullable = false)
    private String ownerEmail;

    // ==========================
    // Tenant Details
    // ==========================

    @Column(nullable = false)
    private String tenantName;

    @Column(nullable = false)
    private String tenantMobile;

    @Column(nullable = false)
    private String tenantEmail;

    // ==========================
    // Agreement Details
    // ==========================

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal deposit;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal monthlyRent;

    // ==========================
    // Fees Paid for Agreement
    // ==========================
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal feesPaid;

    // ==========================
    // Residential Details
    // ==========================

    private Integer bhk;

    private String wing;

    private Integer floor;

    private String flatNumber;

    // ==========================
    // Commercial Details
    // ==========================

    private Double area;

    @Enumerated(EnumType.STRING)
    private CommercialCategory commercialCategory;

    // ==========================
    // Agreement Document
    // ==========================

    private String documentName;

    private String documentPath;

    private LocalDateTime uploadDate;

    // ==========================
    // Common Fields
    // ==========================

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @Column(nullable = false)
    private boolean deleted;

    @PrePersist
    public void prePersist() {

        createdDate = LocalDateTime.now();

        deleted = false;

    }

}