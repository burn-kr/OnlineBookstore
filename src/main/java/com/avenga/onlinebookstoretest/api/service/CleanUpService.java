package com.avenga.onlinebookstoretest.api.service;

import com.avenga.onlinebookstoretest.api.dto.BookDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CleanUpService extends BaseService {

    private final BookService bookService;

    public void cleanUp(String className) {
        log.info("Starting clean up for the {} class", className);
        var cleanUpList = testContext.getCleanUpList(className);

        if (cleanUpList == null || cleanUpList.isEmpty()) {
            log.info("Nothing to clean up...");
        } else {
            cleanUpList.forEach(item -> {
                try {
                    switch (item) {
                        case BookDto bookDto -> bookService.deleteBook(bookDto.getId());
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
