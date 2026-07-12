package net.bugreaper.modules.api.assertable;

import io.qameta.allure.Allure;
import io.restassured.response.Response;
import org.hamcrest.Matcher;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.text.MessageFormat;

import static io.restassured.matcher.RestAssuredMatchers.matchesXsdInClasspath;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static net.bugreaper.core.allurereporter.AllureReporter.*;
import static net.bugreaper.core.assertions.JsonAsserts.*;
import static net.bugreaper.core.filereaders.FileReader.readJsonFromFile;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;


public class AssertableResponseAbstract{

    private final Logger logger;
    private final Response response;

    protected AssertableResponseAbstract(Response response, Logger logger) {
        this.response = response;
        this.logger = logger;
    }

    protected Response seeResponseCodeIsMethod(int statusCode) {
        response.then().assertThat().statusCode(statusCode);
        return response;
    }


    protected AssertableResponseAbstract seeResponseCodeIsSuccessfulMethod() {
        int statusCode = response.getStatusCode();

        if (statusCode < 200 || statusCode > 299) {
            throw new AssertionError("Expected SUCCESSFUL(2xx) status code, but got: " + statusCode);
        }
        return this;
    }

    protected AssertableResponseAbstract seeResponseTimeLessMethod(long maxResponseMs) {
        response.then().time(lessThan(maxResponseMs), MILLISECONDS);
        return this;
    }

    protected AssertableResponseAbstract seeResponseBodyFieldMatchMethod(String path, Matcher<?> matcher) {
        response.then().assertThat().body(path, matcher);
        return this;
    }

    protected AssertableResponseAbstract seeResponseHeaderMatchMethod(String header, Matcher<?> matcher) {
        response.then().assertThat().header(header, matcher);
        return this;
    }

    protected AssertableResponseAbstract seeResponseIsJsonTypeMethod() {

        assertValidJson(response.getBody().asString());
        return this;
    }


    protected AssertableResponseAbstract seeResponseContainsJsonMethod(String expectedBody) {
        attachJson("expected json part", expectedBody);

        containsJson(expectedBody, response.getBody().asString());
        return this;
    }

    protected AssertableResponseAbstract seeResponseContainsExtendedJsonMethod(String expectedSetup) {
        attachJson("expected json with options", expectedSetup);

        assertJsonsExtended(expectedSetup, response.getBody().asString());
        return this;
    }

    protected AssertableResponseAbstract seeResponseContainsJsonSubsetMethod(String expectedBody) {
        attachJson("expected json part", expectedBody);

        containsJsonSubset(expectedBody, response.getBody().asString());
        return this;
    }

    protected AssertableResponseAbstract seeResponseContainsJsonMethod(Path path) {
        String pathString = String.valueOf(path);

        attachFromFileNoStep(pathString, pathString);

        containsJson(readJsonFromFile(pathString), response.getBody().asString());
        return this;
    }

    protected AssertableResponseAbstract seeResponseExactlyMatchJsonMethod(String expectedBody) {

        attachJson("expected json", expectedBody);

        assertJson(expectedBody, response.getBody().asString());
        return this;
    }

    protected AssertableResponseAbstract seeResponseExactlyMatchJsonMethod(Path path) {
        String pathString = String.valueOf(path);

        attachFromFileNoStep(pathString, pathString);

        assertJson(readJsonFromFile(pathString), response.getBody().asString());
        return this;
    }

    protected AssertableResponseAbstract seeResponseContainsJsonStrictOrderMethod(String expectedBody) {
        attachJson("expected part", expectedBody);

        containsStrictOrderJson(expectedBody, response.getBody().asString());
        return this;
    }

    protected AssertableResponseAbstract seeResponseExactlyMatchJsonIgnoringOrderMethod(String expectedBody) {
        attachJson("expected part", expectedBody);

        assertNoStrictOrderJson(expectedBody, response.getBody().asString());
        return this;
    }

    protected AssertableResponseAbstract seeResponseMatchesJsonSchemaMethod(Path path) {
        String pathString = String.valueOf(path);

        attachFromFileNoStep(pathString, pathString);
        response.then().assertThat().body(matchesJsonSchemaInClasspath(pathString));
        return this;
    }

    protected AssertableResponseAbstract seeResponseMatchesXmlSchemaMethod(Path path) {
        String pathString = String.valueOf(path);

        attachFromFileNoStep(pathString, pathString);
        try {
            response.then().assertThat().body(matchesXsdInClasspath(pathString));
        } catch (Exception e) {
            throw new AssertionError("Response schema not match expected XML\n" + e.getMessage(), e);
        }

        return this;
    }

    protected AssertableResponseAbstract seeResponseBodyElementsCountMethod(int expectedCount) {
        response.then().assertThat()
                .body("size()", is(expectedCount));

        return this;
    }

    protected String grabResponseHeaderMethod(String header) {
        String result = response.header(header);
        Allure.addAttachment(header + ":", "text/plain", result);
        return result;
    }

    protected String grabResponseBodyMethod() {
        String result = response.getBody().asString();

        attachCanBeNull("body:", result);

        return result;
    }


    protected String grabStringFromResponseByPathMethod(String path) {
        Object obj = response.then().extract().path(path);
        String result = String.valueOf(obj);

        if (logger.isInfoEnabled()) {
            logger.info(MessageFormat.format("Grabbed data(String) from <{0}>:\n{1}", path, result));
        }

        attachCanBeNull(path, result);

        return result;
    }

    protected Object grabDataFromResponseByPathMethod(String path) {
        Object obj = response.then().extract().path(path);

        attachObject(path, obj);

        return obj;
    }

}
