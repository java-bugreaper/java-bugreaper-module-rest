package net.bugreaper.modules.rest.mocks;

import ch.qos.logback.classic.Level;
import net.bugreaper.core.utils.LogWatcher;
import net.bugreaper.modules.rest.PreSetup;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@Isolated
class ApiMocksEnchantedValidationTests extends PreSetup {

    private LogWatcher logWatcher;
    @BeforeEach
    void setup() {
        logWatcher = new LogWatcher("bugreaper-module-mocks", Level.INFO);
    }

    @AfterEach
    void teardown() {
        logWatcher.detach();
    }

    @Test
    void testWrongSetup() {

        Throwable exception = assertThrows(IllegalArgumentException.class, () ->
                mocksApi.verifyMock("""
                        {
                          "httpRequest": {
                            "method": "GET",
                            "path": "/api/post"
                            "body" : {
                                  id: 8
                                }
                          },
                          "times": {
                            "atLeast":
                          }
                        }"""));

        MatcherAssert.assertThat(
                "Exception for wrong verify setup",
                exception.getMessage(),
                StringContains.containsString("Invalid lenient JSON/JSONArray"));
    }

    @Test
    void testVerifyMockAssertBodyComaNowWork() {

        Throwable exception = assertThrows(AssertionError.class, () ->
                mocksApi.verifyMock("""
                        {
                          "httpRequest": {
                            "method": "POST",
                            "path": "/api/post",
                            "body" : {
                                  "id": 1,
                                }
                          },
                          "times": {
                            "atLeast": 1,
                            "atMost": 1
                          }
                        }"""));

        MatcherAssert.assertThat(
                "Exception for wrong verify setup (coma) that will be passed to mockserver but stopped",
                exception.getMessage(),
                StringContains.containsString("No requests to mock-server in test"));
    }


    @Test
    void verifyValidation_bodyArray() {
        mocksApi.createMock(universalMock);

        apiJsonPlus.sendPost("/api/post",
                        """
                                [
                                  {"id": 3}
                                ]""")
                .seeResponseCodeIs(200);

        Throwable exception = assertThrows(AssertionError.class, () ->
                mocksApi.verifyMock("""
                        {
                          "httpRequest": {
                            "method": "POST",
                            "path": "/api/post",
                            "body" : [
                                  {"id": 1},
                                  {"id": 2}
                            ]
                          },
                          "times": {
                            "atLeast": 1,
                            "atMost": 1
                          }
                        }"""));

        assertEquals(
                "Count of expected mock request(s) not match (enchanted report not finished)",
                exception.getMessage(),
                "Exception for expected verify body String (not supported)");

        assertEquals(
                "Expected verify body is not JSON: JSONArray",
                exception.getCause().getMessage(),
                "Caused by");


        assertEquals(
                """
                        [[INFO]\s
                        Mock verify assertion FAILED, start mock verify enchanted report build]""",
                logWatcher.getLoggedEvents(Level.INFO).toString());
    }



    @Test
    void verifyValidation_bodyString() {
        mocksApi.createMock(universalMock);

        apiJsonPlus.sendPost("/api/post",
                        """
                                {"id": 3}""")
                .seeResponseCodeIs(200);

        Throwable exception = assertThrows(AssertionError.class, () ->
                mocksApi.verifyMock("""
                        {
                          "httpRequest": {
                            "method": "POST",
                            "path": "/api/post",
                            "body" : "id=3"
                          },
                          "times": {
                            "atLeast": 1,
                            "atMost": 1
                          }
                        }"""));

        assertEquals(
                "Count of expected mock request(s) not match (enchanted report not finished)",
                exception.getMessage(),
                "Exception for expected verify body String (not supported)");

        assertEquals(
                "Expected verify body is not JSON: String",
                exception.getCause().getMessage(),
                "Caused by");
    }

}
