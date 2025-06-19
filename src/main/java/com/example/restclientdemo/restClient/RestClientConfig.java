package com.example.restclientdemo.restClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.web.client.RestClient;

@Configuration
@EnableRetry
public class RestClientConfig {

	@Value("${api.base-url}")
	private String apiBaseUrl;

	@Bean
	public RestClientLoggingInterceptor restClientLoggingInterceptor() {
		return new RestClientLoggingInterceptor();
	}

	@Bean
	public HeaderPropagationInterceptor headerPropagationInterceptor() {
		return new HeaderPropagationInterceptor();
	}

	@Bean
	public RestClient restClient(RestClient.Builder builder) {
		return builder
			.baseUrl(apiBaseUrl)
			.requestInterceptor(headerPropagationInterceptor())
			.requestInterceptor(restClientLoggingInterceptor())
			.build();
	}
}
