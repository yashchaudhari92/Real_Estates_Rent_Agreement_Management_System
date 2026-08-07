package com.rentagreement.dto.broker;

import com.rentagreement.enums.BrokerStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BrokerListResponseDTO {

    private Long id;

    private String brokerName;

    private String companyName;

    private String email;

    private String mobileNumber;

    private String username;

    private String officeAddress;

    private BrokerStatus status;

}