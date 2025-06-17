package com.avenga.onlinebookstoretest.api.client;

import com.avenga.onlinebookstoretest.api.dto.BookDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "book-client", url = "${app.url}")
public interface BookClient {

    String BASE_URL = "/Books";
    String BOOK_URL = BASE_URL + "/{id}";

    @GetMapping(BASE_URL)
    List<BookDto> getBooks();
}
