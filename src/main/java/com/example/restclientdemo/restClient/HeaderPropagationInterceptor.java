package com.example.restclientdemo.restClient;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;

@Component
@RequiredArgsConstructor
@Slf4j
public class HeaderPropagationInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            HttpServletRequest currentRequest = requestAttributes.getRequest();
            propagateHeaders(currentRequest, request);
        } else {
            log.warn("No current request found, headers will not be propagated");
        }

        return execution.execute(request, body);
    }

    private void propagateHeaders(HttpServletRequest currentRequest, HttpRequest outgoingRequest) {
        Enumeration<String> headerNames = currentRequest.getHeaderNames();
        if (headerNames != null) {
            Collections.list(headerNames).forEach(headerName -> {
                String headerValue = currentRequest.getHeader(headerName);
                log.debug("Propagating header: {} = {}", headerName, headerValue);
                outgoingRequest.getHeaders().set(headerName, headerValue);
            });
        }
    }
}
