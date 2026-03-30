package com.loanflow.dto.response;

import com.loanflow.enums.Role;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserResponse {

    private UUID id;
    private String name;
    private String email;
    private String phone;
    private Role role;
    private boolean isActive;
    private LocalDateTime createdAt;
}
