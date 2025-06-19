package com.example.restclientdemo.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.restclientdemo.model.Post;

/**
 * Feign client interface for the JSONPlaceholder API. This demonstrates a
 * declarative approach to REST API clients.
 */
@FeignClient(name = "jsonPlaceholder", url = "${api.base-url}", configuration = FeignClientConfig.class)
public interface JsonPlaceholderClient {

	/**
	 * Fetches a post by its ID.
	 *
	 * @param id
	 *            the ID of the post to fetch
	 *
	 * @return the post as a String
	 */
	@GetMapping("/posts/{id}")
	String getPostById(@PathVariable("id") String id);

	/**
	 * Creates a new post.
	 *
	 * @param post
	 *            the post to create
	 *
	 * @return the created post as a String
	 */
	@PostMapping("/posts")
	String createPost(@RequestBody Post post);

	/**
	 * Updates an existing post.
	 *
	 * @param id
	 *            the ID of the post to update
	 * @param post
	 *            the updated post data
	 *
	 * @return the updated post as a String
	 */
	@PutMapping("/posts/{id}")
	String updatePost(@PathVariable("id") String id, @RequestBody Post post);

	/**
	 * Deletes a post by its ID.
	 *
	 * @param id
	 *            the ID of the post to delete
	 *
	 * @return the deletion response as a String
	 */
	@DeleteMapping("/posts/{id}")
	String deletePost(@PathVariable("id") String id);
}
