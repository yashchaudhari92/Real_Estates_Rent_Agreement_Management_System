package com.rentagreement.service.impl;

import com.rentagreement.dto.dashboard.AdminDashboardResponseDTO;
import com.rentagreement.enums.BrokerStatus;
import com.rentagreement.repository.BrokerRepository;
import com.rentagreement.service.AdminDashboardService;
import org.springframework.stereotype.Service;

@Service
public class AdminDashboardServiceImpl
        implements AdminDashboardService {

    private final BrokerRepository brokerRepository;

    public AdminDashboardServiceImpl(
            BrokerRepository brokerRepository
    ) {

        this.brokerRepository = brokerRepository;

    }

    @Override
    public AdminDashboardResponseDTO getDashboardStats() {

        long totalBrokers =
                brokerRepository.countByDeletedFalse();

        long activeBrokers =
                brokerRepository.countByDeletedFalseAndStatus(
                        BrokerStatus.ACTIVE
                );

        long inactiveBrokers =
                brokerRepository.countByDeletedFalseAndStatus(
                        BrokerStatus.INACTIVE
                );

        return AdminDashboardResponseDTO.builder()

                .totalBrokers(totalBrokers)

                .activeBrokers(activeBrokers)

                .inactiveBrokers(inactiveBrokers)

                .build();
    }

}