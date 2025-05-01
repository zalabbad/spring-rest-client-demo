package com.example.restclientdemo.webClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/webclient")
@RequiredArgsConstructor
@Slf4j
public class WebClientDemoController {

    private final WebClientDemoService webClientDemoService;

    @GetMapping("/posts/{id}")
    public ResponseEntity<String> getPost(@PathVariable String id, @RequestHeader("X-Custom-Header") String customHeader) {
        String userId = "user-123"; // Simulated user ID
        MDC.put("userId", userId);
        MDC.put("customHeader", customHeader);

        log.info("WebClient - Received request for post with ID: {}, tx_id: {}, userId: {}",
                id, MDC.get("tx_id"), userId);
        return ResponseEntity
            .ok(webClientDemoService.fetchDataWithRetry("/posts/" + id));
    }
}
