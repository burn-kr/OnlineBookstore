package com.avenga;

import com.avenga.api.service.AuthorService;
import com.avenga.api.service.CleanUpService;
import com.avenga.config.listener.TestNGExecutionListener;
import com.avenga.api.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Listeners;

@SpringBootTest
@Listeners(TestNGExecutionListener.class)
public abstract class BaseTest extends AbstractTestNGSpringContextTests {

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
}
