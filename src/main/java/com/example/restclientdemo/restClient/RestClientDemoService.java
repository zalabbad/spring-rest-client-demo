package com.example.restclientdemo.restClient;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@ConditionalOnProperty(name = "rest-client.enabled", havingValue = "true")
@Service
@RequiredArgsConstructor
@Slf4j
public class RestClientDemoService {

    private final RestClient restClient;
    private final HttpServletRequest httpServletRequest;

    @Retryable(
            retryFor = {RuntimeException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public String fetchDataWithRetry(String resourcePath) {
        log.info("RestClient - Attempting to fetch data from {}", resourcePath);
        log.debug("RestClient - MDC context: {}", MDC.getCopyOfContextMap());
        log.debug("RestClient - Request headers: {}", Collections.list(httpServletRequest.getHeaderNames()));
        log.debug("RestClient - Security context: {}", SecurityContextHolder.getContext().getAuthentication().getName());

        try {
            // Make the actual API call
            String response = restClient.get()
                .uri(resourcePath)
                .retrieve()
                .body(String.class);

            log.info("RestClient - Successfully fetched data");
            return response;
        } catch (RestClientException e) {
            log.error("RestClient - Error while fetching data: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("RestClient - Unknown error while fetching data: {}", e.getMessage());
            // Re-throw to trigger retry
            throw e;
        }
    }
}
