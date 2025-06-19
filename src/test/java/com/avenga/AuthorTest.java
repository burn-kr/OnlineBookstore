package com.avenga;

import com.avenga.api.dto.author.AuthorDto;
import com.avenga.api.dto.author.AuthorField;
import com.avenga.api.dto.book.BookDto;
import io.qameta.allure.Description;
import org.assertj.core.api.Assertions;
import org.springframework.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static com.avenga.api.dto.author.AuthorField.*;
import static com.avenga.constants.AssertionMessage.*;
import static com.avenga.constants.TestGroup.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@Test(groups = {ALL, AUTHORS}, testName = "Authors Test")
public class AuthorTest extends BaseTest {

    private static final String AUTHOR = "author";

    private BookDto firstBook;
    private BookDto secondBook;
    private AuthorDto firstAuthor;
    private AuthorDto secondAuthor;

    @BeforeClass
    public void setUp() {
        firstBook = bookService.createRandomBook();
        secondBook = bookService.createRandomBook();
        firstAuthor = authorService.createRandomAuthor(firstBook);
        secondAuthor = authorService.createRandomAuthor(secondBook);
    }

    @Test(description = "Get all authors Test", groups = SMOKE)
    @Description("Verifies that it's possible to retrieve all the authors")
    public void getAllAuthorsTest() {
        // The least expected amount of authors is 2 as we created 2 authors before the test
        var leastExpectedListSize = 2;
        var authorList = authorService.getAuthors();

        Assertions.assertThat(authorList)
                .as(ITEMS_LIST_UNEXPECTEDLY_EMPTY.formatted(AUTHOR))
                .isNotEmpty();

        assertSoftly(softAssertion -> {
            softAssertion.assertThat(authorList)
                    .as(ITEMS_LIST_SIZE_IS_NOT_AS_EXPECTED.formatted(AUTHOR))
                    .hasSizeGreaterThanOrEqualTo(leastExpectedListSize);
            softAssertion.assertThat(authorList)
                    .as(ITEMS_LIST_IS_NOT_AS_EXPECTED.formatted(AUTHOR))
                    .contains(firstAuthor, secondAuthor);
        });
    }

    @Test(description = "Get author Test")
    @Description("Verifies the an author can be successfully retrieved by his id")
    public void getAuthorTest() {
        var retrievedAuthor = authorService.getAuthor(firstAuthor.getId());

        assertThat(retrievedAuthor)
                .as(ITEM_IS_NOT_AS_EXPECTED.formatted(AUTHOR))
                .isEqualTo(firstAuthor);
    }

    @Test(description = "Get authors by book Test", groups = SMOKE)
    @Description("Verifies that a list of authors can be successfully retrieved by the related book")
    public void getAuthorsByBookTest() {
        var retrievedAuthors = authorService.getAuthorsByBook(firstBook);

        Assertions.assertThat(retrievedAuthors)
                .as(ITEMS_LIST_UNEXPECTEDLY_EMPTY.formatted(AUTHOR))
                .isNotEmpty();
        assertThat(retrievedAuthors)
                .as(ITEMS_LIST_IS_NOT_AS_EXPECTED.formatted(AUTHOR))
                .contains(firstAuthor);
    }

    @Test(description = "Create a new author Test", groups = SMOKE)
    @Description("Verifies that a new author can be successfully created")
    public void createAuthorTest() {
        var authorDto = authorService.prepareRandomAuthorDto(firstBook);
        var createdAuthor = authorService.createAuthor(authorDto);
        var actualAuthor = authorService.getAuthor(authorDto.getId());

        verifyAuthor(authorDto, createdAuthor, actualAuthor);
    }

    @Test(description = "Create an Author with required fields only Test")
    @Description("Verifies that an author with the required fields only can be successfully created")
    public void createAuthorWithRequiredFieldsOnlyTest() {
        var authorDto = authorService.prepareRandomAuthorDto(firstBook, ID, BOOK_ID);
        var createdAuthor = authorService.createAuthor(authorDto);
        var actualAuthor = authorService.getAuthor(authorDto.getId());

        verifyAuthor(authorDto, createdAuthor, actualAuthor);
    }

    @Test(description = "Create author for the same book Test")
    @Description("Verifies that a new author can be created for the book that already assigned to another author")
    public void createAuthorForSameBookTest() {
        var authorDto = authorService.prepareRandomAuthorDto(secondBook);
        var createdAuthor = authorService.createAuthor(authorDto);
        var actualAuthor = authorService.getAuthor(authorDto.getId());

        verifyAuthor(authorDto, createdAuthor, actualAuthor);
    }

    @Test(description = "Update an author Test")
    @Description("Verifies that an existing author can be successfully updated")
    public void updateAuthorTest() {
        firstAuthor = authorService.prepareRandomAuthorDto(firstAuthor.getId(), firstBook);
        var updatedAuthor = authorService.updateAuthor(firstAuthor);
        var actualAuthor = authorService.getAuthor(firstAuthor.getId());

        verifyAuthor(firstAuthor, updatedAuthor, actualAuthor);
    }

