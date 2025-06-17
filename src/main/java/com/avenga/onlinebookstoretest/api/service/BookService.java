package com.avenga.onlinebookstoretest.api.service;

import com.avenga.onlinebookstoretest.api.client.BookClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookService {

    private final BookClient bookClient;

    public void getBooks() {
        log.info("Getting all books");
        var books = bookClient.getBooks();
    }
}
