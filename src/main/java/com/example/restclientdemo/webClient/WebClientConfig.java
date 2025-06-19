package com.example.restclientdemo.webClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

	private final WebClientLoggingFilter loggingFilter;
	private final WebClientHeaderPropagationFilter headerPropagationFilter;

	@Value("${api.base-url}")
	private String apiBaseUrl;

	@Bean
	public WebClient webClient(WebClient.Builder builder) {
		return builder
			.baseUrl(apiBaseUrl)
			.filter(headerPropagationFilter)
			.filter(loggingFilter)
			.build();
	}
}
