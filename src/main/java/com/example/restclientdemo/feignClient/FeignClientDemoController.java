package com.example.restclientdemo.feignClient;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.MDC;

import com.example.restclientdemo.model.Post;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller that demonstrates the use of FeignClient to fetch data from an
 * external API. This shows a declarative approach to REST API clients.
 */
@RestController
@RequestMapping("/api/feign")
@RequiredArgsConstructor
@Slf4j
public class FeignClientDemoController {

	private final FeignClientDemoService feignClientDemoService;

	/**
	 * Fetches a post by its ID using FeignClient.
	 *
	 * @param id
	 *            the ID of the post to fetch
	 * @param customHeader
	 *            a custom header for demonstration purposes
	 *
	 * @return the post as a String
	 */
	@GetMapping("/posts/{id}")
	public ResponseEntity<String> getPost(
		@PathVariable String id,
		@RequestHeader("X-Custom-Header") String customHeader
	) {
		String userId = "user-123"; // Simulated user ID
		MDC.put("userId", userId);
		MDC.put("customHeader", customHeader);

		log.info("FeignClient - Received request for post with ID: {}, tx_id: {}, userId: {}", id,
			MDC.get("tx_id"),
			userId);
		return ResponseEntity.ok(feignClientDemoService.fetchDataWithRetry(id));
	}

	/**
	 * Creates a new post using FeignClient.
	 *
	 * @param post
	 *            the post to create
	 * @param customHeader
	 *            a custom header for demonstration purposes
	 *
	 * @return the created post as a String
	 */
	@PostMapping("/posts")
	public ResponseEntity<String> createPost(
		@RequestBody Post post,
		@RequestHeader("X-Custom-Header") String customHeader
	) {
		String userId = "user-123"; // Simulated user ID
		MDC.put("userId", userId);
		MDC.put("customHeader", customHeader);

		log.info("FeignClient - Received request to create post: {}, tx_id: {}, userId: {}", post,
			MDC.get("tx_id"),
			userId);
		return ResponseEntity.ok(feignClientDemoService.createPostWithRetry(post));
	}

	/**
	 * Updates an existing post using FeignClient.
	 *
	 * @param id
	 *            the ID of the post to update
	 * @param post
	 *            the updated post data
	 * @param customHeader
	 *            a custom header for demonstration purposes
	 *
	 * @return the updated post as a String
	 */
	@PutMapping("/posts/{id}")
	public ResponseEntity<String> updatePost(
		@PathVariable String id, @RequestBody Post post,
		@RequestHeader("X-Custom-Header") String customHeader
	) {
		String userId = "user-123"; // Simulated user ID
		MDC.put("userId", userId);
		MDC.put("customHeader", customHeader);

		log.info("FeignClient - Received request to update post with ID: {}, tx_id: {}, userId: {}",
			id,
			MDC.get("tx_id"), userId);
		return ResponseEntity.ok(feignClientDemoService.updatePostWithRetry(id, post));
	}

	/**
	 * Deletes a post using FeignClient.
	 *
	 * @param id
	 *            the ID of the post to delete
	 * @param customHeader
	 *            a custom header for demonstration purposes
	 *
	 * @return the deletion response as a String
	 */
	@DeleteMapping("/posts/{id}")
	public ResponseEntity<String> deletePost(
		@PathVariable String id,
		@RequestHeader("X-Custom-Header") String customHeader
	) {
		String userId = "user-123"; // Simulated user ID
		MDC.put("userId", userId);
		MDC.put("customHeader", customHeader);

		log.info("FeignClient - Received request to delete post with ID: {}, tx_id: {}, userId: {}",
			id,
			MDC.get("tx_id"), userId);
		return ResponseEntity.ok(feignClientDemoService.deletePostWithRetry(id));
	}
}
