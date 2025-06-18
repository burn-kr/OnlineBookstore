package com.avenga.onlinebookstoretest.api.service;

import com.avenga.onlinebookstoretest.api.dto.book.BookDto;
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

    /**
     * Deletes all the objects created within a certain test class during the test run
     * @param className name of the class
     */
    public void cleanUp(String className) {
        log.info("Starting clean up for the {} class", className);
        var cleanUpList = testContext.getCleanUpList(className);

        if (cleanUpList == null || cleanUpList.isEmpty()) {
            log.info("Nothing to clean up...");
        } else {
            cleanUpList.forEach(item -> {
                try {
                    switch (item) {
                        case BookDto bookDto -> bookService.deleteBook(bookDto);
                        default -> throw new IllegalStateException("Unexpected object: " + item
                                + ". Expected either BookDto or the AuthorDto");
                    }
                } catch (Exception e) {
                    log.warn("Could not delete an item '{}'. Skip and continue with next one", item);
                }
            });
        }
    }
}
