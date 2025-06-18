package com.avenga.onlinebookstoretest.api.client;

import com.avenga.onlinebookstoretest.api.dto.book.BookDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Represents the HTTP client with the CRUD operations for the /Books endpoints
 */
@FeignClient(name = "book-client", url = "${app.url}")
public interface BookClient {

    String BASE_BOOKS_URL = "/Books";
    String BOOK_URL = BASE_BOOKS_URL + "/{id}";

    @GetMapping(BASE_BOOKS_URL)
    List<BookDto> getBooks();

    @GetMapping(BOOK_URL)
    BookDto getBook(@PathVariable("id") int id);

    @PostMapping(BASE_BOOKS_URL)
    BookDto createBook(BookDto bookDto);

    @PutMapping(BOOK_URL)
    BookDto updateBook(@PathVariable("id") int id, BookDto bookDto);

    @DeleteMapping(BOOK_URL)
    void deleteBook(@PathVariable("id") int id);
}
