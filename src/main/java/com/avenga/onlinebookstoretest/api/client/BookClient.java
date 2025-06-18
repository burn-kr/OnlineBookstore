package com.avenga.onlinebookstoretest.api.client;

import com.avenga.onlinebookstoretest.api.dto.BookDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "book-client", url = "${app.url}")
public interface BookClient {

    String BASE_BOOKS_URL = "/Books";
    String BOOK_URL = BASE_BOOKS_URL + "/{id}";

    @GetMapping(BASE_BOOKS_URL)
    List<BookDto> getBooks();

    @GetMapping(BOOK_URL)
    BookDto getBook(@PathVariable int id);

    @PostMapping(BASE_BOOKS_URL)
    BookDto createBook(BookDto bookDto);

    @PutMapping(BOOK_URL)
    BookDto updateBook(@PathVariable int id, BookDto bookDto);

    @DeleteMapping(BOOK_URL)
    void deleteBook(@PathVariable int id);
}
