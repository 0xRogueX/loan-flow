package com.loanflow.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class LoginRequest {

    @Email(message = "Valid email is required")
    @NotBlank
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}
