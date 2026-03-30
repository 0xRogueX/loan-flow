package com.loanflow.security;


import com.loanflow.entity.user.User;
import com.loanflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        if (user.isDeleted()) {
            throw new UsernameNotFoundException(
                    "Account no longer exists: " + email);
        }

        if (!user.isActive()) {
            throw new DisabledException("Account is deactivated: " + email);
        }

        // "ROLE_BORROWER", "ROLE_LOAN_OFFICER", "ROLE_ADMIN"
        // Matches @PreAuthorize("hasRole('BORROWER')") in controllers
        Set<GrantedAuthority> authorities = new HashSet<>();
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());
        authorities.add(authority);


        // Spring Security User — email as username, BCrypt password
        // password field in your User entity stores the BCrypt hash
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(authorities)
                .build();
    }
}
