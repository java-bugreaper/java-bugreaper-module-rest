package net.bugreaper.modules.api.assertable;

import io.qameta.allure.Allure;
import io.qameta.allure.Param;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.hamcrest.Matcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.ArrayList;

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

    private static final Logger logger = LoggerFactory.getLogger("bugreaper-module-api");
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
    @Step("Grab response body field <{path}> value as String")
    public String grabStringFromResponseByPath(String path) {
        Object obj = response.then().extract().path(path);
        String result = String.valueOf(obj);

        if (logger.isInfoEnabled()) {
            logger.info(MessageFormat.format("Grabbed data(String) from <{0}>:\n{1}", path, result));
        }

        attachCanBeNull(path, result);

        return result;
    }


    @Override
    @Step("Grab response body field <{path}>")
    public Object grabDataFromResponseByPath(String path) {
        Object obj = response.then().extract().path(path);

        attachObject(path, obj);

        return obj;
    }

    private static void attachObject(String attachName, Object value) {

        String type = "";
        String attach;

        if (value == null) {
            attach = "null";
        }
        else if (value instanceof String string) {
            type = "type=String";
            attach = string;
        }
        else if (value instanceof ArrayList<?> array) {
            type = "type=Array";
            attach = String.valueOf(array);
        }
        else if (value instanceof Boolean bool) {
            type = "type=Boolean";
            attach =  (Boolean.TRUE.equals(bool)) ? "true" : "false";
        }
        else if (value instanceof Integer) {
            type = "type=Integer";
            attach =  value.toString();

        }
        else if (value instanceof Long) {
            type = "type=Long";
            attach = value.toString();
        }
        else if (value instanceof Float) {
            type = "type=Float";
            attach = String.valueOf(value);
        }
        else {
            type = "type=Other";
            attach = String.valueOf(value);
        }

        if (logger.isInfoEnabled()) {
            logger.info(MessageFormat.format("Grabbed data from <{0}> {1}:\n{2}", attachName, type, attach));
        }

        Allure.addAttachment(MessageFormat.format("{0} {1}:", attachName, type),
                "application/json", attach);
    }

}
