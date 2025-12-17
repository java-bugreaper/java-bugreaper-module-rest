package net.bugreaper.modules.api.assertable;

import net.bugreaper.core.exceptions.FileReaderException;
import org.hamcrest.Matcher;

import java.nio.file.Path;

public interface ResponseAsserts {

    /**
     * Assert response StatusCode
     *
     * @param statusCode expected StatusCode
     * @return this
     * @throws AssertionError on assert fail
     */
    AssertableResponse seeResponseCodeIs(int statusCode);

    /**
     * Assert response StatusCode should be 2xx
     */
    AssertableResponse seeResponseCodeIsSuccessful();

    /**
     * Assert response Body field match matcher
     *
     * @param path    path to field (data.user.id)
     * @param matcher <a href="https://hamcrest.org/JavaHamcrest/javadoc/3.0/org/hamcrest/Matchers.html">Matcher</a>
     * @return this
     * @throws AssertionError on assert fail
     */
    AssertableResponse seeResponseBodyFieldMatch(String path, Matcher<?> matcher);

    /**
     * Assert response  header match matcher
     *
     * @param header  header name
     * @param matcher <a href="https://hamcrest.org/JavaHamcrest/javadoc/3.0/org/hamcrest/Matchers.html">Matcher</a>
     * @return this
     * @throws AssertionError on assert fail
     */
    AssertableResponse seeResponseHeaderMatch(String header, Matcher<?> matcher);

    /**
     * Assert response is Json/JsonArray type
     *
     * @return this
     * @throws AssertionError on assert fail
     */
    AssertableResponse seeResponseIsJsonType();

    /**
     * Assert response time less then maximum expected
     *
     * @param timeMs maximum expected response time in MS
     * @return this
     * @throws AssertionError on assert fail
     */
    AssertableResponse seeResponseTimeLess(long timeMs);

    /**
     * Json assertion without strict array ordering
     * <p> extensible fields will be skipped
     *
     * @param expectedBody expected part of JSON (arrays can be not ordered)
     * @return this
     * @throws AssertionError on assert fail
     */
    AssertableResponse seeResponseContainsJson(String expectedBody);

    /**
     * Json assertion without strict array ordering
     * <p> extensible fields will be skipped
     *
     * @param path path to file in resources with expected part of JSON  (arrays can be not ordered)
     * @return this
     * @throws AssertionError      on assert fail
     * @throws FileReaderException on read file error
     */
    AssertableResponse seeResponseContainsJson(Path path);

    /**
     * Json assertion with strict array ordering
     * <p> extensible fields not expected
     *
     * @param expectedBody expected full JSON with strict ordered arrays
     * @return this
     * @throws AssertionError on assert fail
     */
    AssertableResponse seeResponseExactlyMatchJson(String expectedBody);

    /**
     * Json assertion with strict array ordering
     * <p> extensible fields not expected
     *
     * @param path path to file in resources with expected full JSON with strict ordered arrays
     * @return this
     * @throws AssertionError      on assert fail
     * @throws FileReaderException on read file error
     */
    AssertableResponse seeResponseExactlyMatchJson(Path path);

    /**
     * Json assertion with strict array ordering
     * <p> extensible fields will be skipped
     *
     * @param expectedBody expected part of JSON with strict ordered arrays
     * @return this
     * @throws AssertionError on assert fail
     */
    AssertableResponse seeResponseContainsJsonStrictOrder(String expectedBody);

    /**
     * Json assertion without strict array ordering
     * <p> extensible fields not expected
     *
     * @param expectedBody expected full JSON with arrays (can be not ordered)
     * @throws AssertionError on assert fail
     */
    AssertableResponse seeResponseExactlyMatchJsonIgnoringOrder(String expectedBody);

    /**
     * Validate that the response matches the JSON schema
     *
     * @param path path to file in resources with Json schema
     * @return this
     * @throws AssertionError      on assert fail
     * @throws FileReaderException on read file error
     */
    AssertableResponse seeResponseMatchesJsonSchema(Path path);

    /**
     * Validate that the response matches the XML schema
     *
     * @param path path to file in resources with XML schema
     * @return this
     * @throws AssertionError      on assert fail
     * @throws FileReaderException on read file error
     */
    AssertableResponse seeResponseMatchesXmlSchema(Path path);

    /**
     * Assert that response body(JsonArray) has exactly count elements
     *
     * @param expectedCount expected count of elements
     * @return this
     * @throws AssertionError on assert fail
     */
    AssertableResponse seeResponseBodyElementsCount(int expectedCount);

}
