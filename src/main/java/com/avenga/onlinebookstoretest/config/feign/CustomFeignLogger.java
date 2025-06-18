package com.avenga.onlinebookstoretest.config.feign;

import feign.Logger;
import feign.Request;
import feign.Response;
import io.qameta.allure.Attachment;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;

import java.io.IOException;

/**
 * Custom logger for the Feign HTTP clients. Makes the request/response logging more readable
 */
@Slf4j
public class CustomFeignLogger extends Logger {

    /**
     * Logs the HTTP request in a more readable way
     *
     * <p>This method overrides the standard behavior of {@code feign.Logger#logRequest}
     * to output the formatted request to logs and create an Allure attachment
     *
     * @param configKey The unique configuration key for the Feign client (typically the class name)
     * @param logLevel The logging level applicable to this specific request
     * @param request The {@link Request} object containing all HTTP request details
     */
    @Override
    protected void logRequest(String configKey, Level logLevel, Request request) {
        var requestBody = request.charset() == null ? "<No Content>" : new String(request.body(), request.charset());
        var authHeader = request.headers().get(HttpHeaders.AUTHORIZATION) == null
                ? "<No Authentication header>"
                : request.headers()
                .get(HttpHeaders.AUTHORIZATION)
                .stream()
                .findFirst()
                .orElse("<No Authentication header>");

        log.info("__________ REQUEST __________");
        log.info("{} {}", request.httpMethod().name(), request.url());
        log.info("Authorization: {}", authHeader);
        log.info("Body: {}", requestBody);
        log.info("__________   END   __________");

        attachBody(requestBody);
    }

    /**
     * Logs the HTTP response in a more readable way
     *
     * <p>This overrides the standard {@code feign.Logger#logAndRebufferResponse}
     * to provide custom logging and Allure attachment functionality
     *
     * @param configKey The unique configuration key for the Feign client (typically the class name)
     * @param logLevel The logging level applicable to this specific response
     * @param response The {@link Response} object containing all HTTP response details
     * @param elapsedTime The time in milliseconds taken to receive the response
     * @return The original or a re-buffered {@link Response} object
     * @throws IOException IOException If an I/O error occurs during re-buffering the response body
     */
    @Override
    protected Response logAndRebufferResponse(String configKey, Level logLevel, Response response, long elapsedTime)
            throws IOException {
        var responseBody = response.body() == null
                ? "<No Content>"
                : IOUtils.toString(response.body().asInputStream(), response.charset());

        log.info("__________ RESPONSE _________");
        log.info("Status: {}", response.status());
        log.info("Body: {}", responseBody);
        log.info("__________   END   __________");

        attachBody(responseBody);

        return response.toBuilder().body(responseBody, response.charset()).build();
    }

    @Override
    protected void log(String configKey, String format, Object... args) {
        // Intentionally left blank to prevent default logging if custom logRequest/logAndRebufferResponse are used
    }

    /**
     * Attaches the given string content as an attachment to the Allure report
     *
     * @param body The string content (request/response body) to be attached
     * @return The same string content that was attached
     */
    @Attachment
    private String attachBody(String body) {
        return body;
    }
}