    @Test(description = "Delete author Test")
    @Description("Verifies that an author can be successfully deleted")
    public void deleteAuthorTest() {
        var author = authorService.createRandomAuthor(firstBook);
        // to make sure it was actually created.
        // In case of any response code other than 2XX Feign will throw an exception
        authorService.getAuthor(author.getId());

        authorService.deleteAuthor(secondAuthor);
        var errorResponse = authorService.getAuthorRaw(secondAuthor.getId());

        verifyResponseError(errorResponse, HttpStatus.NOT_FOUND, "Not Found");
    }

    @Test(description = "Get a non existing author Test")
    @Description("Verifies that an error is returned in case a non existing author is requested (non existing id)")
    public void getNonExistingAuthorTest() {
        var lastAuthorId = authorService.getLastAuthorId();
        var errorResponse = authorService.getAuthorRaw(++lastAuthorId);

        verifyResponseError(errorResponse, HttpStatus.NOT_FOUND, "Not Found");
    }

    @Test(description = "Get authors by non existing book Test", groups = SMOKE)
    @Description("Verifies that an empty list of authors is returned when requesting authors by a non existing book")
    public void getAuthorsByNonExistingBookTest() {
        var nonExistingBook = bookService.prepareRandomBookDto();
        var retrievedAuthors = authorService.getAuthorsByBook(nonExistingBook);

        Assertions.assertThat(retrievedAuthors)
                .as(ITEMS_LIST_NOT_EMPTY.formatted(AUTHOR))
                .isEmpty();
    }

    @Test(description = "Create the same exact author Test")
    @Description("Verifies that an error is returned in case of an attempt to create an author duplicate")
    public void createSameExactAuthorTest() {
        var errorResponse = authorService.createAuthorRaw(firstAuthor);

        // TODO: clarify the error status and message
        verifyResponseError(errorResponse, HttpStatus.CONFLICT, "Author ID already exists");
    }

    @Test(description = "Create author for non existing book Test")
    @Description("Verify that an error is returned in case of attempt to create an author with non existing book id")
    public void createAuthorForNonExistingBookTest() {
        var nonExistingBook = bookService.prepareRandomBookDto();
        var authorDto = authorService.prepareRandomAuthorDto(nonExistingBook);
        var errorResponse = authorService.createAuthorRaw(authorDto);

        verifyResponseError(errorResponse, HttpStatus.NOT_FOUND, "Not Found");
    }

    @Test(description = "Update a non existing author Test")
    @Description("Verifies that an error is returned in case of a non existing author update (non existing id)")
    public void updateNonExistingAuthorTest() {
        var authorToUpdate = authorService.prepareRandomAuthorDto(firstBook);
        var errorResponse = authorService.updateAuthorRaw(authorToUpdate);

        verifyResponseError(errorResponse, HttpStatus.NOT_FOUND, "Not Found");
    }

    @DataProvider
    public Object[][] authorFieldsProvider() {
        return new Object[][] {
                {BOOK_ID, FIRST_NAME, LAST_NAME}, // missing ID field
                {ID, FIRST_NAME, LAST_NAME} // missing BOOK_ID field
        };
    }

    @Test(description = "Create an author without a required field Test", dataProvider = "authorFieldsProvider")
    @Description("Verifies that an error is returned in case of an attempt to create a new author " +
            "without a required field")
    public void createAuthorWithoutRequiredFieldTest(AuthorField... authorFields) {
        var invalidAuthorDto = authorService.prepareRandomAuthorDto(firstBook, authorFields);
        var errorResponse = authorService.createAuthorRaw(invalidAuthorDto);

        // TODO: clarify the error status and message
        verifyResponseError(errorResponse, HttpStatus.BAD_REQUEST, "Invalid data");
    }

    @Test(description = "Delete a book that is assigned to an author Test")
    @Description("Verifies that an error is returned in case of an attempt to delete a book " +
            "that is currently assigned to an author")
    public void deleteBookAssignedToAuthorTest() {
        var errorResponse = bookService.deleteBookRaw(firstBook);

        // TODO: clarify the error status and message
        verifyResponseError(errorResponse, HttpStatus.METHOD_NOT_ALLOWED, "Book in use");
    }

    /**
     * Verifies an expected author object against the object returned by the service (create/update)
     * and against the actual author object returned by the GET operation
     * @param expectedAuthor an expected {@link AuthorDto} object
     * @param managedAuthor a {@link AuthorDto} object returned by the service (create/update)
     * @param actualAuthor an actual {@link AuthorDto} object returned by the GET operation
     */
    private void verifyAuthor(AuthorDto expectedAuthor, AuthorDto managedAuthor, AuthorDto actualAuthor) {
        assertThat(managedAuthor)
                .as(ITEM_IS_NOT_AS_EXPECTED.formatted(AUTHOR))
                .isEqualTo(expectedAuthor);
        assertThat(actualAuthor)
                .as(ITEM_IS_NOT_AS_EXPECTED.formatted(AUTHOR))
                .isEqualTo(expectedAuthor);
    }
}
