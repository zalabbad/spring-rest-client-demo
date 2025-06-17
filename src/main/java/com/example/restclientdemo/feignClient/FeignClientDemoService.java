package com.example.restclientdemo.feignClient;

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

/**
 * Service that uses FeignClient to fetch data from the JSONPlaceholder API.
 * This demonstrates a declarative approach to REST API clients with retry capabilities.
 */
@ConditionalOnProperty(name = "feign-client.enabled", havingValue = "true")
@Service
@RequiredArgsConstructor
@Slf4j
public class FeignClientDemoService {

    private final JsonPlaceholderClient jsonPlaceholderClient;
    private final HttpServletRequest httpServletRequest;

    /**
     * Fetches data from the JSONPlaceholder API with retry capabilities.
     * 
     * @param resourceId the ID of the resource to fetch
     * @return the fetched data as a String
     */
    @Retryable(
            retryFor = {RuntimeException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public String fetchDataWithRetry(String resourceId) {
        log.info("FeignClient - Attempting to fetch data for resource ID: {}", resourceId);
        log.debug("FeignClient - MDC context: {}", MDC.getCopyOfContextMap());
        log.debug("FeignClient - Request headers: {}", Collections.list(httpServletRequest.getHeaderNames()));
        log.debug("FeignClient - Security context: {}", SecurityContextHolder.getContext().getAuthentication().getName());

        try {
            // Make the actual API call using the Feign client
            String response = jsonPlaceholderClient.getPostById(resourceId);
            
            log.info("FeignClient - Successfully fetched data");
            return response;
        } catch (Exception e) {
            log.error("FeignClient - Error while fetching data: {}", e.getMessage());
            // Re-throw to trigger retry
            throw e;
        }
    }
}
