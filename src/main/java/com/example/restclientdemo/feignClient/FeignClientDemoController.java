package com.example.restclientdemo.feignClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller that demonstrates the use of FeignClient to fetch data from an external API.
 * This shows a declarative approach to REST API clients.
 */
@ConditionalOnProperty(name = "feign-client.enabled", havingValue = "true")
@RestController
@RequestMapping("/api/feign")
@RequiredArgsConstructor
@Slf4j
public class FeignClientDemoController {

    private final FeignClientDemoService feignClientDemoService;

    /**
     * Fetches a post by its ID using FeignClient.
     * 
     * @param id the ID of the post to fetch
     * @param customHeader a custom header for demonstration purposes
     * @return the post as a String
     */
    @GetMapping("/posts/{id}")
    public ResponseEntity<String> getPost(@PathVariable String id, @RequestHeader("X-Custom-Header") String customHeader) {
        String userId = "user-123"; // Simulated user ID
        MDC.put("userId", userId);
        MDC.put("customHeader", customHeader);

        log.info("FeignClient - Received request for post with ID: {}, tx_id: {}, userId: {}",
                id, MDC.get("tx_id"), userId);
        return ResponseEntity
            .ok(feignClientDemoService.fetchDataWithRetry(id));
    }
}
