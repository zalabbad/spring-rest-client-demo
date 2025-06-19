package com.example.restclientdemo.webClient;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class WebClientLoggingFilter implements ExchangeFilterFunction {

	@Override
	public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
		logRequest(request);
		return next.exchange(request);
	}

	private void logRequest(ClientRequest request) {
		log.info("=========================== WebClient Request Begin ===========================");
		log.info("WebClient - URI: {}", request.url());
		log.info("WebClient - Method: {}", request.method());
		log.info("WebClient - Headers: {}", request.headers());
		log.info("=========================== WebClient Request End ===========================");
	}
}
