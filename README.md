# RestClient vs WebClient: Solving the Retry Context Loss Problem

## Problem Statement: The WebClient Retry Issue

When building resilient web applications, retry mechanisms are essential for handling transient failures. However, WebClient's built-in `.retry()` functionality has a critical limitation:

**WebClient retries occur in different threads, causing the loss of thread-local context.**

This means that during retries, you lose:
- **MDC (Mapped Diagnostic Context)** - Your logging context disappears
- **Security Context** - Authentication information is lost
- **Request Context** - Headers and other request-specific data vanish

This is a significant problem for applications that rely on this context information for:
- **Tracing requests** across multiple services
- **Logging with correlation IDs**
- **Maintaining authentication** during retries
- **Propagating headers** to downstream services

## Solution: RestClient with Spring Retry

This project demonstrates how to use Spring's RestClient (introduced in Spring Framework 6.1) with Spring Retry to solve the context loss problem. The key advantage is:

**Spring Retry executes retries in the same thread, preserving all thread-local context.**

The project includes both implementations (RestClient and WebClient) with identical features to clearly demonstrate the differences in behavior, particularly regarding thread-local context preservation during retries.

## Side-by-Side Comparison

| Feature | WebClient with `.retry()` | RestClient with Spring Retry |
|---------|---------------------------|------------------------------|
| **Thread Behavior** | Switches threads during retries | Maintains same thread during retries |
| **MDC Context** | ❌ Lost during retries | ✅ Preserved during retries |
| **Security Context** | ❌ Lost during retries | ✅ Preserved during retries |
| **Request Headers** | Can be propagated initially, lost in retries | Consistently propagated in all attempts |
| **Implementation** | Reactive (non-blocking) | Synchronous (blocking) |
| **Configuration** | Programmatic | Declarative with `@Retryable` |

## Project Structure

The project implements the same functionality using both RestClient and WebClient to demonstrate the differences:

```
src/main/java/com/example/restclientdemo/
├── RestClientDemoApplication.java
├── config/
│   ├── SecurityConfig.java           # Basic auth configuration
│   └── TransactionIdFilter.java      # Adds tx_id to MDC
├── restClient/                       # RestClient implementation
│   ├── RestClientConfig.java
│   ├── RestClientDemoController.java
│   ├── RestClientDemoService.java    # Uses @Retryable
│   ├── HeaderPropagationInterceptor.java
│   └── RestClientLoggingInterceptor.java
└── webClient/                        # WebClient implementation
    ├── WebClientConfig.java
    ├── WebClientDemoController.java
    ├── WebClientDemoService.java     # Uses .retry()
    ├── WebClientHeaderPropagationFilter.java
    └── WebClientLoggingFilter.java
```

## Implementation Details

### RestClient Implementation

