package com.rentagreement.dto.auth;

import com.rentagreement.enums.BrokerStatus;
import com.rentagreement.enums.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BrokerResponseDTO {

    private Long id;

    private String brokerName;

    private String companyName;

    private String email;

    private String mobileNumber;

    private String username;

    private String officeAddress;

    private Role role;

    private BrokerStatus status;

}