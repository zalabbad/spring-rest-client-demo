package com.example.restclientdemo.restClient;

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
@RequestMapping("/api/demo")
@RequiredArgsConstructor
@Slf4j
public class RestClientDemoController {

    private final RestClientDemoService restClientDemoService;

    @GetMapping("/posts/{id}")
    public ResponseEntity<String> getPost(@PathVariable String id, @RequestHeader("X-Custom-Header") String customHeader) {
        String userId = "user-123"; // Simulated user ID
        MDC.put("userId", userId);
        MDC.put("customHeader", customHeader);

        log.info("Received request for post with ID: {}, requestId: {}, userId: {}",
                id, MDC.get("tx_id"), userId);
        return ResponseEntity
            .ok(restClientDemoService.fetchDataWithRetry("/posts/" + id));
    }

}
