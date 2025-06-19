package com.avenga;

import com.avenga.api.dto.ErrorResponseDto;
import com.avenga.api.service.AuthorService;
import com.avenga.api.service.CleanUpService;
import com.avenga.config.listener.TestNGExecutionListener;
import com.avenga.api.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Listeners;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

@SpringBootTest
@Listeners(TestNGExecutionListener.class)
public abstract class BaseTest extends AbstractTestNGSpringContextTests {

    private static final String RESPONSE_STATUS_CODE_IS_NOT_AS_EXPECTED = "Response status code is not as expected";
    private static final String MESSAGE_IS_NOT_AS_EXPECTED = "Response message is not as expected";

    @Autowired
    protected BookService bookService;

    @Autowired
    protected AuthorService authorService;

    @Autowired
    private CleanUpService cleanUpService;

    @AfterClass(alwaysRun = true)
    public void cleanUpTestData() {
        cleanUpService.cleanUp(this.getClass().getSimpleName());
    }

    protected void verifyResponseError(ErrorResponseDto response, HttpStatus expectedStatus, String expectedMessage) {
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(HttpStatus.valueOf(Integer.parseInt(response.getStatus())))
                    .as(RESPONSE_STATUS_CODE_IS_NOT_AS_EXPECTED)
                    .isEqualTo(expectedStatus);
            softAssertions.assertThat(response.getTitle())
                    .as(MESSAGE_IS_NOT_AS_EXPECTED)
                    .contains(expectedMessage);

        });
    }
}
