package com.example.restclientdemo.restClient;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.web.client.RestClient;

@Configuration
@EnableRetry
@RequiredArgsConstructor
public class RestClientConfig {

    private final RestClientLoggingInterceptor restClientLoggingInterceptor;
    private final HeaderPropagationInterceptor headerPropagationInterceptor;

    @Value("${api.base-url}")
    private String apiBaseUrl;

    @Bean
    public RestClient restClient(RestClient.Builder builder) {
        return builder
                .baseUrl(apiBaseUrl)
                .requestInterceptor(headerPropagationInterceptor)
                .requestInterceptor(restClientLoggingInterceptor)
                .build();
    }
}
