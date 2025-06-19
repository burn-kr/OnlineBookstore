package com.avenga.api.service;

import com.avenga.api.client.BookClient;
import com.avenga.api.dto.ErrorResponse;
import com.avenga.api.dto.book.BookDto;
import com.avenga.api.dto.book.BookField;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.avenga.api.dto.book.BookField.*;

/**
 * <p>A service class for managing the {@link BookDto} objects</p>
 * <p>It allows to prepare {@link BookDto} objects with random data and a specific fields that can be used for create/update operations</p>
 * <p>It also allows to perform actual CRUD operations with the {@link BookDto} objects</p>
 */
@Slf4j
@Service
public class BookService extends BaseService {

    private final BookClient bookClient;
    private int lastBookId;

    @Autowired
    public BookService(BookClient bookClient) {
        this.bookClient = bookClient;

        log.debug("Getting all the books");
        var existingBookList = bookClient.getBooks();

        // we only get the last id once then we just calculate it
        // assuming that no one else adds objects as the regression execution is scheduled for the late night
        log.debug("Looking for the max id of the existing books");
        lastBookId = Collections.max(existingBookList, Comparator.comparing(BookDto::getId)).getId();

        log.debug("The max book id is {}", lastBookId);
    }

    /**
     * Prepares a {@link BookDto} DTO with the calculated id and some random data
     *
     * @return a {@link BookDto} to create a book object
     */
    @Step("Create a random book request body")
    public BookDto prepareRandomBookDto() {
        return prepareRandomBookDto(++lastBookId);
    }

    /**
     * Prepares a {@link BookDto} DTO with the calculated id and some random data for the specified fields
     *
     * @param bookFields an array of {@link BookField}
     * @return a {@link BookDto} to create a book object
     */
    @Step("Create a random book request body")
    public BookDto prepareRandomBookDto(BookField... bookFields) {
        return prepareRandomBookDto(++lastBookId, bookFields);
    }

    /**
     * Prepares a {@link BookDto} DTO with the specified id and some random data for all the fields
     *
     * @param bookId the specific id of the {@link BookDto} object
     * @return a {@link BookDto} to create a book object
     */
    @Step("Create a book request body with random data for the book id {0}")
    public BookDto prepareRandomBookDto(int bookId) {
        log.info("Preparing a book dto with random data for id {}", bookId);
        return prepareRandomBookDto(bookId, ID, TITLE, DESCRIPTION, EXCERPT, PAGE_COUNT, PUBLISH_DATE);
    }

    /**
     * Prepares a {@link BookDto} DTO with the specified id and some random data for the specified fields
     *
     * @param bookId the specific id of the {@link BookDto} object
     * @param bookFields an array of {@link BookField}
     * @return a {@link BookDto} to create a book object
     */
    @Step("Create a book request body with random data for the certain fields for the book id {0}")
    public BookDto prepareRandomBookDto(int bookId, BookField... bookFields) {
        log.info("Building a book dto with random data for fields {}", Arrays.toString(bookFields));
        var bookDtoBuilder = BookDto.builder();

        Arrays.stream(bookFields).forEach(bookField -> {
            switch (bookField) {
                case ID -> bookDtoBuilder.id(bookId);
                case TITLE -> bookDtoBuilder.title(faker.book().title());
                case DESCRIPTION -> bookDtoBuilder.description(faker.lorem().sentence());
                case EXCERPT -> bookDtoBuilder.excerpt(faker.lorem().paragraph(3));
                case PAGE_COUNT -> bookDtoBuilder.pageCount(faker.random().nextInt(100, 1000));
                case PUBLISH_DATE -> bookDtoBuilder.publishDate(LocalDateTime.now().toString());
            }
        });

        return bookDtoBuilder.build();
    }

    /**
     * Retrieves the list of all the {@link BookDto} objects
     *
     * @return a list of {@link BookDto} objects
     */
    @Step("Get all the books")
    public List<BookDto> getBooks() {
        log.info("Getting all books");
        return bookClient.getBooks();
    }

    /**
     * Retrieves one {@link BookDto} object by its id
     *
     * @param bookId the id of the {@link BookDto} object
     * @return a {@link BookDto} object
     */
    @Step("Get the book by id {0}")
    public BookDto getBook(int bookId) {
        log.info("Getting the book with id {}", bookId);
        return bookClient.getBook(bookId);
    }

