package com.avenga.onlinebookstoretest;

import com.avenga.onlinebookstoretest.api.service.BookService;
import com.avenga.onlinebookstoretest.config.listener.TestNGExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Listeners;

@SpringBootTest
@Listeners(TestNGExecutionListener.class)
public abstract class BaseTest extends AbstractTestNGSpringContextTests {

    @Autowired
    protected BookService bookService;
}
