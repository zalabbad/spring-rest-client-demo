# RestClient Demo with Spring Retry

## Overview

This project demonstrates how to use Spring's RestClient (introduced in Spring Framework 6.1) with Spring Retry to address a common issue with WebClient's retry mechanism. It showcases how to maintain thread-local context (MDC, security context, etc.) during retry operations, which is a limitation when using WebClient's built-in retry functionality.

## Problem Statement

When using WebClient with its built-in `.retry()` functionality, retries occur in a separate thread, which causes the loss of:
- MDC (Mapped Diagnostic Context) information
- Authentication context
- Other thread-local data

This is problematic for applications that rely on this context information for logging, security, and other purposes.

## Solution

This demo shows how to use Spring's RestClient with Spring Retry to implement retries that preserve the MDC context and other thread-local data. Spring Retry executes retries in the same thread, ensuring that thread-local context is maintained across retry attempts.

## Key Features

### 1. RestClient with Spring Retry Integration

- **RestClient Configuration**: A simple configuration for RestClient with interceptors
- **Spring Retry Integration**: Using `@Retryable` annotation for retry functionality
- **Thread Continuity**: Spring Retry executes retries in the same thread, preserving thread-local context

### 2. Transaction ID Tracking

- **Unique Identifier**: Each request gets a UUID as its transaction ID
- **MDC Integration**: The tx_id is added to the MDC context and included in all log messages
- **Automatic Cleanup**: The filter removes the tx_id from MDC after the request is processed

### 3. Request Interceptors

#### Header Propagation Interceptor

- **Automatic Header Propagation**: Forwards all headers from the incoming request to outgoing requests
- **Request Context Access**: Uses Spring's RequestContextHolder to access the current request
- **Transparent Operation**: Works without any changes to service code

#### Logging Interceptor

- **Request Logging**: Logs the URI, method, headers, and body of each request
- **Structured Logging**: Formats logs in a consistent, readable way

### 4. Security Configuration

- **Basic Authentication**: The `/api/demo/posts/{id}` endpoint is protected with basic authentication
- **Security Context Preservation**: The security context is preserved during retries
- **Header Propagation**: Authentication headers are automatically forwarded to external services

### 5. Testing with WireMock

- **Mock External API**: Uses WireMock to simulate external API responses
- **Scenario Testing**: Configures different response scenarios for testing retry behavior
- **WebTestClient**: Uses WebTestClient for testing controller endpoints

This approach allows tests to automatically use the WireMock server without any code changes.

## Testing

The project includes tests that demonstrate the retry behavior and context preservation:

```bash
./gradlew test
```

The tests use WireMock to simulate different response scenarios:
1. **Success after retry**: The first request fails with a 500 error, but the retry succeeds
2. **Failure after all retries**: All requests fail with a 500 error

## Advantages Over WebClient

1. **Thread Continuity**: Spring Retry executes retries in the same thread, preserving thread-local context
2. **MDC Preservation**: Logging context is maintained across retry attempts
3. **Security Context Preservation**: Authentication information is preserved during retries
4. **Simpler Configuration**: Using `@Retryable` provides a clean, declarative way to configure retries
5. **Flexible Retry Policies**: Spring Retry offers various retry policies and backoff strategies
