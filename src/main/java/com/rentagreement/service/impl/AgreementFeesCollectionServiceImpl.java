package com.rentagreement.service.impl;

import com.rentagreement.dto.fees.AgreementFeesCollectionResponseDTO;
import com.rentagreement.entity.Broker;
import com.rentagreement.exception.ResourceNotFoundException;
import com.rentagreement.repository.BrokerRepository;
import com.rentagreement.repository.RentAgreementRepository;
import com.rentagreement.service.AgreementFeesCollectionService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class AgreementFeesCollectionServiceImpl
        implements AgreementFeesCollectionService {

    private final RentAgreementRepository agreementRepository;

    private final BrokerRepository brokerRepository;

    public AgreementFeesCollectionServiceImpl(
            RentAgreementRepository agreementRepository,
            BrokerRepository brokerRepository
    ) {
        this.agreementRepository = agreementRepository;
        this.brokerRepository = brokerRepository;
    }

    @Override
    public AgreementFeesCollectionResponseDTO getFeesCollection(
            LocalDate fromDate,
            LocalDate toDate
    ) {

        Broker currentBroker = getCurrentBroker();

        /*
         * Only Main Broker can view
         * Agreement Fees Collection.
         */
        if (currentBroker.getParentBroker() != null) {

            throw new IllegalArgumentException(
                    "Only Main Broker can view agreement fees collection."
            );
        }

        LocalDate today = LocalDate.now();

        // ==========================================
        // This Month
        // ==========================================

        LocalDate monthStart =
                today.withDayOfMonth(1);

        LocalDate nextMonthStart =
                monthStart.plusMonths(1);

        BigDecimal thisMonth =
                getFeesBetween(
                        currentBroker.getId(),
                        monthStart,
                        nextMonthStart
                );

        BigDecimal pendingDues =
                getPendingFeesBetween(
                        currentBroker.getId(),
                        monthStart,
                        nextMonthStart
                );

        // ==========================================
        // This Year
        // ==========================================

        LocalDate yearStart =
                today.withDayOfYear(1);

        LocalDate nextYearStart =
                yearStart.plusYears(1);

        BigDecimal thisYear =
                getFeesBetween(
                        currentBroker.getId(),
                        yearStart,
                        nextYearStart
                );

        // ==========================================
        // Custom Date Range
        // ==========================================

        BigDecimal customTotal =
                BigDecimal.ZERO;

        BigDecimal customPendingDues =
                BigDecimal.ZERO;

        if (fromDate != null && toDate != null) {

            if (toDate.isBefore(fromDate)) {

                throw new IllegalArgumentException(
                        "To date cannot be before From date."
                );
            }

            LocalDate customToDateExclusive =
                    toDate.plusDays(1);

            customTotal =
                    getFeesBetween(
                            currentBroker.getId(),
                            fromDate,
                            customToDateExclusive
                    );

            customPendingDues =
                    getPendingFeesBetween(
                            currentBroker.getId(),
                            fromDate,
                            customToDateExclusive
                    );
        }

        // ==========================================
        // Response
        // ==========================================

        return AgreementFeesCollectionResponseDTO.builder()

                .thisMonth(thisMonth)

                .thisYear(thisYear)

                .customTotal(customTotal)

                .pendingDues(pendingDues)

                .customPendingDues(customPendingDues)

                .customFrom(fromDate)

                .customTo(toDate)

                .build();
    }

    // ==========================================================
    // Collected Fees
    // ==========================================================

    private BigDecimal getFeesBetween(
            Long brokerId,
            LocalDate fromDate,
            LocalDate toDate
    ) {

        LocalDateTime fromDateTime =
                fromDate.atStartOfDay();

        LocalDateTime toDateTime =
                toDate.atStartOfDay();

        BigDecimal total =
                agreementRepository
                        .sumFeesPaidByBrokerIdAndCreatedDateBetween(
                                brokerId,
                                fromDateTime,
                                toDateTime
                        );

        return total != null
                ? total
                : BigDecimal.ZERO;
    }

    // ==========================================================
    // Pending Fees
    // ==========================================================

    private BigDecimal getPendingFeesBetween(
            Long brokerId,
            LocalDate fromDate,
            LocalDate toDate
    ) {

        LocalDateTime fromDateTime =
                fromDate.atStartOfDay();

        LocalDateTime toDateTime =
                toDate.atStartOfDay();

        BigDecimal total =
                agreementRepository
                        .sumPendingFeesByBrokerIdAndCreatedDateBetween(
                                brokerId,
                                fromDateTime,
                                toDateTime
                        );

        return total != null
                ? total
                : BigDecimal.ZERO;
    }

    // ==========================================================
    // Current Broker
    // ==========================================================

    private Broker getCurrentBroker() {

        String username =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        return brokerRepository
                .findByUsernameAndDeletedFalse(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Logged-in broker not found"
                        )
                );
    }

}


