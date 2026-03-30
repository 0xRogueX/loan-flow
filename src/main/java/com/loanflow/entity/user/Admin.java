package com.loanflow.entity.user;

import com.loanflow.enums.AdminAccessLevel;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "admin_profiles")
@DiscriminatorValue("ADMIN")
@Getter
@Setter
@NoArgsConstructor
public class Admin extends User {

//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id" , nullable = false)
//    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdminAccessLevel accessLevel;

    private LocalDateTime lastActionAt;
}