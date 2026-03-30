package com.loanflow.constants;

/**
        * JWT and Spring Security string constants.
        * Centralise here so JwtAuthenticationFilter, JwtTokenProvider,
 * and SecurityConfig never drift out of sync.
 */
public final class SecurityConstants {

    private SecurityConstants() {}

    // ── HTTP header ────────────────────────────────────────────────

    /** Standard Authorization header name */
    public static final String AUTHORIZATION_HEADER = "Authorization";

    /** Prefix stripped from token before parsing */
    public static final String BEARER_PREFIX = "Bearer ";

    /** Token type field in AuthResponse */
    public static final String TOKEN_TYPE = "Bearer";

    // ── Spring Security role prefix ────────────────────────────────
    // Used by CustomUserDetailsService when building SimpleGrantedAuthority

    /**
     * Spring Security requires ROLE_ prefix.
     * Usage: ROLE_PREFIX + user.getRole().name()
     * Produces: "ROLE_BORROWER", "ROLE_LOAN_OFFICER", "ROLE_ADMIN"
     */
    public static final String ROLE_PREFIX = "ROLE_";

    // ── Public endpoints ───────────────────────────────────────────
    // Used by SecurityConfig — add new public URLs here only

    /**
     * Endpoints that bypass JWT authentication entirely.
     * SecurityConfig.filterChain() reads this array directly.
     * Never scatter permitAll() calls across multiple places.
     */
    public static final String[] PUBLIC_URLS = {
            "/api/v1/auth/register",
            "/api/v1/auth/login",
            "/actuator/health",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    };
}

