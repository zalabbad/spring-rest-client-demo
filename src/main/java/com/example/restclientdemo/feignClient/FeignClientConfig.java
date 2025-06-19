package com.example.restclientdemo.feignClient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.Logger;
import feign.RequestInterceptor;

/**
 * Configuration for Feign clients. This class provides beans for logging and
 * header propagation.
 */
@Configuration
public class FeignClientConfig {

	/**
	 * Creates a request interceptor that propagates headers from the current
	 * request to the Feign client request.
	 *
	 * @return the header propagation interceptor
	 */
	@Bean
	public RequestInterceptor headerPropagationInterceptor() {
		return new FeignHeaderPropagationInterceptor();
	}

	/**
	 * Creates a request interceptor that logs Feign client requests and responses.
	 * This bean is to custom logging the requests/responses made by the Feign
	 * client, and can be replaced with a property configuration to have the default
	 * logging behavior.
	 *
	 * @return the logging interceptor
	 */
	@Bean
	public RequestInterceptor loggingInterceptor() {
		return new FeignLoggingInterceptor();
	}

	/**
	 * Sets the logging level for Feign clients. This bean can be replaced with a
	 * property configuration
	 * `spring.cloud.openfeign.client.config.default.loggerLevel`
	 *
	 * @return the logging level
	 */
	@Bean
	Logger.Level feignLoggerLevel() {
		return Logger.Level.FULL;
	}
}
