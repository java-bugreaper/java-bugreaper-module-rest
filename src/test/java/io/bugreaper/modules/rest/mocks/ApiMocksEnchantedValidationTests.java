package io.bugreaper.modules.rest.mocks;

import io.bugreaper.modules.mocks.exceptions.MockEnchantedException;
import io.bugreaper.modules.rest.PreSetup;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import static org.junit.jupiter.api.Assertions.assertThrows;


class ApiMocksEnchantedValidationTests extends PreSetup {

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

        Throwable exception = assertThrows(AssertionFailedError.class, () ->
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

        Throwable exception = assertThrows(MockEnchantedException.class, () ->
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

        MatcherAssert.assertThat(
                "Exception for expected verify body JSONArray (not supported)",
                exception.getMessage(),
                StringContains.containsString("Expected verify body is not JSON: JSONArray"));
    }

    @Test
    void verifyValidation_bodyString() {
        mocksApi.createMock(universalMock);

        apiJsonPlus.sendPost("/api/post",
                        """
                                {"id": 3}""")
                .seeResponseCodeIs(200);

        Throwable exception = assertThrows(MockEnchantedException.class, () ->
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

        MatcherAssert.assertThat(
                "Exception for expected verify body String (not supported)",
                exception.getMessage(),
                StringContains.containsString("Expected verify body is not JSON: String"));
    }

}
