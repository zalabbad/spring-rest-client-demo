package com.example.restclientdemo.config;

import java.io.IOException;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import org.slf4j.MDC;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TransactionIdFilter extends OncePerRequestFilter {

	public static final String TX_ID = "tx_id";

	@Override
	protected void doFilterInternal(
		HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
	) throws ServletException, IOException {
		try {
			// Generate a unique transaction ID for this request
			String txId = UUID.randomUUID().toString();

			// Add it to the MDC context
			MDC.put(TX_ID, txId);

			// Continue with the filter chain
			filterChain.doFilter(request, response);
		} finally {
			// Clean up the MDC context after the request is processed
			MDC.remove(TX_ID);
		}
	}
}
