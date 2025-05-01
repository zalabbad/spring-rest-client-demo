package com.example.restclientdemo.webClient;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebClientDemoService {

    private final WebClient webClient;
    private final HttpServletRequest httpServletRequest;

    public String fetchDataWithRetry(String resourcePath) {
        log.info("WebClient - Attempting to fetch data from {}", resourcePath);
        log.info("WebClient - MDC before call: {}", MDC.getCopyOfContextMap());
        log.info("WebClient - Request headers: {}", Collections.list(httpServletRequest.getHeaderNames()));
        log.info("WebClient - Security context: {}", SecurityContextHolder.getContext().getAuthentication().getName());

        return webClient.get()
                .uri(resourcePath)
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(response -> {
                    log.info("WebClient - Successfully fetched data");
                    log.info("WebClient - MDC after success: {}", MDC.getCopyOfContextMap());
                })
                .doOnError(e -> {
                    log.error("WebClient - Error while fetching data: {}", e.getMessage());
                    log.info("WebClient - MDC after error: {}", MDC.getCopyOfContextMap());
                })
                // This is where the issue occurs - retry happens in a different thread
                .retry(2)
                .block();
    }
}
