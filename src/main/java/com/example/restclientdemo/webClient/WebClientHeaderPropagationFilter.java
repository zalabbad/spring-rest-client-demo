package com.example.restclientdemo.webClient;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebClientHeaderPropagationFilter implements ExchangeFilterFunction {

    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            HttpServletRequest currentRequest = requestAttributes.getRequest();
            ClientRequest.Builder mutatedRequest = ClientRequest.from(request);

            // Propagate headers from current request to outgoing request
            Collections.list(currentRequest.getHeaderNames()).forEach(headerName -> {
                String headerValue = currentRequest.getHeader(headerName);
                log.debug("WebClient - Propagating header: {} = {}", headerName, headerValue);
                mutatedRequest.header(headerName, headerValue);
            });

            return next.exchange(mutatedRequest.build());
        } else {
            log.warn("WebClient - No current request found, headers will not be propagated");
            return next.exchange(request);
        }
    }
}
