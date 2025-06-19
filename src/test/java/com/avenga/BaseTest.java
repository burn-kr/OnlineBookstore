package com.avenga;

import com.avenga.api.dto.ErrorResponse;
import com.avenga.api.service.AuthorService;
import com.avenga.api.service.CleanUpService;
import com.avenga.config.listener.TestNGExecutionListener;
import com.avenga.api.service.BookService;
import feign.Response;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Listeners;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static org.apache.commons.lang3.StringUtils.EMPTY;
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

    @AfterClass
    public void cleanUpTestData() {
        cleanUpService.cleanUp(this.getClass().getSimpleName());
    }

    protected void verifyResponseError(ErrorResponse response, HttpStatus expectedStatus, String expectedMessage) {
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
