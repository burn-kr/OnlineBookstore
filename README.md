# OnlineBookstoreTest

[![Test Report](https://img.shields.io/badge/Test%20Report-View%20Latest-blue?style=flat-square)](https://burn-kr.github.io/OnlineBookstore/)

## Project Description

The `OnlineBookstoreTest` project is a set of automated End-to-End (E2E) tests for the Online Bookstore backend services. The project's goal is to ensure the quality and stability of the API interfaces by verifying the functionality of adding, modifying, deleting, and retrieving data about books and authors.

The tests are written in Java using the Spring Boot framework, TestNG for test management, and Feign for API interaction. Test results reporting is generated using Allure, which provides a clear visualization of outcomes and facilitates debugging.

## Table of Contents

1.  [Features](#features)
2.  [Technologies Used](#technologies-used)
3.  [Getting Started](#getting-started)
    * [Prerequisites](#prerequisites)
    * [Installation](#installation)
4.  [Running Tests](#running-tests)
    * [Local Run](#local-run)
    * [Running Smoke Tests](#running-smoke-tests)
    * [Running Books Tests](#running-books-tests)
    * [Running Authors Tests](#running-authors-tests)
5.  [Allure Reports](#allure-reports)
    * [Local Generation](#local-generation)
    * [Viewing Report Locally](#viewing-report-locally)
    * [Viewing Online](#viewing-online)
6.  [Automated Runs (GitHub Actions)](#automated-runs-github-actions)

## Features

* Automated E2E tests for `/Books` and `/Authors` APIs.
* Verification of CRUD operations (Create, Read, Update, Delete).
* Flexible test configuration using Spring Boot profiles.
* Detailed reporting with Allure Framework, including request/response logs.
* Scheduled test execution via GitHub Actions.

## Technologies Used

* **Java 21**
* **Maven** - Project management and build automation
* **Spring Boot 2.7.18** - Framework for rapid application development
    * `spring-boot-starter-test` - For testing Spring Boot applications
    * `spring-cloud-starter-openfeign` - For declarative HTTP clients
* **TestNG 7.9.0** - Testing framework
* **AssertJ 3.24.2** - Fluent assertion library
* **Lombok 1.18.30** - To reduce boilerplate code (getters, setters, etc.)
* **Feign** - For creating HTTP clients
* **Allure Framework 2.29.1** - For generating interactive test reports
    * `allure-testng` - Allure integration with TestNG
    * `allure-maven` - Maven plugin for Allure
* **AspectJ Weaver 1.9.21.1** - For Aspect-Oriented Programming support (used by Allure)
* **Jackson 2.16.1** - For JSON processing
* **JavaFaker 1.0.2** - For generating test data

## Getting Started

### Prerequisites

* Java Development Kit (JDK) 21
* Apache Maven 3.6+
* Internet access to download Maven dependencies.
* A running `OnlineBookstore` API backend service that you intend to test.

### Installation

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/burn-kr/OnlineBookbookstore.git](https://github.com/burn-kr/OnlineBookbookstore.git)
    cd OnlineBookstoreTest
    ```
2.  **Build Maven dependencies:**
    ```bash
    mvn clean install -DskipTests
    ```

## Running Tests

Tests are executed using the Maven Surefire Plugin, leveraging a TestNG suite XML file.

### Local Run

To run all tests defined in `suite/All.xml`:

```bash
mvn clean test
```

### Running smoke tests

To run the smoke test or tests only for Authors or Books:

```bash
mvn clean test -Dgroups=Smoke
```

### Running Books tests

```bash
mvn clean test -Dgroups=Books
```

### Running Authors tests

```bash
mvn clean test -Dgroups=Authors
```

## Allure Reports
Test results are automatically aggregated and can be transformed into informative HTML reports using the Allure Framework.

### Local Generation

After running your tests (e.g., mvn clean verify), raw Allure results will be generated in the target/allure-results directory. To generate the HTML report from these results:

```bash
mvn allure:report
```
This command will generate the HTML report in the target/test-report/ directory.

### Viewing Report Locally

To generate the report and immediately open it in your web browser (this requires the Allure CLI to be installed, but the Maven plugin can launch it for you):

```bash
mvn allure:serve
```

This command will generate the report and launch a local web server that opens the report in your browser.

### Viewing Online

The latest Test Report (Allure Report) is automatically published to GitHub Pages after each successful workflow run in GitHub Actions.

[View Latest Test Report](https://burn-kr.github.io/OnlineBookstore/)

## Automated Runs (GitHub Actions)

This project is configured to run tests automatically on a schedule using GitHub Actions.

* Configuration: .github/workflows/scheduled-tests.yml
* Schedule: Tests are scheduled to run daily at 01:00 UTC (04:00 EEST).
* Manual Trigger: You can also trigger the pipeline manually via the GitHub Actions UI, selecting the "Run workflow" option and specifying the desired environment (currently only `dev`).