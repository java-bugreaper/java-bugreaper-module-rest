package net.bugreaper.modules.api.assertable;

import net.bugreaper.core.assertions.JsonAsserts;
import net.bugreaper.core.exceptions.FileReaderException;
import org.hamcrest.Matcher;

import java.nio.file.Path;

public interface ResponseAsserts {

    /**
     * Asserts that the response StatusCode matches the expected status code.
     *
     * @param statusCode expected HTTP StatusCode
     * @return this instance for method chaining
     * @throws AssertionError if the assertion fails
     */
    AssertableResponse seeResponseCodeIs(int statusCode);

    /**
     * Asserts that the response StatusCode is in the 2xx range.
     * @return this instance for method chaining
     * @throws AssertionError if the assertion fails
     */
    AssertableResponse seeResponseCodeIsSuccessful();

    /**
     * Asserts response Body field using custom matchers.
     *
     * @param path    path to field (data.user.id)
     * @param matcher <a href="https://hamcrest.org/JavaHamcrest/javadoc/3.0/org/hamcrest/Matchers.html">Matcher</a>
     * @return this instance for method chaining
     * @throws AssertionError if the assertion fails
     */
    AssertableResponse seeResponseBodyFieldMatch(String path, Matcher<?> matcher);

    /**
     * Asserts response Header using custom matchers.
     *
     * @param header  header name
     * @param matcher <a href="https://hamcrest.org/JavaHamcrest/javadoc/3.0/org/hamcrest/Matchers.html">Matcher</a>
     * @return this instance for method chaining
     * @throws AssertionError if the assertion fails
     */
    AssertableResponse seeResponseHeaderMatch(String header, Matcher<?> matcher);

    /**
     * Asserts that the response is JSON object or JSON array.
     *
     * @return this instance for method chaining
     * @throws AssertionError if the assertion fails
     */
    AssertableResponse seeResponseIsJsonType();

    /**
     * Asserts that the response time is less than maximum expected.
     *
     * @param timeMs maximum expected response time in MS
     * @return this instance for method chaining
     * @throws AssertionError if the assertion fails
     */
    AssertableResponse seeResponseTimeLess(long timeMs);

    /**
     * Asserts that the response JSON matches the expected JSON without strict array ordering.
     * <p>Extra fields and the order of elements in arrays are ignored.</p>
     *
     * @param expectedBody expected JSON subset (array order is ignored and array size may differ)
     * @return this instance for method chaining
     * @throws AssertionError if the assertion fails
     * @throws IllegalArgumentException if the provided data is not valid JSON
     */
    AssertableResponse seeResponseContainsJsonSubset(String expectedBody);

    /**
     * Asserts that the response JSON matches the expected JSON without strict array ordering.
     *
     * <p>Extra object fields are ignored, but additional array elements cause an
     * {@link AssertionError}.</p>
     *
     * @param expectedBody expected JSON subset (array order is ignored, but the number of elements must match if provided)
     * @return this instance for method chaining
     * @throws AssertionError if the assertion fails
     * @throws IllegalArgumentException if the provided data is not valid JSON
     */
    AssertableResponse seeResponseContainsJson(String expectedBody);

    /**
     * Asserts that the response JSON matches the expected JSON without strict array ordering.
     *
     * <p>Extra object fields are ignored, but additional array elements cause an
     * {@link AssertionError}.</p>
     *
     * @param filePath path to the local file, including the file name, relative to <b>resources</b>,
     *                 containing the expected JSON subset (array order is ignored, but the number of elements must match if provided)
     * @return this instance for method chaining
     * @throws AssertionError      if the assertion fails
     * @throws FileReaderException if reading the file fails or the provided data is not valid JSON
     */
    AssertableResponse seeResponseContainsJson(Path filePath);

    /**
     * Asserts response JSON matches the expected JSON using contains, AND/OR,
     * and optional checks.
     *
     * <p>Same behavior as {@link JsonAsserts#assertJsonsExtended(String, String)}.
     *
     * @param expectedSetup expected JSON with contains, AND/OR, and optional checks
     * @return this instance for method chaining
     * @throws AssertionError if the assertion fails
     * @throws IllegalArgumentException if the provided data is not valid JSON
     */
    AssertableResponse seeResponseContainsExtendedJson(String expectedSetup);

    /**
     * Asserts that the response JSON matches the expected JSON with strict array ordering.
     *
     * <p>Extra object fields cause an {@link AssertionError}.</p>
     *
     * @param expectedBody expected full JSON with strict array ordering
     * @return this instance for method chaining
     * @throws AssertionError if the assertion fails
     * @throws IllegalArgumentException if the provided data is not valid JSON
     */
    AssertableResponse seeResponseExactlyMatchJson(String expectedBody);

    /**
     * Asserts that the response JSON matches the expected JSON with strict array ordering.
     *
     * <p>Extra object fields cause an {@link AssertionError}.</p>
     *
     * @param filePath path to the local file, including the file name, relative to <b>resources</b>, containing the expected JSON with strict array ordering
     * @return this instance for method chaining
     * @throws AssertionError      if the assertion fails
     * @throws FileReaderException if reading the file fails or the provided data is not valid JSON
     */
    AssertableResponse seeResponseExactlyMatchJson(Path filePath);

    /**
     * Asserts that the response JSON matches the expected JSON with strict array ordering.
     *
     * <p>Extra object fields are ignored.</p>
     *
     * @param expectedBody expected part of JSON with strict ordered arrays
     * @return this instance for method chaining
     * @throws AssertionError if the assertion fails
     * @throws IllegalArgumentException if the provided data is not valid JSON
     */
    AssertableResponse seeResponseContainsJsonStrictOrder(String expectedBody);

    /**
     * Asserts that the response JSON matches the expected JSON without strict array ordering.
     *
     * <p>Extra object fields cause an {@link AssertionError}.</p>
     *
     * @param expectedBody expected full JSON with arrays (array order is ignored)
     * @throws AssertionError if the assertion fails
     * @throws IllegalArgumentException if the provided data is not valid JSON
     */
    AssertableResponse seeResponseExactlyMatchJsonIgnoringOrder(String expectedBody);

    /**
     * Asserts that the response matches the JSON schema.
     *
     * @param filePath  path to the local file, including the file name, relative to <b>resources</b>, containing the JSON schema
     * @return this instance for method chaining
     * @throws AssertionError      if the assertion fails
     * @throws FileReaderException if reading the file fails or the provided data is not valid JSON
     */
    AssertableResponse seeResponseMatchesJsonSchema(Path filePath);

    /**
     * Asserts that the response matches the XML schema.
     *
     * @param filePath path to the local file, including the file name, relative to <b>resources</b>, containing the XML schema
     * @return this instance for method chaining
     * @throws AssertionError      if the assertion fails
     * @throws FileReaderException if reading the file fails
     */
    AssertableResponse seeResponseMatchesXmlSchema(Path filePath);

    /**
     * Asserts that the response body JSON array contains exactly the expected number of elements.
     *
     * @param expectedCount expected number of elements
     * @return this instance for method chaining
     * @throws AssertionError if the assertion fails
     */
    AssertableResponse seeResponseBodyElementsCount(int expectedCount);

}
