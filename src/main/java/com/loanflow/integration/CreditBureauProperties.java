package com.loanflow.integration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Typed binding for application.yml credit-bureau.* block:
 *
 *   credit-bureau:
 *     base-url: http://localhost:8081/api/v1/credit
 *     timeout-ms: 3000
 */
@Component
@ConfigurationProperties(prefix = "credit-bureau")
@Getter
@Setter
public class CreditBureauProperties {

    private String baseUrl;

    /** Milliseconds before HTTP call times out. Default: 3000ms */
    private int timeoutMs = 3000;
}

