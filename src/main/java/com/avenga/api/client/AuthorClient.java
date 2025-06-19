package com.avenga.api.client;

import com.avenga.api.dto.author.AuthorDto;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Represents the HTTP client with the CRUD operations for the /Authors endpoints
 */
@FeignClient(name = "author-client", url = "${app.url}")
public interface AuthorClient {

    String BASE_AUTHORS_URL = "/Authors";
    String AUTHOR_URL = BASE_AUTHORS_URL + "/{id}";
    String BOOK_AUTHOR_URL = BASE_AUTHORS_URL + "/authors/books/{bookId}";

    @GetMapping(BASE_AUTHORS_URL)
    List<AuthorDto> getAuthors();

    @GetMapping(AUTHOR_URL)
    AuthorDto getAuthor(@PathVariable("id") int id);

    @GetMapping(AUTHOR_URL)
    Response getAuthorRaw(@PathVariable("id") int id);

    @GetMapping(BOOK_AUTHOR_URL)
    List<AuthorDto> getAuthorByBook(@PathVariable("bookId") int id);

    @PostMapping(BASE_AUTHORS_URL)
    AuthorDto createAuthor(AuthorDto authorDto);

    @PostMapping(BASE_AUTHORS_URL)
    Response createAuthorRaw(AuthorDto authorDto);

    @PutMapping(AUTHOR_URL)
    AuthorDto updateAuthor(@PathVariable("id") int id, AuthorDto authorDto);

    @PutMapping(AUTHOR_URL)
    Response updateAuthorRaw(@PathVariable("id") int id, AuthorDto authorDto);

    @DeleteMapping(AUTHOR_URL)
    void deleteAuthor(@PathVariable("id") int id);
}
