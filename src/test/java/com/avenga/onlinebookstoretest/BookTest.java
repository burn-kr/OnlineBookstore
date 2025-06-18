package com.avenga.onlinebookstoretest;

import io.qameta.allure.Description;
import org.testng.annotations.Test;

import static com.avenga.onlinebookstoretest.constants.AssertionMessage.BOOK_IS_NOT_AS_EXPECTED;
import static com.avenga.onlinebookstoretest.constants.TestGroup.ALL;
import static com.avenga.onlinebookstoretest.constants.TestGroup.BOOKS;
import static org.assertj.core.api.Assertions.assertThat;

@Test(groups = {ALL, BOOKS}, testName = "Books Test")
public class BookTest extends BaseTest {

    @Test(description = "Create a new book Test")
    @Description("Verifies that a new book can be successfully created")
    public void createBookTest() {
        var bookDto = bookService.prepareRandomBookDto();
        bookService.createBook(bookDto);

        var actualBook = bookService.getBook(bookDto.getId());

        assertThat(actualBook)
                .as(BOOK_IS_NOT_AS_EXPECTED)
                .isEqualTo(bookDto);
    }
}
