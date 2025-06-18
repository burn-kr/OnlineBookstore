package com.avenga.onlinebookstoretest.api.service;

import com.avenga.onlinebookstoretest.api.client.BookClient;
import com.avenga.onlinebookstoretest.api.dto.book.BookDto;
import com.avenga.onlinebookstoretest.api.dto.book.BookField;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.avenga.onlinebookstoretest.api.dto.book.BookField.*;

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

        log.debug("Looking for the max id of the existing books");
        lastBookId = Collections.max(existingBookList, Comparator.comparing(BookDto::getId)).getId();

        log.debug("The max book id is {}", lastBookId);
    }

    @Step("Prepare a random book request body")
    public BookDto prepareRandomBookDto() {
        return prepareRandomBookDto(++lastBookId);
    }

    @Step("Prepare a random book request body")
    public BookDto prepareRandomBookDto(BookField... bookFields) {
        return prepareRandomBookDto(++lastBookId, bookFields);
    }

    @Step("Create a book request body with random data for the book id {0}")
    public BookDto prepareRandomBookDto(int bookId) {
        log.info("Preparing a book dto with random data for id {}", bookId);
        return prepareRandomBookDto(bookId, ID, TITLE, DESCRIPTION, EXCERPT, PAGE_COUNT, PUBLISH_DATE);
    }

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

    @Step("Get all the books")
    public List<BookDto> getBooks() {
        log.info("Getting all books");
        return bookClient.getBooks();
    }

    @Step("Get the book by id {0}")
    public BookDto getBook(int bookId) {
        log.info("Getting the book with id {}", bookId);

        return bookClient.getBook(bookId);
    }

    @Step("Create a random book")
    public BookDto createRandomBook() {
        var randomBookDto = prepareRandomBookDto();
        var createdBookDto = createBook(randomBookDto);

        testContext.addToCleanUpList(createdBookDto, stackWalker.getCallerClass().getSimpleName());

        return createdBookDto;
    }

    @Step("Create a new book")
    public BookDto createBook(BookDto bookDto) {
        log.info("Creating a new book");

        var createdBookDto = bookClient.createBook(bookDto);

        if (stackWalker.getCallerClass() != this.getClass()) {
            testContext.addToCleanUpList(createdBookDto, stackWalker.getCallerClass().getSimpleName());
        }

        return createdBookDto;
    }

    @Step("Update the book with")
    public BookDto updateBook(BookDto bookDto) {
        var bookId = bookDto.getId();
        log.info("Updating the book with id {}", bookId);

        return bookClient.updateBook(bookId, bookDto);
    }

    @Step("Delete the book")
    public void deleteBook(BookDto bookDto) {
        var bookId = bookDto.getId();
        log.info("Deleting a book with id {}", bookId);
        bookClient.deleteBook(bookId);
    }

    @Step("Get the last id of the existing books")
    public int getLastBookId() {
        log.info("Returning the last book id ({})", lastBookId);
        return lastBookId;
    }
}
