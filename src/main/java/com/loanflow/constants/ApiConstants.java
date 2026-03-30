package com.loanflow.constants;

/**
 * API path prefix constants.
 * Use in @RequestMapping to avoid scattered string literals.
 */
public final class ApiConstants {

    private ApiConstants() {}

    public static final String API_V1              = "/api/v1";
    public static final String AUTH_BASE           = API_V1 + "/auth";
    public static final String LOAN_BASE           = API_V1 + "/loans";
    public static final String PAYMENT_BASE        = API_V1 + "/payments";
    public static final String OFFICER_BASE        = API_V1 + "/officer";
    public static final String ADMIN_BASE          = API_V1 + "/admin";

    // ── Common path segments ───────────────────────────────────────
    public static final String PATH_SCHEDULE       = "/schedule";
    public static final String PATH_APPLICATIONS   = "/applications";
    public static final String PATH_APPROVE        = "/approve/{applicationNumber}";
    public static final String PATH_SIMULATE       = "/simulate";
    public static final String PATH_REPORTS        = "/reports";
}
