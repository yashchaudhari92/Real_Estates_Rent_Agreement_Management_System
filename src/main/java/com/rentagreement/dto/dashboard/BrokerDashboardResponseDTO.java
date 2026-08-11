package com.rentagreement.dto.dashboard;

import com.rentagreement.dto.agreement.AgreementResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BrokerDashboardResponseDTO {

    private String brokerName;

    private String companyName;

    private long totalBuildings;

    private long totalAgreements;

    private long activeAgreements;

    private long expiringSoon;

    private long expiredAgreements;

    private List<AgreementResponseDTO> recentAgreements;

}