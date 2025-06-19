package com.example.restclientdemo.model;

/** Model record representing a post from the JSONPlaceholder API. */
public record Post(Integer id, String title, String body, Integer userId) {
}
