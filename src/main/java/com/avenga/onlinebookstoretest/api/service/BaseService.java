package com.avenga.onlinebookstoretest.api.service;

import com.avenga.onlinebookstoretest.config.TestContext;
import org.springframework.beans.factory.annotation.Autowired;

import static java.lang.StackWalker.Option.RETAIN_CLASS_REFERENCE;

public abstract class BaseService {
    protected final StackWalker stackWalker = StackWalker.getInstance(RETAIN_CLASS_REFERENCE);
    protected TestContext testContext;

    @Autowired
    public final void setTestContext(TestContext testContext) {
        this.testContext = testContext;
    }
}
