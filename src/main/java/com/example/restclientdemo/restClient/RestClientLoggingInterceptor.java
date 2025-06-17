package com.example.restclientdemo.restClient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@ConditionalOnProperty(name = "rest-client.enabled", havingValue = "true")
@Component
@Slf4j
public class RestClientLoggingInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        logRequest(request, body);
        ClientHttpResponse response = execution.execute(request, body);
        return response;
    }

    private void logRequest(HttpRequest request, byte[] body) {
        log.info("=========================== RestClient Request Begin ===========================");
        log.info("RestClient - URI: {}", request.getURI());
        log.info("RestClient - Method: {}", request.getMethod());
        log.info("RestClient - Headers: {}", request.getHeaders());
        log.info("RestClient - Request body: {}", new String(body, StandardCharsets.UTF_8));
        log.info("=========================== RestClient Request End ===========================");
    }
}
