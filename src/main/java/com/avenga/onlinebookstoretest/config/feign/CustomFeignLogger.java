package com.avenga.onlinebookstoretest.config.feign;

import feign.Logger;
import feign.Request;
import feign.Response;
import io.qameta.allure.Attachment;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;

import java.io.IOException;

@Slf4j
public class CustomFeignLogger extends Logger {

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
    }

    @Attachment
    private String attachBody(String body) {
        return body;
    }
}
