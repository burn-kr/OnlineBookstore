package com.avenga.api.service;

import com.avenga.config.TestContext;
import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;

import static java.lang.StackWalker.Option.RETAIN_CLASS_REFERENCE;

/**
 * Represents an abstract parent service that contains some shared objects for other services
 */
public abstract class BaseService {

    protected final StackWalker stackWalker = StackWalker.getInstance(RETAIN_CLASS_REFERENCE);
    @Autowired
    protected TestContext testContext;
    @Autowired
    protected Faker faker;
}
