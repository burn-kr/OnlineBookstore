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

    public BookDto createRandomBookDto() {
        return BookDto.builder()
                .title("")
                .description("")
                .excerpt("")
                .pageCount(0)
                .publishDate(LocalDateTime.now().toString())
                .build();
    }

    @Step("Get all the books")
    public List<BookDto> getBooks() {
        log.info("Getting all books");
        return bookClient.getBooks();
    }

    public BookDto createBook(BookDto bookDto) {
        log.info("Creating a new book");

        testContext.addToCleanUpList(bookDto, stackWalker.getCallerClass().getSimpleName());

        return bookDto;
    }

    public void deleteBook(Long bookId) {
        log.info("Deleting a book with id {}", bookId);
    }
}
