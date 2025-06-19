package com.avenga.api.service;

import com.avenga.api.client.AuthorClient;
import com.avenga.api.dto.ErrorResponseDto;
import com.avenga.api.dto.author.AuthorField;
import com.avenga.api.dto.book.BookDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.avenga.api.dto.author.AuthorDto;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.avenga.api.dto.author.AuthorField.*;

/**
 * <p>A service class for managing the {@link AuthorDto} objects</p>
 * <p>It allows to prepare {@link AuthorDto} objects with random data and a specific fields that can be used for create/update operations</p>
 * <p>It also allows to perform actual CRUD operations with the {@link AuthorDto} objects</p>
 */
@Slf4j
@Service
public class AuthorService extends BaseService {

    private final AuthorClient authorClient;
    private int lastAuthorId;

    @Autowired
    public AuthorService(AuthorClient authorClient) {
        this.authorClient = authorClient;

        log.debug("Getting all the authors");
        var existingAuthorList = authorClient.getAuthors();

        // we only get the last id once then we just calculate it
        // assuming that no one else adds objects as the regression execution is scheduled for the late night
        log.debug("Looking for the max id of the existing authors");
        lastAuthorId = Collections.max(existingAuthorList, Comparator.comparing(AuthorDto::getId)).getId();

        log.debug("The max author id is {}", lastAuthorId);
    }

    /**
     * Prepares a {@link AuthorDto} DTO with the calculated id, book id and some random data for all the fields
     *
     * @param book the specific {@link BookDto} object related to the author
     * @return {@link AuthorDto} to create an author object
     */
    @Step("Create a random author request body")
    public AuthorDto prepareRandomAuthorDto(BookDto book) {
        return prepareRandomAuthorDto(++lastAuthorId, book, ID, BOOK_ID, FIRST_NAME, LAST_NAME);
    }

    /**
     * Prepares a {@link AuthorDto} DTO with the calculated id, book id and some random data for the specified fields
     *
     * @param book the specific {@link BookDto} object related to the author
     * @param authorFields an array of {@link AuthorField}
     * @return {@link AuthorDto} to create an author object
     */
    @Step("Create a random author request body")
    public AuthorDto prepareRandomAuthorDto(BookDto book, AuthorField... authorFields) {
        return prepareRandomAuthorDto(++lastAuthorId, book, authorFields);
    }

    /**
     * Prepares a {@link AuthorDto} DTO with the specified id, book id and some random data for the specified fields
     *
     * @param authorId the specific id of the author
     * @param book the specific {@link BookDto} object related to the author
     * @param authorFields an array of {@link AuthorField}
     * @return {@link AuthorDto} to create an author object
     */
    @Step("Create an author request body with random data for the certain fields for the author id {0} with book {1}")
    public AuthorDto prepareRandomAuthorDto(int authorId, BookDto book, AuthorField... authorFields) {
        var bookId = book.getId();
        log.info("Building an author dto with id {}, bookId {} and some random data for fields {}",
                Arrays.toString(authorFields), authorId, bookId);
        var authorBuilder = AuthorDto.builder();

        Arrays.stream(authorFields).forEach(authorField -> {
            switch (authorField) {
                case ID -> authorBuilder.id(authorId);
                case BOOK_ID -> authorBuilder.bookId(bookId);
                case FIRST_NAME -> authorBuilder.firstName(faker.name().firstName());
                case LAST_NAME -> authorBuilder.lastName(faker.name().lastName());
            }
        });

        return authorBuilder.build();
    }

    /**
     * <p>A single method to create a random {@link AuthorDto} object</p>
     * <p>First it prepares the {@link AuthorDto} object with the book id and some random data for the request.
     * Then it performs the request to actually create this object</p>
     * <p>When the object is created the method adds this object the cleanup list based on the test class
     * so that the {@link CleanUpService} could remove it after the test</p>
     *
     * @param book the specific {@link BookDto} object related to the author
     * @return created {@link AuthorDto} object
     */
    @Step("Create a new random author")
    public AuthorDto createRandomAuthor(BookDto book) {
        var randomAuthorDto = prepareRandomAuthorDto(book);
        var createdAuthorDto = createAuthor(randomAuthorDto);

        testContext.addToCleanUpList(createdAuthorDto, stackWalker.getCallerClass().getSimpleName());

        return createdAuthorDto;
    }

    /**
     * <p>Creates a prepared {@link AuthorDto} object</p>
     * <p>When the object is created the method adds this object the cleanup list based on the test class
     * so that the {@link CleanUpService} could remove it after the test</p>
     *
     * @param authorDto the prepared {@link AuthorDto} object to create
     * @return created {@link AuthorDto} object
     */
    @Step("Create a new author")
    public AuthorDto createAuthor(AuthorDto authorDto) {
        log.info("Creating a new author");

        var createdAuthorDto = authorClient.createAuthor(authorDto);

        if (stackWalker.getCallerClass() != this.getClass()) {
            testContext.addToCleanUpList(createdAuthorDto, stackWalker.getCallerClass().getSimpleName());
        }

        return createdAuthorDto;
    }

