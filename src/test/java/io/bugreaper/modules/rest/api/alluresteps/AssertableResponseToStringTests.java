package io.bugreaper.modules.rest.api.alluresteps;

import io.bugreaper.modules.api.assertable.response.asserters.*;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AssertableResponseToStringTests {

    // Condition
    // start "Api response should have: "


    @Test
    void testBodyFieldCondition() {

        assertEquals(
                "body field [id] is <5>",
                new BodyFieldCondition("id", is(5)).toString());
    }

    @Test
    void testHeaderCondition() {

        assertEquals(
                "header [head-1] a string containing \"_part\"",
                new HeaderCondition("head-1", StringContains.containsString("_part")).toString());
    }

    @Test
    void testResponseTimeLessCondition() {

        assertEquals(
                "<response time> less 500 milliseconds",
                new ResponseTimeLessCondition(500).toString());
    }

    @Test
    void testStatusCodeCondition() {

        assertEquals(
                "<status code> is 200",
                new StatusCodeCondition(200).toString());
    }

    @Test
    void testStatusCodeSuccessfulCondition() {

        assertEquals(
                "<status code> is SUCCESSFUL",
                new StatusCodeSuccessfulCondition().toString());
    }

    // BodyCondition
    // start "Api response body should: "

    @Test
    void testBodyIsJsonTypeCondition() {

        assertEquals(
                "be JSON type",
                new BodyIsJsonTypeCondition().toString());
    }

    @Test
    void testBodyJsonContainsCondition() {

        assertEquals(
                "CONTAINS JSON with non-strict order",
                new BodyJsonContainsCondition("here is JSON").toString());
    }

    @Test
    void testBodyJsonContainsFromFileCondition() {

        assertEquals(
                "CONTAINS JSON with non-strict order",
                new BodyJsonContainsFromFileCondition(Path.of("file.json")).toString());
    }

    @Test
    void testBodyJsonEqualCondition() {

        assertEquals(
                "be EQUAL to JSON with strict order",
                new BodyJsonEqualCondition("here is JSON").toString());
    }

    @Test
    void testBodyJsonEqualFromFileCondition() {

        assertEquals(
                "be EQUAL to JSON with strict order",
                new BodyJsonEqualFromFileCondition(Path.of("file.json")).toString());
    }

    @Test
    void testBodyJsonEqualNonStrictOrderCondition() {

        assertEquals(
                "be EQUAL to JSON with non-strict order",
                new BodyJsonEqualNonStrictOrderCondition("here is JSON").toString());
    }

    @Test
    void testBodyJsonContainsStrictOrderCondition() {

        assertEquals(
                "CONTAINS JSON with strict order",
                new BodyJsonContainsStrictOrderCondition("here is JSON").toString());
    }

    @Test
    void testBodyJsonValidatorCondition() {

        assertEquals(
                "have correct JSON schema",
                new BodyJsonValidatorCondition("here is JSON").toString());
    }

    @Test
    void testBodyXmlValidatorCondition() {

        assertEquals(
                "have correct XML schema",
                new BodyXmlValidatorCondition("here is XML").toString());
    }
}

