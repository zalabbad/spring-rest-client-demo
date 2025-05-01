package com.example.restclientdemo.webClient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
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
        log.info("URI: {}", request.url());
        log.info("Method: {}", request.method());
        log.info("Headers: {}", request.headers());
        log.info("=========================== WebClient Request End ===========================");
    }
}
