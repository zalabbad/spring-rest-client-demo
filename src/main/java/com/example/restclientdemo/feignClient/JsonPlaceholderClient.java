package com.example.restclientdemo.feignClient;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client interface for the JSONPlaceholder API.
 * This demonstrates a declarative approach to REST API clients.
 */
@ConditionalOnProperty(name = "feign-client.enabled", havingValue = "true")
@FeignClient(name = "jsonPlaceholder", url = "${api.base-url}",
             configuration = FeignClientConfig.class)
public interface JsonPlaceholderClient {

    /**
     * Fetches a post by its ID.
     * 
     * @param id the ID of the post to fetch
     * @return the post as a String
     */
    @GetMapping("/posts/{id}")
    String getPostById(@PathVariable("id") String id);
}
