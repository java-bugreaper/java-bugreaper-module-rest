package net.bugreaper.modules.api.assertable;

import io.qameta.allure.Param;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.hamcrest.Matcher;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

import static io.qameta.allure.model.Parameter.Mode.HIDDEN;

/**
 * Provides a fluent API for asserting and extracting data from API responses.
 * <p>
 * This class is designed to be used in a chainable manner, allowing multiple
 * assertions to be performed in a single readable statement.
 * </p>
 *
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * api.sendGet("/users/1")
 *     .seeResponseCodeIs(200)
 *     .seeResponseContainsJson(Paths.get("expected.json"));
 * }</pre>
 *
 * <h2>Design Notes:</h2>
 * <ul>
 *     <li>All assertion methods return {@code this} to support fluent chaining.</li>
 *     <li>Assertion logic is delegated to internal helper methods to keep steps readable.</li>
 *     <li>Failures should throw assertion errors to immediately fail the test.</li>
 * </ul>
 */
public class AssertableResponse extends AssertableResponseAbstract implements ResponseAsserts, ResponseGrab {

    public AssertableResponse(Response response) {
        super(response, LoggerFactory.getLogger("bugreaper-module-api"));
    }


    @Override
    @Step("↑(API)[ASSERT] Status code is: {statusCode}")
    public AssertableResponse seeResponseCodeIs(int statusCode) {
        seeResponseCodeIsMethod(statusCode);
        return this;
    }


    @Override
    @Step("↑(API)[ASSERT] Status code is: SUCCESSFUL(2xx)")
    public AssertableResponse seeResponseCodeIsSuccessful() {
        seeResponseCodeIsSuccessfulMethod();
        return this;
    }

    @Override
    @Step("↑(API)[ASSERT] Response time less: {maxResponseMs}")
    public AssertableResponse seeResponseTimeLess(long maxResponseMs) {
        seeResponseTimeLessMethod(maxResponseMs);
        return this;
    }

    @Override
    @Step("↑(API)[ASSERT] Response body field: <{path}> {matcher}")
    public AssertableResponse seeResponseBodyFieldMatch(String path, Matcher<?> matcher) {
        seeResponseBodyFieldMatchMethod(path, matcher);
        return this;
    }

    @Override
    @Step("↑(API)[ASSERT] Response header: <{header}> {matcher}")
    public AssertableResponse seeResponseHeaderMatch(String header, Matcher<?> matcher) {
        seeResponseHeaderMatchMethod(header, matcher);
        return this;
    }

    @Override
    @Step("↑(API)[ASSERT] Response is JSON type")
    public AssertableResponse seeResponseIsJsonType() {
        seeResponseIsJsonTypeMethod();
        return this;
    }

    @Override
    @Step("↑(API)[ASSERT] Response body: CONTAINS JSON with non-strict order")
    public AssertableResponse seeResponseContainsJson(@Param(mode = HIDDEN) String expectedBody) {
        seeResponseContainsJsonMethod(expectedBody);
        return this;
    }

    @Override
    @Step("↑(API)[ASSERT] Response body: CONTAINS JSON with non-strict order (ignoring extensive array elements)")
    public AssertableResponse seeResponseContainsJsonSubset(@Param(mode = HIDDEN) String expectedBody) {
        seeResponseContainsJsonSubsetMethod(expectedBody);
        return this;
    }
    @Override
    @Step("↑(API)[ASSERT] Response body: CONTAINS JSON with non-strict order")
    public AssertableResponse seeResponseContainsJson(Path path) {
        seeResponseContainsJsonMethod(path);
        return this;
    }

    @Override
    @Step("↑(API)[ASSERT] Response body: EQUAL to JSON with strict order")
    public AssertableResponse seeResponseExactlyMatchJson(@Param(mode = HIDDEN) String expectedBody) {
        seeResponseExactlyMatchJsonMethod(expectedBody);
        return this;
    }

    @Override
    @Step("↑(API)[ASSERT] Response body: EQUAL to JSON with strict order")
    public AssertableResponse seeResponseExactlyMatchJson(Path path) {
        seeResponseExactlyMatchJsonMethod(path);
        return this;
    }

    @Override
    @Step("↑(API)[ASSERT] Response body: CONTAINS JSON with strict order")
    public AssertableResponse seeResponseContainsJsonStrictOrder(@Param(mode = HIDDEN) String expectedBody) {
        seeResponseContainsJsonStrictOrderMethod(expectedBody);
        return this;
    }

    @Override
    @Step("Response body: CONTAINS extended JSON (with options)")
    public AssertableResponse seeResponseContainsExtendedJson(@Param(mode = HIDDEN) String expectedSetup) {
        seeResponseContainsExtendedJsonMethod(expectedSetup);
        return this;
    }

    @Override
    @Step("↑(API)[ASSERT] Response body: EQUAL to JSON with non-strict order")
    public AssertableResponse seeResponseExactlyMatchJsonIgnoringOrder(@Param(mode = HIDDEN) String expectedBody) {
        seeResponseExactlyMatchJsonIgnoringOrderMethod(expectedBody);
        return this;
    }

    @Override
    @Step("↑(API)[ASSERT] Response body: has correct JSON schema")
    public AssertableResponse seeResponseMatchesJsonSchema(Path path) {
        seeResponseMatchesJsonSchemaMethod(path);
        return this;
    }

    @Override
    @Step("↑(API)[ASSERT] Response body: has correct XML schema")
    public AssertableResponse seeResponseMatchesXmlSchema(Path path) {
        seeResponseMatchesXmlSchemaMethod(path);
        return this;
    }

    @Step("↑(API)[ASSERT] Response body: has {expectedCount} elements")
    public AssertableResponse seeResponseBodyElementsCount(int expectedCount) {
        seeResponseBodyElementsCountMethod(expectedCount);
        return this;
    }

    // Grab

    @Override
    @Step("↑(API) Grab header <{header}> value")
    public String grabResponseHeader(String header) {
        return grabResponseHeaderMethod(header);
    }

    @Override
    @Step("↑(API) Grab response body")
    public String grabResponseBody() {
        return  grabResponseBodyMethod();
    }

    @Override
    @Step("↑(API) Grab response body field <{path}> value as String")
    public String grabStringFromResponseByPath(String path) {
        return grabStringFromResponseByPathMethod(path);
    }

    @Override
    @Step("↑(API) Grab response body field <{path}>")
    public Object grabDataFromResponseByPath(String path) {
        return grabDataFromResponseByPathMethod(path);
    }

}
