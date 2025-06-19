package com.example.restclientdemo.restClient;

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

@RestController
@RequestMapping("/api/restclient")
@RequiredArgsConstructor
@Slf4j
public class RestClientDemoController {

	private final RestClientDemoService restClientDemoService;

	@GetMapping("/posts/{id}")
	public ResponseEntity<String> getPost(
		@PathVariable String id,
		@RequestHeader("X-Custom-Header") String customHeader
	) {
		String userId = "user-123"; // Simulated user ID
		MDC.put("userId", userId);
		MDC.put("customHeader", customHeader);

		log.info("RestClient - Received request for post with ID: {}, tx_id: {}, userId: {}", id,
			MDC.get("tx_id"),
			userId);
		return ResponseEntity.ok(restClientDemoService.fetchDataWithRetry(id));
	}

	@PostMapping("/posts")
	public ResponseEntity<String> createPost(
		@RequestBody Post post,
		@RequestHeader("X-Custom-Header") String customHeader
	) {
		String userId = "user-123"; // Simulated user ID
		MDC.put("userId", userId);
		MDC.put("customHeader", customHeader);

		log.info("RestClient - Received request to create post: {}, tx_id: {}, userId: {}", post,
			MDC.get("tx_id"),
			userId);
		return ResponseEntity.ok(restClientDemoService.createPostWithRetry(post));
	}

	@PutMapping("/posts/{id}")
	public ResponseEntity<String> updatePost(
		@PathVariable String id, @RequestBody Post post,
		@RequestHeader("X-Custom-Header") String customHeader
	) {
		String userId = "user-123"; // Simulated user ID
		MDC.put("userId", userId);
		MDC.put("customHeader", customHeader);

		log.info("RestClient - Received request to update post with ID: {}, tx_id: {}, userId: {}",
			id,
			MDC.get("tx_id"), userId);
		return ResponseEntity.ok(restClientDemoService.updatePostWithRetry(id, post));
	}

	@DeleteMapping("/posts/{id}")
	public ResponseEntity<String> deletePost(
		@PathVariable String id,
		@RequestHeader("X-Custom-Header") String customHeader
	) {
		String userId = "user-123"; // Simulated user ID
		MDC.put("userId", userId);
		MDC.put("customHeader", customHeader);

		log.info("RestClient - Received request to delete post with ID: {}, tx_id: {}, userId: {}",
			id,
			MDC.get("tx_id"), userId);
		return ResponseEntity.ok(restClientDemoService.deletePostWithRetry(id));
	}
}
