package com.avenga.api.service;

import com.avenga.api.client.AuthorClient;
import com.avenga.api.dto.author.AuthorField;
import com.avenga.api.dto.book.BookDto;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.avenga.api.dto.author.AuthorDto;

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

        log.debug("Looking for the max id of the existing authors");
        lastAuthorId = Collections.max(existingAuthorList, Comparator.comparing(AuthorDto::getId)).getId();

        log.debug("The max author id is {}", lastAuthorId);
    }

    /**
     * Prepares a {@link AuthorDto} DTO with the calculated id, book id and some random data for all the fields
     *
     * @param bookId the specific id of the book
     * @return {@link AuthorDto} to create an author object
     */
    @Step("Create a random author request body")
    public AuthorDto prepareRandomAuthorDto(int bookId) {
        return prepareRandomAuthorDto(++lastAuthorId, bookId, ID, BOOK_ID, FIRST_NAME, LAST_NAME);
    }

    /**
     * Prepares a {@link AuthorDto} DTO with the calculated id, book id and some random data for the specified fields
     *
     * @param bookId the specific id of the book
     * @param authorFields an array of {@link AuthorField}
     * @return {@link AuthorDto} to create an author object
     */
    @Step("Create a random author request body")
    public AuthorDto prepareRandomAuthorDto(int bookId, AuthorField... authorFields) {
        return prepareRandomAuthorDto(++lastAuthorId, bookId, authorFields);
    }

    /**
     * Prepares a {@link AuthorDto} DTO with the specified id, book id and some random data for the specified fields
     *
     * @param authorId the specific id of the author
     * @param bookId the specific id of the book
     * @param authorFields an array of {@link AuthorField}
     * @return {@link AuthorDto} to create an author object
     */
    @Step("Create an author request body with random data for the certain fields for the author id {0} with book id {1}")
    public AuthorDto prepareRandomAuthorDto(int authorId, int bookId, AuthorField... authorFields) {
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
     * @return created {@link BookDto} object
     */
    @Step("Create a new random author")
    public AuthorDto createRandomAuthor(int bookId) {
        var randomAuthorDto = prepareRandomAuthorDto(bookId);
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
     * Retrieves one {@link AuthorDto} object by the book id
     *
     * @param book the {@link BookDto} of the author
     * @return an {@link AuthorDto} object
     */
    @Step("Get the author by book")
    public AuthorDto getAuthorByBook(BookDto book) {
        var bookId = book.getId();
        log.info("Getting the author by the book id {}", bookId);
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
}
