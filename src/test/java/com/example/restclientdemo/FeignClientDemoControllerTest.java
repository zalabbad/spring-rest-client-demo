package com.example.restclientdemo;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import java.util.Base64;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test class for the FeignClientDemoController.
 * This demonstrates testing a controller that uses FeignClient to interact with an external API.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class FeignClientDemoControllerTest {

    private WireMockServer wireMockServer;
    private static final int WIREMOCK_PORT = 8089;

    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    void setup() {
        // Start WireMock server
        wireMockServer = new WireMockServer(WireMockConfiguration.options().port(WIREMOCK_PORT));
        wireMockServer.start();
        WireMock.configureFor("localhost", WIREMOCK_PORT);

        // Reset all stubs before each test
        WireMock.reset();

        MDC.clear();
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void testControllerWithRetry() {
        // Arrange
        String resourcePath = "/posts/3";
        String credentials = Base64.getEncoder().encodeToString("zayed:password".getBytes());

        // Configure WireMock to fail on first request, then succeed
        stubFor(get(urlEqualTo(resourcePath))
                .inScenario("retry scenario")
                .whenScenarioStateIs(Scenario.STARTED)
                .willReturn(aResponse()
                        .withStatus(500)
                        .withBody("Server Error"))
                .willSetStateTo("after first failure"));

        stubFor(get(urlEqualTo(resourcePath))
                .inScenario("retry scenario")
                .whenScenarioStateIs("after first failure")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("{\"id\":3,\"title\":\"FeignClient Retry Post\",\"body\":\"This response came after a retry with FeignClient\",\"userId\":1}")
                ));

        // Act & Assert
        webTestClient.get()
                .uri("/api/feign/posts/3")
                .headers(headers -> {
                    headers.add("X-Custom-Header", "custom-header-value");
                    headers.add("Authorization", "Basic " + credentials);
                })
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(response -> {
                    assertNotNull(response, "Response should not be null");
                    // Note: We can't assert on MDC values here because WebTestClient runs in a different thread
                    // The tx_id will be visible in the logs
                });

        // Verify the request was made to WireMock at least twice (initial + retry)
        verify(moreThanOrExactly(2), getRequestedFor(urlEqualTo(resourcePath)));
    }

    @Test
    void testControllerWithRetryAndFail() {
        // Arrange
        String resourcePath = "/posts/3";
        String credentials = Base64.getEncoder().encodeToString("zayed:password".getBytes());

        // Configure WireMock to always fail
        stubFor(get(urlEqualTo(resourcePath))
            .willReturn(aResponse()
                .withStatus(500)
                .withBody("Server Error")));

        // Act & Assert
        webTestClient.get()
            .uri("/api/feign/posts/3")
            .headers(headers -> {
                headers.add("X-Custom-Header", "custom-header-value");
                headers.add("Authorization", "Basic " + credentials);
            })
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().is5xxServerError();

        // Verify the request was made to WireMock at least three times (initial + 2 retries)
        verify(moreThanOrExactly(3), getRequestedFor(urlEqualTo(resourcePath)));
    }
}
