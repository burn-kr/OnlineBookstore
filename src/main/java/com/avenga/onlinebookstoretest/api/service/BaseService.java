package com.avenga.onlinebookstoretest.api.service;

import com.avenga.onlinebookstoretest.config.TestContext;
import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;

import static java.lang.StackWalker.Option.RETAIN_CLASS_REFERENCE;

public abstract class BaseService {

    protected final StackWalker stackWalker = StackWalker.getInstance(RETAIN_CLASS_REFERENCE);
    @Autowired
    protected TestContext testContext;
    @Autowired
    protected Faker faker;
}
