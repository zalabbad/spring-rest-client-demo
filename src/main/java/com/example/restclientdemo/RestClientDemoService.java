package com.example.restclientdemo;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

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
        log.info("Attempting to fetch data from {}", resourcePath);
        log.info("MDC: {}", MDC.getCopyOfContextMap());
        log.info("Request headers: {}", Collections.list(httpServletRequest.getHeaderNames()));

        try {
            // Make the actual API call
            String response = restClient.get()
                .uri(resourcePath)
                .retrieve()
                .body(String.class);

            log.info("Successfully fetched data");
            return response;
        } catch (RestClientException e) {
            log.error("Error while fetching data: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unknown error while fetching data: {}", e.getMessage());
            // Re-throw to trigger retry
            throw e;
        }
    }
}
