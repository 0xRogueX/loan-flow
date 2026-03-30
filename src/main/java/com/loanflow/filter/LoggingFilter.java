package com.loanflow.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class LoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {
            // Trace ID
            MDC.put("traceId", UUID.randomUUID().toString().substring(0, 8));

            // HTTP info
            MDC.put("method", request.getMethod());

            String uri = request.getRequestURI();
            String query = request.getQueryString();
            MDC.put("uri", query == null ? uri : uri + "?" + query);

            // Client info
            MDC.put("ip", request.getRemoteAddr());

            // User info
            String user = request.getUserPrincipal() != null
                    ? request.getUserPrincipal().getName()
                    : "ANONYMOUS";
            MDC.put("user", user);

            filterChain.doFilter(request, response);

            // Response status
            MDC.put("status", String.valueOf(response.getStatus()));

        } finally {
            MDC.clear();
        }
    }
}