package com.example.restclientdemo.webClient;

import java.util.Collections;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import org.slf4j.MDC;

import com.example.restclientdemo.model.Post;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebClientDemoService {

	private final WebClient webClient;
	private final HttpServletRequest httpServletRequest;

	public String fetchDataWithRetry(String resourceId) {
		log.info("WebClient - Attempting to fetch data for resource ID: {}", resourceId);
		log.debug("WebClient - MDC context: {}", MDC.getCopyOfContextMap());
		log.debug("WebClient - Request headers: {}", Collections.list(httpServletRequest
			.getHeaderNames()));
		log.debug("WebClient - Security context: {}", SecurityContextHolder.getContext()
			.getAuthentication().getName());

		return webClient
			.get()
			.uri("/posts/" + resourceId)
			.retrieve()
			.bodyToMono(String.class)
			.doOnNext(response -> {
				log.info("WebClient - Successfully fetched data");
				log.debug("WebClient - MDC after success: {}", MDC.getCopyOfContextMap());
			})
			.doOnError(e -> {
				log.error("WebClient - Error while fetching data: {}", e.getMessage());
				log.debug("WebClient - MDC after error: {}", MDC.getCopyOfContextMap());
			})
			.retry(2)
			.block();
	}

	public String createPostWithRetry(Post post) {
		log.info("WebClient - Attempting to create post: {}", post);
		log.debug("WebClient - MDC context: {}", MDC.getCopyOfContextMap());

		return webClient
			.post()
			.uri("/posts")
			.bodyValue(post)
			.retrieve().bodyToMono(String.class)
			.doOnNext(response -> {
				log.info("WebClient - Successfully created post");
				log.debug("WebClient - MDC after success: {}", MDC.getCopyOfContextMap());
			})
			.doOnError(e -> {
				log.error("WebClient - Error while creating post: {}", e.getMessage());
				log.debug("WebClient - MDC after error: {}", MDC.getCopyOfContextMap());
			})
			.retry(2)
			.block();
	}

	public String updatePostWithRetry(String resourceId, Post post) {
		log.info("WebClient - Attempting to update post with ID: {}", resourceId);
		log.debug("WebClient - MDC context: {}", MDC.getCopyOfContextMap());

		return webClient
			.put()
			.uri("/posts/" + resourceId)
			.bodyValue(post)
			.retrieve()
			.bodyToMono(String.class)
			.doOnNext(response -> {
				log.info("WebClient - Successfully updated post");
				log.debug("WebClient - MDC after success: {}", MDC.getCopyOfContextMap());
			})
			.doOnError(e -> {
				log.error("WebClient - Error while updating post: {}", e.getMessage());
				log.debug("WebClient - MDC after error: {}", MDC.getCopyOfContextMap());
			})
			.retry(2)
			.block();
	}

	public String deletePostWithRetry(String resourceId) {
		log.info("WebClient - Attempting to delete post with ID: {}", resourceId);
		log.debug("WebClient - MDC context: {}", MDC.getCopyOfContextMap());

		return webClient
			.delete()
			.uri("/posts/" + resourceId)
			.retrieve()
			.bodyToMono(String.class)
			.doOnNext(response -> {
				log.info("WebClient - Successfully deleted post");
				log.debug("WebClient - MDC after success: {}", MDC.getCopyOfContextMap());
			})
			.doOnError(e -> {
				log.error("WebClient - Error while deleting post: {}", e.getMessage());
				log.debug("WebClient - MDC after error: {}", MDC.getCopyOfContextMap());
			})
			.retry(2)
			.block();
	}
}