    /**
     * Retrieves book information by its ID, expecting an error response.
     * It attempts to deserialize the raw Feign HTTP response body into an {@link ErrorResponse}.
     *
     * @param bookId the id of the book to retrieve
     * @return an {@link ErrorResponse} object parsed from the HTTP response body
     * @throws RuntimeException if an I/O error occurs during response body reading or deserialization.
     */
    @Step("Get the book by id {0}")
    public ErrorResponse getBookRaw(int bookId) {
        log.info("Getting the book with id {}", bookId);
        var response = bookClient.getBookRaw(bookId);

        try {
            return objectMapper.readValue(response.body().asInputStream(), ErrorResponse.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read the response body");
        }
    }

    /**
     * <p>A single method to create a random {@link BookDto} object</p>
     * <p>First it prepares the {@link BookDto} object with some random data for the request.
     * Then it performs the request to actually create this object</p>
     * <p>When the object is created the method adds this object the cleanup list based on the test class
     * so that the {@link CleanUpService} could remove it after the test</p>
     *
     * @return created {@link BookDto} object
     */
    @Step("Create a new random book")
    public BookDto createRandomBook() {
        var randomBookDto = prepareRandomBookDto();
        var createdBookDto = createBook(randomBookDto);

        testContext.addToCleanUpList(createdBookDto, stackWalker.getCallerClass().getSimpleName());

        return createdBookDto;
    }

    /**
     * <p>Creates a prepared {@link BookDto} object</p>
     * <p>When the object is created the method adds this object the cleanup list based on the test class
     * so that the {@link CleanUpService} could remove it after the test</p>
     *
     * @param bookDto the prepared {@link BookDto} object to create
     * @return created {@link BookDto} object
     */
    @Step("Create a new book")
    public BookDto createBook(BookDto bookDto) {
        log.info("Creating a new book");

        var createdBookDto = bookClient.createBook(bookDto);

        if (stackWalker.getCallerClass() != this.getClass()) {
            testContext.addToCleanUpList(createdBookDto, stackWalker.getCallerClass().getSimpleName());
        }

        return createdBookDto;
    }

    /**
     * <p>Creates a prepared {@link BookDto} object expecting an error response.
     * It attempts to deserialize the raw Feign HTTP response body into an {@link ErrorResponse}.</p>
     * <p>If the object was created the method adds this object the cleanup list based on the test class
     * so that the {@link CleanUpService} could remove it after the test</p>
     *
     * @param bookDto the prepared {@link BookDto} object to create
     * @return an {@link ErrorResponse} object parsed from the HTTP response body
     * @throws RuntimeException if an I/O error occurs during response body reading or deserialization.
     */
    @Step("Create a new book")
    public ErrorResponse createBookRaw(BookDto bookDto) {
        log.info("Creating a new book");

        var response = bookClient.createBookRaw(bookDto);

        try {
            // in case there was no error and the book was created make sure we add it to the cleanup list
            if (response.status() == HttpStatus.OK.value()) {
                var json = IOUtils.toString(response.body().asInputStream(), Charsets.UTF_8);
                var book = new ObjectMapper().readValue(json, BookDto.class);

                testContext.addToCleanUpList(book, stackWalker.getCallerClass().getSimpleName());
            }

            return objectMapper.readValue(response.body().asInputStream(), ErrorResponse.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read the response body");
        }
    }

    /**
     * Updates the specified {@link BookDto} object
     *
     * @param bookDto {@link BookDto} to update
     * @return updated {@link BookDto} object
     */
    @Step("Update the book")
    public BookDto updateBook(BookDto bookDto) {
        var bookId = bookDto.getId();
        log.info("Updating the book with id {}", bookId);

        return bookClient.updateBook(bookId, bookDto);
    }

    /**
     * Updates the specified {@link BookDto} object and expects an {@link ErrorResponse}.
     * It handles the raw Feign HTTP response and attempts to deserialize the response body
     * into an {@link ErrorResponse}.
     *
     * @param bookDto {@link BookDto} to update
     * @return an {@link ErrorResponse} object parsed from the HTTP response body
     * @throws RuntimeException if an I/O error occurs during response body reading or deserialization
     */
    @Step("Update the book")
    public ErrorResponse updateBookRaw(BookDto bookDto) {
        var bookId = bookDto.getId();
        log.info("Updating the book with id {}", bookId);

        var response = bookClient.updateBookRaw(bookId, bookDto);

        try {
            return objectMapper.readValue(response.body().asInputStream(), ErrorResponse.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read the response body");
        }
    }

    /**
     * Deletes the specified {@link BookDto} object
     *
     * @param bookDto the {@link BookDto} object to delete
     */
    @Step("Delete the book")
    public void deleteBook(BookDto bookDto) {
        var bookId = bookDto.getId();
        log.info("Deleting the book with id {}", bookId);
        bookClient.deleteBook(bookId);
    }

    /**
     * Returns the last calculated id of the existing books
     *
     * @return last id of the books
     */
    @Step("Get the last id of the existing books")
    public int getLastBookId() {
        log.info("Returning the last book id ({})", lastBookId);
        return lastBookId;
    }
}
