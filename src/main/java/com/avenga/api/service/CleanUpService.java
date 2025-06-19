package com.avenga.api.service;

import com.avenga.api.dto.author.AuthorDto;
import com.avenga.api.dto.book.BookDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * The cleanup service for removing all the test data created during the test execution
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CleanUpService extends BaseService {

    private final BookService bookService;
    private final AuthorService authorService;

    /**
     * Deletes all the objects created within a certain test class during the test run
     * @param className name of the class
     */
    public void cleanUp(String className) {
        log.info("Starting cleanup for the {} class", className);
        var cleanUpList = testContext.getCleanUpList(className);

        if (cleanUpList == null || cleanUpList.isEmpty()) {
            log.info("Nothing to cleanup...");
        } else {
            cleanUpList.forEach(item -> {
                try {
                    switch (item) {
                        case BookDto bookDto -> bookService.deleteBook(bookDto);
                        case AuthorDto authorDto -> authorService.deleteAuthor(authorDto);
                        default -> throw new IllegalStateException("Unexpected object: " + item
                                + ". Expected either BookDto or the AuthorDto");
                    }
                } catch (Exception e) {
                    log.warn("Could not delete an item '{}'. Skipping and continue with next one...", item);
                }
            });
        }
        log.info("Cleanup finished");
    }
}
