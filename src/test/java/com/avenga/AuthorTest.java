package com.avenga;

import com.avenga.api.dto.author.AuthorDto;
import com.avenga.api.dto.author.AuthorField;
import com.avenga.api.dto.book.BookDto;
import feign.FeignException;
import io.qameta.allure.Description;
import org.assertj.core.api.Assertions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static com.avenga.api.dto.author.AuthorField.*;
import static com.avenga.constants.AssertionMessage.*;
import static com.avenga.constants.TestGroup.ALL;
import static com.avenga.constants.TestGroup.AUTHORS;
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

    @Test(description = "Get all authors Test")
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
                .as(ITEM_IS_NOT_AS_EXPECTED)
                .isEqualTo(firstAuthor);
    }

    @Test(description = "Get author by book Test")
    @Description("Verifies that an author can be successfully retrieved by the related book")
    public void getAuthorByBookTest() {
        var retrievedAuthor = authorService.getAuthorByBook(firstBook);

        assertThat(retrievedAuthor)
                .as(ITEM_IS_NOT_AS_EXPECTED)
                .isEqualTo(firstAuthor);
    }

    @Test(description = "Create a new author Test")
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

    @Test(description = "Delete author Test", expectedExceptions = FeignException.NotFound.class)
    @Description("Verifies that an author can be successfully deleted")
    public void deleteAuthorTest() {
        authorService.deleteAuthor(secondAuthor);
        authorService.getAuthor(secondAuthor.getId());
    }

    @Test(description = "Get a non existing author Test", expectedExceptions = FeignException.NotFound.class)
    @Description("Verifies that an error is returned in case a non existing author is requested (non existing id)")
    public void getNonExistingAuthorTest() {
        var lastAuthorId = authorService.getLastAuthorId();
        authorService.getAuthor(++lastAuthorId);
    }

    @Test(description = "Create the same exact author Test", expectedExceptions = FeignException.Conflict.class)
    @Description("Verifies that an error is returned in case of an attempt to create an author duplicate")
    public void createSameExactAuthorTest() {
        authorService.createAuthor(firstAuthor);
    }

    @Test(description = "Create author for non existing book Test", expectedExceptions = FeignException.NotFound.class)
    @Description("Verify that an error is returned in case of attempt to create an author with non existing book id")
    public void createAuthorForNonExistingBookTest() {
        var nonExistingBook = bookService.prepareRandomBookDto();
        authorService.createRandomAuthor(nonExistingBook);
    }

    @Test(description = "Update a non existing author Test", expectedExceptions = FeignException.NotFound.class)
    @Description("Verifies that an error is returned in case of a non existing author update (non existing id)")
    public void updateNonExistingAuthorTest() {
        var authorToUpdate = authorService.prepareRandomAuthorDto(firstBook);
        authorService.updateAuthor(authorToUpdate);
    }

    @DataProvider
    public Object[][] authorFieldsProvider() {
        return new Object[][] {
                {BOOK_ID, FIRST_NAME, LAST_NAME}, // missing ID field
                {ID, FIRST_NAME, LAST_NAME} // missing BOOK_ID field
        };
    }

    @Test(description = "Create an author without a required field Test", dataProvider = "authorFieldsProvider",
            expectedExceptions = FeignException.BadRequest.class)
    @Description("Verifies that an error is returned in case of an attempt to create a new author without a required field")
    public void createAuthorWithoutRequiredFieldTest(AuthorField... authorFields) {
        var invalidAuthorDto = authorService.prepareRandomAuthorDto(firstBook, authorFields);
        authorService.createAuthor(invalidAuthorDto);
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
