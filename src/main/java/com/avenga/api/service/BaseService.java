package com.avenga.api.service;

import com.avenga.api.dto.ErrorResponseDto;
import com.avenga.config.TestContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import feign.Response;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

import static java.lang.StackWalker.Option.RETAIN_CLASS_REFERENCE;

/**
 * Represents an abstract parent service that contains some shared objects for other services
 */
public abstract class BaseService {

    protected final StackWalker stackWalker = StackWalker.getInstance(RETAIN_CLASS_REFERENCE);

    @Autowired
    protected TestContext testContext;

    @Autowired
    protected Faker faker;

    @Autowired
    protected ObjectMapper objectMapper;

    /**
     * <p>Reads and deserializes the error response body from a Feign {@link Response}
     * into an {@link ErrorResponseDto} object</p>
     * <p>This utility method is typically used to parse API error messages returned by the server</p>
     *
     * @param response еhe Feign {@link Response} object containing the error body
     * @return еn {@link ErrorResponseDto} object parsed from the response body
     * @throws RuntimeException if an I/O error occurs during response body reading or deserialization
     */
    protected ErrorResponseDto readErrorResponseBody(Response response) {
        try {
            return objectMapper.readValue(response.body().asInputStream(), ErrorResponseDto.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read/deserialize the response body into the ErrorResponse object");
        }
    }
}
