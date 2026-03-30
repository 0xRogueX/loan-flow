package com.loanflow.security;

import com.loanflow.entity.user.User;
import com.loanflow.exception.ResourceNotFoundException;
import com.loanflow.exception.UnauthorizedAccessException;
import com.loanflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class SecurityUtils {

    private final UserRepository userRepository;

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext()
                .getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            log.warn("Attempted to access a secured resource without valid authentication.");
            throw new UnauthorizedAccessException(
                    "No authenticated user found in security context.");
        }

        String email = auth.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Token valid, but user not found in database for email: {}", email);
                    return new ResourceNotFoundException("Authenticated user not found in database.");
                });
    }

    // Used to prevent Horizontal Privilege Escalation (IDOR attacks) - Insecure Direct Object Reference
    public boolean isOwner(UUID resourceOwnerId) {
        if (resourceOwnerId == null) {
            return false;
        }
        return getCurrentUser().getId().equals(resourceOwnerId);
    }

    public boolean hasRole(String role) {
        Authentication auth = SecurityContextHolder.getContext()
                .getAuthentication();

        if (auth == null || !auth.isAuthenticated() ||
                auth instanceof AnonymousAuthenticationToken) {
            return false;
        }

        String requiredAuthority = "ROLE_" + role.toUpperCase();

        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(requiredAuthority::equals);
    }
}