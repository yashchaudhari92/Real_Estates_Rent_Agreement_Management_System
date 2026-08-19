package com.rentagreement.service;

import com.rentagreement.dto.agreement.AgreementResponseDTO;
import com.rentagreement.dto.dashboard.BrokerDashboardResponseDTO;

import java.util.List;

public interface BrokerDashboardService {

    BrokerDashboardResponseDTO getDashboardStats();

    List<AgreementResponseDTO> getExpiringAgreementsByMonth(
            int year,
            int month
    );

}