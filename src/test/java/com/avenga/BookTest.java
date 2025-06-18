package com.avenga;

import com.avenga.api.dto.book.BookField;
import com.avenga.constants.AssertionMessage;
import com.avenga.constants.TestGroup;
import com.avenga.api.dto.book.BookDto;
import feign.FeignException;
import io.qameta.allure.Description;
import org.assertj.core.api.Assertions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@Test(groups = {TestGroup.ALL, TestGroup.BOOKS}, testName = "Books Test")
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

        Assertions.assertThat(booksList)
                .as(AssertionMessage.BOOKS_LIST_UNEXPECTEDLY_EMPTY)
                .isNotEmpty();

        assertSoftly(softAssertion -> {
            softAssertion.assertThat(booksList)
                    .as(AssertionMessage.BOOKS_LIST_SIZE_IS_NOT_AS_EXPECTED)
                    .hasSizeGreaterThanOrEqualTo(leastExpectedListSize);
            softAssertion.assertThat(booksList)
                    .as(AssertionMessage.BOOKS_LIST_IS_NOT_AS_EXPECTED)
                    .contains(firstBook, secondBook);
        });
    }

    @Test(description = "Get book Test")
    @Description("Verifies the a book can be successfully retrieved by its id")
    public void getBookTest() {
        var retrievedBook = bookService.getBook(firstBook.getId());

        assertThat(retrievedBook)
                .as(AssertionMessage.BOOK_IS_NOT_AS_EXPECTED)
                .isEqualTo(firstBook);
    }

    @Test(description = "Create a new book Test")
    @Description("Verifies that a new book can be successfully created")
    public void createBookTest() {
        var bookDto = bookService.prepareRandomBookDto();
        var createdBook = bookService.createBook(bookDto);
        var actualBook = bookService.getBook(bookDto.getId());

        verifyBook(bookDto, createdBook, actualBook);
    }

    @Test(description = "Create a book with required fields only Test")
    @Description("Verifies that a book with the required fields only can be successfully created")
    public void createBookWithRequiredFieldsOnlyTest() {
        var bookDto = bookService.prepareRandomBookDto(BookField.ID, BookField.PAGE_COUNT, BookField.PUBLISH_DATE);
        var createdBook = bookService.createBook(bookDto);
        var actualBook = bookService.getBook(bookDto.getId());

        verifyBook(bookDto, createdBook, actualBook);
    }

    @Test(description = "Update a book Test")
    @Description("Verifies that an existing book can be successfully updated")
    public void updateBookTest() {
        firstBook = bookService.prepareRandomBookDto(firstBook.getId());
        var updatedBook = bookService.updateBook(firstBook);
        var actualBook = bookService.getBook(firstBook.getId());

        verifyBook(firstBook, updatedBook, actualBook);
    }

    @Test(description = "Delete book Test", expectedExceptions = FeignException.NotFound.class)
    @Description("Verifies that a book can be successfully deleted")
    public void deleteBookTest() {
        bookService.deleteBook(secondBook);
        bookService.getBook(secondBook.getId());
    }

    @Test(description = "Get a non existing book Test", expectedExceptions = FeignException.NotFound.class)
    @Description("Verifies that an error is returned in case a non existing book is requested (non existing id)")
    public void getNonExistingBookTest() {
        var lastBookId = bookService.getLastBookId();
        bookService.getBook(++lastBookId);
    }

    @Test(description = "Create the same exact book Test", expectedExceptions = FeignException.Conflict.class)
    @Description("Verifies that an error is returned in case of an attempt to create a book duplicate")
    public void createSameExactBookTest() {
        bookService.createBook(firstBook);
    }

    @Test(description = "Update a non existing book Test", expectedExceptions = FeignException.NotFound.class)
    @Description("Verifies that an error is returned in case of a non existing book update (non existing id)")
    public void updateNonExistingBookTest() {
        var bookToUpdate = bookService.prepareRandomBookDto();
        bookService.updateBook(bookToUpdate);
    }

    @DataProvider
    public Object[][] bookFieldsProvider() {
        return new Object[][] {
                {BookField.TITLE, BookField.DESCRIPTION, BookField.EXCERPT, BookField.PAGE_COUNT, BookField.PUBLISH_DATE}, // missing ID field
                {BookField.ID, BookField.TITLE, BookField.DESCRIPTION, BookField.EXCERPT, BookField.PUBLISH_DATE}, // missing DESCRIPTION field
                {BookField.ID, BookField.TITLE, BookField.DESCRIPTION, BookField.EXCERPT, BookField.PAGE_COUNT}, // missing PUBLISH_DATE field
        };
    }

    @Test(description = "Create a book without a required field Test", dataProvider = "bookFieldsProvider",
            expectedExceptions = FeignException.BadRequest.class)
    @Description("Verifies that an error is returned in case of an attempt to create a new book without a required field")
    public void createBookWithoutRequiredFieldTest(BookField... bookFields) {
        var invalidBookDto = bookService.prepareRandomBookDto(bookFields);
        bookService.createBook(invalidBookDto);
    }

    /**
     * Verifies an expected book object against the object returned by the service (create/update)
     * and against the actual book object returned by the GET operation
     * @param expectedBook an expected {@link BookDto} object
     * @param managedBook a {@link BookDto} object returned by the service (create/update)
     * @param actualBook an actual {@link BookDto} object returned by the GET operation
     */
    private void verifyBook(BookDto expectedBook, BookDto managedBook, BookDto actualBook) {
        assertThat(managedBook)
                .as(AssertionMessage.BOOK_IS_NOT_AS_EXPECTED)
                .isEqualTo(expectedBook);
        assertThat(actualBook)
                .as(AssertionMessage.BOOK_IS_NOT_AS_EXPECTED)
                .isEqualTo(expectedBook);
    }
}
