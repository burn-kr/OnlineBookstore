package com.avenga.onlinebookstoretest;

import com.avenga.onlinebookstoretest.api.dto.BookDto;
import feign.FeignException;
import io.qameta.allure.Description;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.avenga.onlinebookstoretest.constants.AssertionMessage.*;
import static com.avenga.onlinebookstoretest.constants.TestGroup.ALL;
import static com.avenga.onlinebookstoretest.constants.TestGroup.BOOKS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@Test(groups = {ALL, BOOKS}, testName = "Books Test")
public class BookTest extends BaseTest {

    private BookDto firstBook;
    private BookDto secondBook;

    @BeforeClass
    public void setUp() {
        firstBook = bookService.createRandomBook();
        secondBook = bookService.createRandomBook();
    }

    @Test(description = "Get all books Test")
    @Description("Verifies that it's possible to retrieve all the books")
    public void getAllBooksTest() {
        // The least expected amount of books is 2 as we created 2 books before the test
        var leastExpectedListSize = 2;
        var booksList = bookService.getBooks();

        assertThat(booksList)
                .as(BOOKS_LIST_UNEXPECTEDLY_EMPTY)
                .isNotEmpty();

        assertSoftly(softAssertion -> {
            softAssertion.assertThat(booksList)
                    .as(BOOKS_LIST_SIZE_IS_NOT_AS_EXPECTED)
                    .hasSizeGreaterThanOrEqualTo(leastExpectedListSize);
            softAssertion.assertThat(booksList)
                    .as(BOOKS_LIST_IS_NOT_AS_EXPECTED)
                    .contains(firstBook, secondBook);
        });
    }

    @Test(description = "Get book Test")
    @Description("Verifies the a book can be successfully retrieved by its id")
    public void getBookTest() {
        var retrievedBook = bookService.getBook(firstBook.getId());

        assertThat(retrievedBook)
                .as(BOOK_IS_NOT_AS_EXPECTED)
                .isEqualTo(firstBook);
    }

    @Test(description = "Create a new book Test")
    @Description("Verifies that a new book can be successfully created")
    public void createBookTest() {
        var bookDto = bookService.prepareRandomBookDto();
        var createdBook = bookService.createBook(bookDto);

        var actualBook = bookService.getBook(bookDto.getId());

        assertThat(createdBook)
                .as(BOOK_IS_NOT_AS_EXPECTED)
                .isEqualTo(bookDto);
        assertThat(actualBook)
                .as(BOOK_IS_NOT_AS_EXPECTED)
                .isEqualTo(bookDto);
    }

    @Test(description = "Update a book Test")
    @Description("Verifies that an existing book can be successfully updated")
    public void updateBookTest() {
        firstBook = bookService.prepareRandomBookDto(firstBook.getId());
        var updatedBook = bookService.updateBook(firstBook);

        var actualBook = bookService.getBook(firstBook.getId());

        assertThat(updatedBook)
                .as(BOOK_IS_NOT_AS_EXPECTED)
                .isEqualTo(firstBook);
        assertThat(actualBook)
                .as(BOOK_IS_NOT_AS_EXPECTED)
                .isEqualTo(firstBook);
    }

    @Test(description = "Delete book Test", expectedExceptions = FeignException.NotFound.class)
    @Description("Verifies that a book can be successfully deleted")
    public void deleteBookTest() {
        bookService.deleteBook(secondBook);
        bookService.getBook(secondBook.getId());
    }
}
