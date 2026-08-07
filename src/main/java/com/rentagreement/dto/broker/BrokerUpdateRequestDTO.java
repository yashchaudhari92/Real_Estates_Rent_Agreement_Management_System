package com.rentagreement.dto.broker;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BrokerUpdateRequestDTO {

    @NotBlank
    private String brokerName;

    @NotBlank
    private String companyName;

    @Email
    private String email;

    @NotBlank
    private String mobileNumber;

    @NotBlank
    private String officeAddress;

}