package com.rentagreement.dto.agreement;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeleteAgreementRequestDTO {

    @NotBlank(message = "Delete password is required")
    private String deletePassword;

}