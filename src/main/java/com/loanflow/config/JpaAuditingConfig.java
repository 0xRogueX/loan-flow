//package com.loanflow.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.domain.AuditorAware;
//import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//
//import java.util.Optional;
//
//@Configuration
//@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
//public class JpaAuditingConfig {
//
//    @Bean
//    public AuditorAware<String> auditorProvider() {
//        return () -> {
//            // 1. Get the current security context
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//            // 2. If no user is logged in (e.g., system jobs or registration), return "SYSTEM"
//            if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
//                return Optional.of("SYSTEM");
//            }
//
//            // 3. Extract the email/username and return it for Hibernate to inject into @CreatedBy
//            Object principal = authentication.getPrincipal();
//            if (principal instanceof UserDetails) {
//                return Optional.of(((UserDetails) principal).getUsername());
//            } else {
//                return Optional.of(principal.toString());
//            }
//        };
//    }
//}