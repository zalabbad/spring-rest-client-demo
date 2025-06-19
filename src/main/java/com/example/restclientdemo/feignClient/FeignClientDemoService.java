package com.example.restclientdemo.feignClient;

import java.util.Collections;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import org.slf4j.MDC;

import com.example.restclientdemo.model.Post;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service that uses FeignClient to fetch data from the JSONPlaceholder API.
 * This demonstrates a declarative approach to REST API clients with retry
 * capabilities.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FeignClientDemoService {

	private final JsonPlaceholderClient jsonPlaceholderClient;
	private final HttpServletRequest httpServletRequest;

	/**
	 * Fetches data from the JSONPlaceholder API with retry capabilities.
	 *
	 * @param resourceId
	 *            the ID of the resource to fetch
	 *
	 * @return the fetched data as a String
	 */
	@Retryable(retryFor = {RuntimeException.class}, maxAttempts = 2, backoff = @Backoff(delay = 1000, multiplier = 2))
	public String fetchDataWithRetry(String resourceId) {
		log.info("FeignClient - Attempting to fetch data for resource ID: {}", resourceId);
		log.debug("FeignClient - MDC context: {}", MDC.getCopyOfContextMap());
		log.debug("FeignClient - Request headers: {}", Collections.list(httpServletRequest
			.getHeaderNames()));
		log.debug("FeignClient - Security context: {}",
			SecurityContextHolder.getContext().getAuthentication().getName());

		try {
			// Make the actual API call using the Feign client
			String response = jsonPlaceholderClient.getPostById(resourceId);

			log.info("FeignClient - Successfully fetched data");
			return response;
		} catch (Exception e) {
			log.error("FeignClient - Error while fetching data: {}", e.getMessage());
			// Re-throw to trigger retry
			throw e;
		}
	}

	/**
	 * Creates a new post with retry capabilities.
	 *
	 * @param post
	 *            the post to create
	 *
	 * @return the created post as a String
	 */
	@Retryable(retryFor = {RuntimeException.class}, maxAttempts = 2, backoff = @Backoff(delay = 1000, multiplier = 2))
	public String createPostWithRetry(Post post) {
		log.info("FeignClient - Attempting to create post: {}", post);
		log.debug("FeignClient - MDC context: {}", MDC.getCopyOfContextMap());

		try {
			String response = jsonPlaceholderClient.createPost(post);
			log.info("FeignClient - Successfully created post");
			return response;
		} catch (Exception e) {
			log.error("FeignClient - Error while creating post: {}", e.getMessage());
			throw e;
		}
	}

	/**
	 * Updates an existing post with retry capabilities.
	 *
	 * @param resourceId
	 *            the ID of the post to update
	 * @param post
	 *            the updated post data
	 *
	 * @return the updated post as a String
	 */
	@Retryable(retryFor = {RuntimeException.class}, maxAttempts = 2, backoff = @Backoff(delay = 1000, multiplier = 2))
	public String updatePostWithRetry(String resourceId, Post post) {
		log.info("FeignClient - Attempting to update post with ID: {}", resourceId);
		log.debug("FeignClient - MDC context: {}", MDC.getCopyOfContextMap());

		try {
			String response = jsonPlaceholderClient.updatePost(resourceId, post);
			log.info("FeignClient - Successfully updated post");
			return response;
		} catch (Exception e) {
			log.error("FeignClient - Error while updating post: {}", e.getMessage());
			throw e;
		}
	}

	/**
	 * Deletes a post with retry capabilities.
	 *
	 * @param resourceId
	 *            the ID of the post to delete
	 *
	 * @return the deletion response as a String
	 */
	@Retryable(retryFor = {RuntimeException.class}, maxAttempts = 2, backoff = @Backoff(delay = 1000, multiplier = 2))
	public String deletePostWithRetry(String resourceId) {
		log.info("FeignClient - Attempting to delete post with ID: {}", resourceId);
		log.debug("FeignClient - MDC context: {}", MDC.getCopyOfContextMap());

		try {
			String response = jsonPlaceholderClient.deletePost(resourceId);
			log.info("FeignClient - Successfully deleted post");
			return response;
		} catch (Exception e) {
			log.error("FeignClient - Error while deleting post: {}", e.getMessage());
			throw e;
		}
	}
}
