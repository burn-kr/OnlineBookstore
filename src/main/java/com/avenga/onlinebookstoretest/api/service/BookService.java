package com.avenga.onlinebookstoretest.api.service;

import com.avenga.onlinebookstoretest.api.client.BookClient;
import com.avenga.onlinebookstoretest.api.dto.BookDto;
import io.qameta.allure.Step;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookService extends BaseService {

    private final BookClient bookClient;

    @Step("Create a book request body with random data")
    public BookDto prepareRandomBookDto() {
        return BookDto.builder()
                .title(faker.book().title())
                .description(faker.lorem().sentence())
                .excerpt(faker.lorem().paragraph(3))
                .pageCount(faker.random().nextInt(100, 1000))
                .publishDate(LocalDateTime.now().toString())
                .build();
    }

    @Step("Get all the books")
    public List<BookDto> getBooks() {
        log.info("Getting all books");
        return bookClient.getBooks();
    }

    @Step("Get the book by id")
    public BookDto getBook(int bookId) {
        log.info("Getting the book with id {}", bookId);

        return bookClient.getBook(bookId);
    }

    @Step("Create a new book")
    public BookDto createBook(BookDto bookDto) {
        log.info("Creating a new book");

        var createdBookDto = bookClient.createBook(bookDto);
        testContext.addToCleanUpList(createdBookDto, stackWalker.getCallerClass().getSimpleName());

        return bookDto;
    }

    @Step("Update the book")
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
}
