package com.loanflow.integration;

import com.loanflow.exception.CreditBureauException;
import com.loanflow.integration.dto.CreditBureauResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreditBureauClient {

    private final RestTemplate restTemplate;
    private final CreditBureauProperties properties;

    private static final String REPORT_ENDPOINT = "/report";

    public CreditBureauResponse fetchReport(String panNumber) {
        String url = properties.getBaseUrl() + REPORT_ENDPOINT;
        log.info("Calling Credit Bureau for PAN: {}", panNumber);

        try {
            HttpEntity<CreditBureauResponse> entity = new HttpEntity<>(
                    CreditBureauResponse.builder()
                            .panNumber(panNumber)
                            .build(),
                    getHeader());

            ResponseEntity<CreditBureauResponse> response =
                    restTemplate.postForEntity(url, entity, CreditBureauResponse.class);

            log.info("Bureau response received for PAN: {}", panNumber);
            return response.getBody();
        } catch (RestClientException e) {
            log.warn("Credit Bureau unavailable for PAN: {}. Reason: {}", panNumber, e.getMessage());
            throw new CreditBureauException("Failed to fetch credit bureau report: " + e.getMessage());
        }
    }

    private HttpHeaders getHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
