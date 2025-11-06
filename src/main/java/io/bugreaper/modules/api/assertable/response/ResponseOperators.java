package io.bugreaper.modules.api.assertable.response;

import io.bugreaper.modules.api.assertable.response.asserters.*;
import io.bugreaper.modules.api.assertable.response.extract.BodyPath;
import io.bugreaper.modules.api.assertable.response.extract.Header;
import org.hamcrest.Matcher;

import java.nio.file.Path;

public class ResponseOperators {

    private ResponseOperators() {
        throw new IllegalStateException("Utility class");
    }


    public static StatusCodeCondition statusCode(int code) {
        return new StatusCodeCondition(code);
    }

    public static StatusCodeSuccessfulCondition statusCodeSuccessful() {
        return new StatusCodeSuccessfulCondition();
    }

    public static ResponseTimeLessCondition responseTimeLess(long timeMs) {
        return new ResponseTimeLessCondition(timeMs);
    }

    public static BodyFieldCondition bodyField(String path, Matcher<?> matcher) {
        return new BodyFieldCondition(path, matcher);
    }

    public static HeaderCondition headerField(String headerName, Matcher<?> matcher) {
        return new HeaderCondition(headerName, matcher);
    }

    public static BodyJsonValidatorCondition bodyJsonValidator(String path) {
        return new BodyJsonValidatorCondition(path);
    }

    public static BodyXmlValidatorCondition bodyXmlValidator(String path) {
        return new BodyXmlValidatorCondition(path);
    }

    /**
     * Check is Json/JsonArray type
     * <p> extensible fields not expected
     */
    public static BodyIsJsonTypeCondition bodyIsJson() {
        return new BodyIsJsonTypeCondition();
    }

    /**
     * Assertion without strict array ordering
     * <p> extensible fields will be skipped
     *
     * @param path path to file with expected part of JSON with arrays (can be not ordered)
     */
    public static BodyJsonContainsFromFileCondition bodyJsonContains(Path path) {
        return new BodyJsonContainsFromFileCondition(path);
    }

    /**
     * Assertion without strict array ordering
     * <p> extensible fields will be skipped
     *
     * @param body expected part of JSON with arrays (can be not ordered)
     */
    public static BodyJsonContainsCondition bodyJsonContains(String body) {
        return new BodyJsonContainsCondition(body);
    }

    /**
     * Assertion with strict array ordering
     * <p> extensible fields not expected
     *
     * @param body expected full JSON with strict ordered arrays
     */
    public static BodyJsonEqualCondition bodyJsonEqual(String body) {
        return new BodyJsonEqualCondition(body);
    }

    /**
     * Assertion with strict array ordering
     * <p> extensible fields not expected
     *
     * @param path path to file with expected full JSON with strict ordered arrays
     */
    public static BodyJsonEqualFromFileCondition bodyJsonEqual(Path path) {
        return new BodyJsonEqualFromFileCondition(path);
    }

    /**
     * Assertion with strict array ordering
     * <p> extensible fields will be skipped
     *
     * @param body expected part of JSON with strict ordered arrays
     */
    public static BodyJsonContainsStrictOrderCondition bodyJsonContainsStrictOrder(String body) {
        return new BodyJsonContainsStrictOrderCondition(body);
    }

    /**
     * Assertion without strict array ordering
     * <p> extensible fields not expected
     *
     * @param body expected full JSON with arrays (can be not ordered)
     */
    public static BodyJsonEqualNonStrictOrderCondition bodyJsonEqualNoStrictOrder(String body) {
        return new BodyJsonEqualNonStrictOrderCondition(body);
    }

    //extractors

    /**
     * Grab data from body by path
     *
     * @param bodyPath path in body (object.data.id)
     */
    public static BodyPath grabFromBodyPath(String bodyPath) {
        return new BodyPath(bodyPath);
    }

    /**
     * Grab data from header
     *
     * @param header header name
     */
    public static Header grabHeader(String header) {
        return new Header(header);
    }


}