//package com.rentagreement.service.impl;
//
//import com.rentagreement.dto.fees.AgreementFeesCollectionResponseDTO;
//import com.rentagreement.entity.Broker;
//import com.rentagreement.exception.ResourceNotFoundException;
//import com.rentagreement.repository.BrokerRepository;
//import com.rentagreement.repository.RentAgreementRepository;
//import com.rentagreement.service.AgreementFeesCollectionService;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//
//@Service
//public class AgreementFeesCollectionServiceImpl
//        implements AgreementFeesCollectionService {
//
//    private final RentAgreementRepository agreementRepository;
//
//    private final BrokerRepository brokerRepository;
//
//    public AgreementFeesCollectionServiceImpl(
//            RentAgreementRepository agreementRepository,
//            BrokerRepository brokerRepository
//    ) {
//
//        this.agreementRepository = agreementRepository;
//
//        this.brokerRepository = brokerRepository;
//
//    }
//
//
//    @Override
//    public AgreementFeesCollectionResponseDTO getFeesCollection(
//            LocalDate fromDate,
//            LocalDate toDate
//    ) {
//
//        Broker currentBroker = getCurrentBroker();
//
//        /*
//         * Only Main Broker can view
//         * Agreement Fees Collection.
//         */
//        if (currentBroker.getParentBroker() != null) {
//
//            throw new IllegalArgumentException(
//                    "Only Main Broker can view agreement fees collection."
//            );
//
//        }
//
//        LocalDate today = LocalDate.now();
//
//        // ==========================================
//        // This Month
//        // ==========================================
//
//        LocalDate monthStart =
//                today.withDayOfMonth(1);
//
//        LocalDate nextMonthStart =
//                monthStart.plusMonths(1);
//
//        BigDecimal thisMonth =
//                getFeesBetween(
//                        currentBroker.getId(),
//                        monthStart,
//                        nextMonthStart
//                );
//
//
//        // ==========================================
//        // This Year
//        // ==========================================
//
//        LocalDate yearStart =
//                today.withDayOfYear(1);
//
//        LocalDate nextYearStart =
//                yearStart.plusYears(1);
//
//        BigDecimal thisYear =
//                getFeesBetween(
//                        currentBroker.getId(),
//                        yearStart,
//                        nextYearStart
//                );
//
//
//        // ==========================================
//        // Custom Date Range
//        // ==========================================
//
//        BigDecimal customTotal =
//                BigDecimal.ZERO;
//
//        if (fromDate != null && toDate != null) {
//
//            if (toDate.isBefore(fromDate)) {
//
//                throw new IllegalArgumentException(
//                        "To date cannot be before From date."
//                );
//
//            }
//
//            customTotal =
//                    getFeesBetween(
//                            currentBroker.getId(),
//                            fromDate,
//                            toDate.plusDays(1)
//                    );
//
//        }
//
//
//        return AgreementFeesCollectionResponseDTO.builder()
//
//                .thisMonth(thisMonth)
//
//                .thisYear(thisYear)
//
//                .customTotal(customTotal)
//
//                .customFrom(fromDate)
//
//                .customTo(toDate)
//
//                .build();
//
//    }
//
//
//    private BigDecimal getFeesBetween(
//            Long brokerId,
//            LocalDate fromDate,
//            LocalDate toDate
//    ) {
//
//        LocalDateTime fromDateTime =
//                fromDate.atStartOfDay();
//
//        LocalDateTime toDateTime =
//                toDate.atStartOfDay();
//
//        BigDecimal total =
//                agreementRepository
//                        .sumFeesPaidByBrokerIdAndCreatedDateBetween(
//                                brokerId,
//                                fromDateTime,
//                                toDateTime
//                        );
//
//        return total != null
//                ? total
//                : BigDecimal.ZERO;
//
//    }
//
//
//    private Broker getCurrentBroker() {
//
//        String username =
//                SecurityContextHolder
//                        .getContext()
//                        .getAuthentication()
//                        .getName();
//
//        return brokerRepository
//                .findByUsernameAndDeletedFalse(username)
//                .orElseThrow(() ->
//                        new ResourceNotFoundException(
//                                "Logged-in broker not found"
//                        )
//                );
//
//    }
//
//}