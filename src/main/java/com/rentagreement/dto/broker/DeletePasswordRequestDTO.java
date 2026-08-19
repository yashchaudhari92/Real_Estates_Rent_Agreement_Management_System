package com.rentagreement.dto.broker;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DeletePasswordRequestDTO {

    private String currentDeletePassword;

    @NotBlank(message = "New delete password is required")
    @Size(min = 6, message = "Delete password must contain at least 6 characters")
    private String newDeletePassword;

    @NotBlank(message = "Confirm delete password is required")
    private String confirmDeletePassword;

}