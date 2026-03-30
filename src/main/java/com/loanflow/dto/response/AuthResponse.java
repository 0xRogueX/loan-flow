package com.loanflow.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.loanflow.enums.Role;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {

    private String token;

    private Role role;

    private String name;

    private String email;
}
