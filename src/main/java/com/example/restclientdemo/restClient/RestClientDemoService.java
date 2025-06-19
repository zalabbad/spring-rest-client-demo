package com.example.restclientdemo.restClient;

import java.util.Collections;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import org.slf4j.MDC;

import com.example.restclientdemo.model.Post;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestClientDemoService {

	private final RestClient restClient;
	private final HttpServletRequest httpServletRequest;

	@Retryable(retryFor = {RuntimeException.class}, maxAttempts = 2, backoff = @Backoff(delay = 1000, multiplier = 2))
	public String fetchDataWithRetry(String resourceId) {
		log.info("RestClient - Attempting to fetch data for resource ID: {}", resourceId);
		log.debug("RestClient - MDC context: {}", MDC.getCopyOfContextMap());
		log.debug("RestClient - Request headers: {}", Collections.list(httpServletRequest
			.getHeaderNames()));
		log.debug("RestClient - Security context: {}",
			SecurityContextHolder.getContext().getAuthentication().getName());

		try {
			// Make the actual API call
			String response = restClient.get()
				.uri("/posts/" + resourceId)
				.retrieve()
				.body(String.class);

			log.info("RestClient - Successfully fetched data");
			return response;
		} catch (RestClientException e) {
			log.error("RestClient - Error while fetching data: {}", e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error("RestClient - Unknown error while fetching data: {}", e.getMessage());
			// Re-throw to trigger retry
			throw e;
		}
	}

	@Retryable(retryFor = {RuntimeException.class}, maxAttempts = 2, backoff = @Backoff(delay = 1000, multiplier = 2))
	public String createPostWithRetry(Post post) {
		log.info("RestClient - Attempting to create post: {}", post);
		log.debug("RestClient - MDC context: {}", MDC.getCopyOfContextMap());

		try {
			String response = restClient.post()
				.uri("/posts")
				.body(post)
				.retrieve()
				.body(String.class);

			log.info("RestClient - Successfully created post");
			return response;
		} catch (RestClientException e) {
			log.error("RestClient - Error while creating post: {}", e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error("RestClient - Unknown error while creating post: {}", e.getMessage());
			throw e;
		}
	}

	@Retryable(retryFor = {RuntimeException.class}, maxAttempts = 2, backoff = @Backoff(delay = 1000, multiplier = 2))
	public String updatePostWithRetry(String resourceId, Post post) {
		log.info("RestClient - Attempting to update post with ID: {}", resourceId);
		log.debug("RestClient - MDC context: {}", MDC.getCopyOfContextMap());

		try {
			String response = restClient
				.put()
				.uri("/posts/" + resourceId)
				.body(post)
				.retrieve()
				.body(String.class);

			log.info("RestClient - Successfully updated post");
			return response;
		} catch (RestClientException e) {
			log.error("RestClient - Error while updating post: {}", e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error("RestClient - Unknown error while updating post: {}", e.getMessage());
			throw e;
		}
	}

	@Retryable(retryFor = {RuntimeException.class}, maxAttempts = 2, backoff = @Backoff(delay = 1000, multiplier = 2))
	public String deletePostWithRetry(String resourceId) {
		log.info("RestClient - Attempting to delete post with ID: {}", resourceId);
		log.debug("RestClient - MDC context: {}", MDC.getCopyOfContextMap());

		try {
			String response = restClient
				.delete()
				.uri("/posts/" + resourceId)
				.retrieve()
				.body(String.class);

			log.info("RestClient - Successfully deleted post");
			return response;
		} catch (RestClientException e) {
			log.error("RestClient - Error while deleting post: {}", e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error("RestClient - Unknown error while deleting post: {}", e.getMessage());
			throw e;
		}
	}
}
