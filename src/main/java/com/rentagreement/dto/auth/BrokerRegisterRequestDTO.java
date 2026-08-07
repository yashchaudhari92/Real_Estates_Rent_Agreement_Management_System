package com.rentagreement.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BrokerRegisterRequestDTO {

    @NotBlank
    private String brokerName;

    @NotBlank
    private String companyName;

    @Email
    private String email;

    @NotBlank
    private String mobileNumber;

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    private String officeAddress;

}