    /**
     * <p>Creates a prepared {@link AuthorDto} object expecting an error response.
     * It attempts to deserialize the raw Feign HTTP response body into an {@link ErrorResponseDto}.</p>
     * <p>If the object was created the method adds this object the cleanup list based on the test class
     * so that the {@link CleanUpService} could remove it after the test</p>
     *
     * @param authorDto the prepared {@link AuthorDto} object to create
     * @return an {@link ErrorResponseDto} object parsed from the HTTP response body
     * @throws RuntimeException if an I/O error occurs during response body reading or deserialization.
     */
    @Step("Create a new author")
    public ErrorResponseDto createAuthorRaw(AuthorDto authorDto) {
        log.info("Creating a new author");

        var response = authorClient.createAuthorRaw(authorDto);

        try {
            // in case there was no error and the book was created make sure we add it to the cleanup list
            if (response.status() == HttpStatus.OK.value()) {
                var json = IOUtils.toString(response.body().asInputStream(), Charsets.UTF_8);
                var author = new ObjectMapper().readValue(json, AuthorDto.class);

                testContext.addToCleanUpList(author, stackWalker.getCallerClass().getSimpleName());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read/deserialize the response body into the AuthorDto object");
        }

        return readErrorResponseBody(response);
    }

    /**
     * Retrieves the list of all the {@link AuthorDto} objects
     *
     * @return a list of {@link AuthorDto} objects
     */
    @Step("Get all the authors")
    public List<AuthorDto> getAuthors() {
        log.info("Getting all authors");
        return authorClient.getAuthors();
    }

    /**
     * Retrieves one {@link AuthorDto} object by its id
     *
     * @param authorId the id of the {@link AuthorDto} object
     * @return an {@link AuthorDto} object
     */
    @Step("Get the author by id {0}")
    public AuthorDto getAuthor(int authorId) {
        log.info("Getting the author with id {}", authorId);
        return authorClient.getAuthor(authorId);
    }

    /**
     * Retrieves an author information by his ID, expecting an error response.
     * It attempts to deserialize the raw Feign HTTP response body into an {@link ErrorResponseDto}.
     *
     * @param authorId the id of the author to retrieve
     * @return an {@link ErrorResponseDto} object parsed from the HTTP response body
     * @throws RuntimeException if an I/O error occurs during response body reading or deserialization.
     */
    @Step("Get the author by id {0}")
    public ErrorResponseDto getAuthorRaw(int authorId) {
        log.info("Getting the author with id {}", authorId);
        var response = authorClient.getAuthorRaw(authorId);

        return readErrorResponseBody(response);
    }

    /**
     * Retrieves the list of {@link AuthorDto} object by the book id
     *
     * @param book the {@link BookDto} of the author
     * @return an {@link AuthorDto} object
     */
    @Step("Get the authors by book")
    public List<AuthorDto> getAuthorsByBook(BookDto book) {
        var bookId = book.getId();
        log.info("Getting the authors by the book id {}", bookId);
        return authorClient.getAuthorByBook(bookId);
    }

    /**
     * Updates the specified {@link AuthorDto} object
     *
     * @param authorDto {@link AuthorDto} to update
     * @return updated {@link AuthorDto} object
     */
    @Step("Update the author")
    public AuthorDto updateAuthor(AuthorDto authorDto) {
        var authorId = authorDto.getId();
        log.info("Updating the author with id {}", authorId);

        return authorClient.updateAuthor(authorId, authorDto);
    }

    /**
     * Updates the specified {@link AuthorDto} object and expects an {@link ErrorResponseDto}.
     * It handles the raw Feign HTTP response and attempts to deserialize the response body
     * into an {@link ErrorResponseDto}.
     *
     * @param authorDto {@link AuthorDto} to update
     * @return an {@link ErrorResponseDto} object parsed from the HTTP response body
     * @throws RuntimeException if an I/O error occurs during response body reading or deserialization
     */
    @Step("Update the author")
    public ErrorResponseDto updateAuthorRaw(AuthorDto authorDto) {
        var authorId = authorDto.getId();
        log.info("Updating the author with id {}", authorId);

        var response = authorClient.updateAuthorRaw(authorId, authorDto);

        return readErrorResponseBody(response);
    }

    /**
     * Deletes the specified {@link AuthorDto} object
     *
     * @param authorDto the {@link AuthorDto} object to delete
     */
    @Step("Delete the author")
    public void deleteAuthor(AuthorDto authorDto) {
        var authorId = authorDto.getId();
        log.info("Deleting the author with id {}", authorId);
        authorClient.deleteAuthor(authorId);
    }

    /**
     * Returns the last calculated id of the existing authors
     *
     * @return last id of the authors
     */
    @Step("Get the last id of the existing authors")
    public int getLastAuthorId() {
        log.info("Returning the last author id ({})", lastAuthorId);
        return lastAuthorId;
    }
}
