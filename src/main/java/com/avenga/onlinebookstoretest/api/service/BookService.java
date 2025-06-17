package com.avenga.onlinebookstoretest.api.service;

import com.avenga.onlinebookstoretest.api.client.BookClient;
import com.avenga.onlinebookstoretest.api.dto.BookDto;
import io.qameta.allure.Step;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookService {

    private final BookClient bookClient;

    @Step("Get all the books")
    public List<BookDto> getBooks() {
        log.info("Getting all books");
        return bookClient.getBooks();
    }
}
