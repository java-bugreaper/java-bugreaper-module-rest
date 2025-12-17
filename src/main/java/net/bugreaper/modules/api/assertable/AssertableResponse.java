package net.bugreaper.modules.api.assertable;

import io.qameta.allure.Allure;
import io.qameta.allure.Param;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.hamcrest.Matcher;

import java.nio.file.Path;

import static net.bugreaper.core.allurereporter.AllureReporter.*;
import static net.bugreaper.core.assertions.JsonAsserts.*;
import static net.bugreaper.core.filereaders.FileReader.readJsonFromFile;
import static io.qameta.allure.model.Parameter.Mode.HIDDEN;
import static io.restassured.matcher.RestAssuredMatchers.matchesXsdInClasspath;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.*;


public class AssertableResponse implements ResponseAsserts, ResponseGrab {

    private final Response response;

    public AssertableResponse(Response response) {
        this.response = response;
    }


    @Override
    @Step("Status code is: {statusCode}")
    public AssertableResponse seeResponseCodeIs(int statusCode) {
        response.then().assertThat().statusCode(statusCode);
        return this;
    }


    @Override
    @Step("Status code is: SUCCESSFUL(2xx)")
    public AssertableResponse seeResponseCodeIsSuccessful() {
        int statusCode = response.getStatusCode();

        assertTrue(statusCode >= 200 && statusCode <= 299,
                "Expected SUCCESSFUL(2xx) status code, but got: " + statusCode);

        return this;
    }

    @Override
    @Step("Response time less: {maxResponseMs}")
    public AssertableResponse seeResponseTimeLess(long maxResponseMs) {
        response.then().time(lessThan(maxResponseMs), MILLISECONDS);
        return this;
    }

    @Override
    @Step("Response body field: <{path}> {matcher}")
    public AssertableResponse seeResponseBodyFieldMatch(String path, Matcher<?> matcher) {
        response.then().assertThat().body(path, matcher);
        return this;
    }

    @Override
    @Step("Response header: <{header}> {matcher}")
    public AssertableResponse seeResponseHeaderMatch(String header, Matcher<?> matcher) {
        response.then().assertThat().header(header, matcher);
        return this;
    }

    @Override
    @Step("Response is JSON type")
    public AssertableResponse seeResponseIsJsonType() {

        assertValidJson(response.getBody().asString());
        return this;
    }


    @Override
    @Step("Response body: CONTAINS JSON with non-strict order")
    public AssertableResponse seeResponseContainsJson(@Param(mode = HIDDEN) String expectedBody) {
        attachJson("expected json part", expectedBody);

        containsJson(expectedBody, response.getBody().asString());
        return this;
    }

    @Override
    @Step("Response body: CONTAINS JSON with non-strict order")
    public AssertableResponse seeResponseContainsJson(Path path) {
        String pathString = String.valueOf(path);

        attachFromFileNoStep(pathString, pathString);

        containsJson(readJsonFromFile(pathString), response.getBody().asString());
        return this;
    }

    @Override
    @Step("Response body: EQUAL to JSON with strict order")
    public AssertableResponse seeResponseExactlyMatchJson(@Param(mode = HIDDEN) String expectedBody) {

        attachJson("expected json", expectedBody);

        assertJson(expectedBody, response.getBody().asString());
        return this;
    }

    @Override
    @Step("Response body: EQUAL to JSON with strict order")
    public AssertableResponse seeResponseExactlyMatchJson(Path path) {
        String pathString = String.valueOf(path);

        attachFromFileNoStep(pathString, pathString);

        assertJson(readJsonFromFile(pathString), response.getBody().asString());
        return this;
    }

    @Override
    @Step("Response body: CONTAINS JSON with strict order")
    public AssertableResponse seeResponseContainsJsonStrictOrder(@Param(mode = HIDDEN) String expectedBody) {
        attachJson("expected part", expectedBody);

        containsStrictOrderJson(expectedBody, response.getBody().asString());
        return this;
    }

    @Override
    @Step("Response body: EQUAL to JSON with non-strict order")
    public AssertableResponse seeResponseExactlyMatchJsonIgnoringOrder(@Param(mode = HIDDEN) String expectedBody) {
        attachJson("expected part", expectedBody);

        assertNoStrictOrderJson(expectedBody, response.getBody().asString());
        return this;
    }

    @Override
    @Step("Response body: has correct JSON schema")
    public AssertableResponse seeResponseMatchesJsonSchema(Path path) {
        String pathString = String.valueOf(path);

        attachFromFileNoStep(pathString, pathString);
        response.then().assertThat().body(matchesJsonSchemaInClasspath(pathString));
        return this;
    }

    @Override
    @Step("Response body: has correct XML schema")
    public AssertableResponse seeResponseMatchesXmlSchema(Path path) {
        String pathString = String.valueOf(path);

        attachFromFileNoStep(pathString, pathString);
        try {
            response.then().assertThat().body(matchesXsdInClasspath(pathString));
        } catch (Exception e) {
            fail("Response schema not match expected XML\n" + e.getMessage(), e);
        }

        return this;
    }

    @Step("Response body: has {expectedCount} elements")
    public AssertableResponse seeResponseBodyElementsCount(int expectedCount) {
        response.then().assertThat()
                .body("size()", is(expectedCount));

        return this;
    }

    @Override
    @Step("Grab header <{header}> value")
    public String grabResponseHeader(String header) {
        String result = response.header(header);
        Allure.addAttachment(header + ":", "text/plain", result);
        return result;
    }

    @Override
    @Step("Grab response body")
    public String grabResponseBody() {
        String result = response.getBody().asString();

        attachCanBeNull("body:", result);

        return result;
    }

    @Override
    @Step("Grab response body field <{path}> value")
    public String grabDataFromResponseByPath(String path) {
        String result = response.then().extract().path(path);

        attachCanBeNull(path, result);

        return result;
    }
}
