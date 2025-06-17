package com.avenga.onlinebookstoretest.config.listener;

import lombok.extern.slf4j.Slf4j;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
public class TestNGExecutionListener extends TestListenerAdapter {

	private static final String ANSI_GREEN = "\u001B[32m";
	private static final String ANSI_RED = "\u001B[31m";
	private static final String ANSI_RESET = "\u001B[0m";

	@Override
	public void onStart(ITestContext testContext) {
		log.info("Running suite: \"{}\" containing {} tests", testContext.getName(),
				testContext.getAllTestMethods().length);
	}

	@Override
	public void onTestStart(ITestResult result) {
		log.info("\n" + ANSI_GREEN + """
						 _____                _       ____    _                    _                _
						|_   _|   ___   ___  | |_    / ___|  | |_    __ _   _ __  | |_    ___    __| |
						  | |    / _ \\ / __| | __|   \\___ \\  | __|  / _` | | '__| | __|  / _ \\  / _` |
						  | |   |  __/ \\__ \\ | |_     ___) | | |_  | (_| | | |    | |_  |  __/ | (_| |
						  |_|    \\___| |___/  \\__|   |____/   \\__|  \\__,_| |_|     \\__|  \\___|  \\__,_|
				""" + ANSI_RESET);

		log.info("Starting test: {}#{} {} {}", result.getTestClass().getRealClass().getName(),
				result.getMethod().getMethodName(), join(result.getParameters()), join(result.getFactoryParameters()));
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		log.info("{}PASSED{}: {}#{}", ANSI_GREEN, ANSI_RESET, result.getTestClass().getRealClass().getName(),
				result.getMethod().getMethodName());

		log.info("\n" + ANSI_GREEN + """ 
				  _____                _       ____                                   _
				 |_   _|   ___   ___  | |_    |  _ \\    __ _   ___   ___    ___    __| |
				   | |    / _ \\ / __| | __|   | |_) |  / _` | / __| / __|  / _ \\  / _` |
				   | |   |  __/ \\__ \\ | |_    |  __/  | (_| | \\__ \\ \\__ \\ |  __/ | (_| |
				   |_|    \\___| |___/  \\__|   |_|      \\__,_| |___/ |___/  \\___|  \\__,_|
				""" + ANSI_RESET);
	}

	@Override
	public void onTestFailure(ITestResult result) {

		log.error(result.getThrowable().getMessage());

		log.error("\n" + ANSI_RED + """ 
				  _____                _       _____           _   _              _
				 |_   _|   ___   ___  | |_    |  ___|   __ _  (_) | |   ___    __| |
				   | |    / _ \\ / __| | __|   | |_     / _` | | | | |  / _ \\  / _` |
				   | |   |  __/ \\__ \\ | |_    |  _|   | (_| | | | | | |  __/ | (_| |
				   |_|    \\___| |___/  \\__|   |_|      \\__,_| |_| |_|  \\___|  \\__,_|
				""" + ANSI_RESET);
		log.error("{}FAILED{}: {}#{}", ANSI_RED, ANSI_RESET, result.getTestClass().getRealClass().getName(),
				result.getMethod().getMethodName());
	}

	private String join(Object[] params) {
		return Arrays.stream(params).map(Object::toString).collect(Collectors.joining(","));
	}
}