The RestClient implementation uses Spring Retry's `@Retryable` annotation to handle retries:

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class RestClientDemoService {

    private final RestClient restClient;
    private final HttpServletRequest httpServletRequest;

    @Retryable(
            retryFor = {RuntimeException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public String fetchDataWithRetry(String resourcePath) {
        log.info("RestClient - Attempting to fetch data from {}", resourcePath);
        log.debug("RestClient - MDC context: {}", MDC.getCopyOfContextMap());
        
        // Make the API call
        String response = restClient.get()
            .uri(resourcePath)
            .retrieve()
            .body(String.class);
            
        log.info("RestClient - Successfully fetched data");
        return response;
    }
}
```

### WebClient Implementation

The WebClient implementation uses the built-in `.retry()` method:

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class WebClientDemoService {

    private final WebClient webClient;
    private final HttpServletRequest httpServletRequest;

    public String fetchDataWithRetry(String resourcePath) {
        log.info("WebClient - Attempting to fetch data from {}", resourcePath);
        log.debug("WebClient - MDC context: {}", MDC.getCopyOfContextMap());
        
        return webClient.get()
                .uri(resourcePath)
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(response -> {
                    log.info("WebClient - Successfully fetched data");
                    log.debug("WebClient - MDC after success: {}", MDC.getCopyOfContextMap());
                })
                // This is where the issue occurs - retry happens in a different thread
                .retry(2)
                .block();
    }
}
```

### Key Features

Both implementations include:

1. **Transaction ID Tracking**
   - Each request gets a UUID as its transaction ID
   - The ID is added to MDC and included in all log messages

2. **Header Propagation**
   - All headers from incoming requests are forwarded to outgoing requests
   - Uses Spring's RequestContextHolder to access the current request

3. **Request Logging**
   - Logs the URI, method, headers, and body of each request
   - Uses a consistent format for easy troubleshooting

4. **Security with Basic Authentication**
   - Protected endpoints require authentication
   - Authentication headers are automatically forwarded

## Tests That Demonstrate the Difference

The project includes tests that clearly demonstrate the context preservation difference:

### RestClientDemoControllerTest

This test shows that RestClient maintains context during retries:

```java
@Test
void testControllerWithRetry() {
    // Configure WireMock to fail on first request, then succeed
    stubFor(get(urlEqualTo("/posts/2"))
            .inScenario("retry scenario")
            .whenScenarioStateIs(Scenario.STARTED)
            .willReturn(aResponse().withStatus(500))
            .willSetStateTo("after first failure"));

    stubFor(get(urlEqualTo("/posts/2"))
            .inScenario("retry scenario")
            .whenScenarioStateIs("after first failure")
            .willReturn(aResponse().withStatus(200).withBody("{...}")));

    // Make request with custom header and auth
    webTestClient.get()
            .uri("/api/demo/posts/2")
            .headers(headers -> {
                headers.add("X-Custom-Header", "custom-value");
                headers.add("Authorization", "Basic " + credentials);
            })
            .exchange()
            .expectStatus().isOk();

    // Verify the request was made to WireMock at least twice (initial + retry)
    verify(moreThanOrExactly(2), getRequestedFor(urlEqualTo("/posts/2")));
}
```

### WebClientDemoControllerTest

This test demonstrates that WebClient loses context during retries:

```java
@Test
void testWebClientControllerWithRetry() {
    // Same WireMock configuration as above
    
    // Make request with custom header and auth
    webTestClient.get()
            .uri("/api/webclient/posts/2")
            .headers(headers -> {
                headers.add("X-Custom-Header", "custom-value");
                headers.add("Authorization", "Basic " + credentials);
            })
            .exchange()
            .expectStatus().isOk();

    // Verify the request was made to WireMock at least twice (initial + retry)
    verify(moreThanOrExactly(2), getRequestedFor(urlEqualTo("/posts/2")));
}
```

### What the Tests Show

When you run these tests and examine the logs:

1. **RestClient Test**: You'll see that the MDC context (tx_id, userId) and security context are preserved across all retry attempts.

2. **WebClient Test**: You'll notice that the MDC context and security context are present in the initial request but lost during retry attempts.

This clearly demonstrates the fundamental difference between the two approaches.

## Running the Tests

To run the tests and see the difference in behavior:

```bash
./gradlew test
```

The tests use WireMock to simulate different response scenarios:
1. **Success after retry**: The first request fails with a 500 error, but the retry succeeds
2. **Failure after all retries**: All requests fail with a 500 error

## Why RestClient with Spring Retry is Better

1. **Thread Continuity**: Spring Retry executes retries in the same thread, preserving thread-local context
2. **MDC Preservation**: Logging context is maintained across retry attempts
3. **Security Context Preservation**: Authentication information is preserved during retries
4. **Simpler Configuration**: Using `@Retryable` provides a clean, declarative way to configure retries
5. **Flexible Retry Policies**: Spring Retry offers various retry policies and backoff strategies
6. **Synchronous Operation**: RestClient is synchronous by default, which is often simpler to work with
7. **Familiar API**: RestClient's API is similar to RestTemplate, making it easier to adopt

## When to Use WebClient

WebClient still has its place:
- When you need non-blocking, reactive programming
- When you don't rely on thread-local context
- When you implement custom context propagation mechanisms
- When maximum throughput is more important than context preservation
