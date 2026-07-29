package net.bugreaper.modules.rest.mocks;

import ch.qos.logback.classic.Level;
import com.fasterxml.jackson.databind.JsonNode;
import net.bugreaper.core.utils.AllureAssert;
import net.bugreaper.core.utils.AllureResultLoader;
import net.bugreaper.core.utils.LogWatcher;
import net.bugreaper.modules.rest.PreSetup;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Isolated;

import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@Isolated
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ApiMocksEnchantedLogicTests extends PreSetup {

    private LogWatcher logWatcher;
    @BeforeEach
    void setup() {
        logWatcher = new LogWatcher("MockEnchantedReport", Level.DEBUG);
    }

    @AfterEach
    void teardown() {
        logWatcher.detach();
    }

    @Test
    void testVerifyBaseMockCountNoRequests() {

        Throwable exception = assertThrows(AssertionError.class, () ->
                mocksApi.assertAllMocksCount(1));

        MatcherAssert.assertThat(
                "Exception for Count verify when no requests found",
                exception.getMessage(),
                StringContains.containsString("No requests to mock-server in test"));
    }

    @Test
    void testVerifyBaseMockCountNotMatch() {
        mocksApi.createMock(universalMock);

        apiJsonPlus.sendPost("/api/post",
                        """
                                {
                                  "id": 8
                                }""")
                .seeResponseCodeIs(200);

        Throwable exception = assertThrows(AssertionError.class, () ->
                mocksApi.assertAllMocksCount(3));

        MatcherAssert.assertThat(
                "Exception for Count verify when no requests has other counts != 0",
                exception.getMessage(),
                StringContains.containsString("Failed assert all mocks count: expected Exactly <3> with AR <1>"));
    }


    @Test
    void testVerifyBaseMockFromTo() {
        mocksApi.createMock(universalMock);

        api.sendPost("/api/post",
                        """
                                {
                                  "id": 8
                                }""")
                .seeResponseCodeIs(200);

        Throwable exception = assertThrows(AssertionError.class, () ->
                mocksApi.assertAllMocksCount(2, 3));

        MatcherAssert.assertThat(
                "Exception for Count verify when expected from 2 to 3",
                exception.getMessage(),
                is("Failed assert all mocks count: expected From:<2> To:<3> with AR <1>"));
    }

    @Test
    void testVerifyBaseMockFromToNotLogic() {
        mocksApi.createMock(universalMock);

        apiJsonPlus.sendPost("/api/post",
                        """
                                {
                                  "id": 8
                                }""")
                .seeResponseCodeIs(200);

        Throwable exception = assertThrows(AssertionError.class, () ->
                mocksApi.assertAllMocksCount(3, 1));

        MatcherAssert.assertThat(
                "Exception for Count verify when expected from 3 to 1 (not logic)",
                exception.getMessage(),
                StringContains.containsString("Failed assert all mocks count: expected From:<3> is grater then To:<1>"));
    }

    @Test
    void testVerifyBaseMockTo() {
        mocksApi.createMock(universalMock);

        apiJsonPlus.sendPost("/api/post",
                        """
                                {
                                  "id": 8
                                }""")
                .seeResponseCodeIs(200);

        apiJsonPlus.sendPost("/api/post",
                        """
                                {
                                  "id": 8
                                }""")
                .seeResponseCodeIs(200);

        Throwable exception = assertThrows(AssertionError.class, () ->
                mocksApi.verifyMock("""
                        {
                            "httpRequest": {},
                            "times": {
                                "atMost": 1
                            }
                        }"""));

        MatcherAssert.assertThat(
                "Exception for Count verify when expected to 1",
                exception.getMessage(),
                StringContains.containsString("Failed assert all mocks count: expected To:<1> with AR <2>"));
    }

    @Test
    void testVerifyBaseMockFrom() {
        mocksApi.createMock(universalMock);

        apiJsonPlus.sendPost("/api/post",
                        """
                                {
                                  "id": 8
                                }""")
                .seeResponseCodeIs(200);

        Throwable exception = assertThrows(AssertionError.class, () ->
                mocksApi.verifyMock("""
                        {
                            "httpRequest": {},
                            "times": {
                                "atLeast": 4
                            }
                        }"""));

        MatcherAssert.assertThat(
                "Exception for Count verify when expected from 3",
                exception.getMessage(),
                StringContains.containsString("Failed assert all mocks count: expected From:<4> with AR <1>"));
    }

    @Test
    void testVerifyBaseNoCount() {
        mocksApi.createMock(universalMock);

        apiJsonPlus.sendPost("/api/post",
                        """
                                {
                                  "id": 8
                                }""")
                .seeResponseCodeIs(200);

        Throwable exception = assertThrows(AssertionError.class, () ->
                mocksApi.assertAllMocksCount(2, 3));

        MatcherAssert.assertThat(
                "Exception for Count verify when expected from 2 to 3",
                exception.getMessage(),
                StringContains.containsString("Failed assert all mocks count: expected From:<2> To:<3> with AR <1>"));
    }


    @Test
    void testVerifyBaseMockCountExpectedZero() {
        mocksApi.createMock(universalMock);

        apiJsonPlus.sendPost("/api/post",
                        """
                                {
                                  "id": 8
                                }""")
                .seeResponseCodeIs(200);

        Throwable exception = assertThrows(AssertionError.class, () ->
                mocksApi.assertAllMocksCount(0));

        MatcherAssert.assertThat(
                "Exception for Count verify when counts != 0",
                exception.getMessage(),
                StringContains.containsString("Failed assert all mocks count: expected Exactly <0> with AR <1>"));
    }

    @Test
    void testVerifyMockNoRequests() {

        mocksApi.createMock(universalMock);

        Throwable exception = assertThrows(AssertionError.class, () ->
                mocksApi.verifyMock("""
                        {
                          "httpRequest": {
                            "method": "POST",
                            "path": "/api/post1"
                          },
                          "times": {
                            "atLeast": 1,
                            "atMost": 1
                          }
                        }"""));

        MatcherAssert.assertThat(
                "Exception for verify when no requests found",
                exception.getMessage(),
                StringContains.containsString("No requests to mock-server in test"));
    }

    @Test
    void testVerifyMockNotAssertedMethod() {
        mocksApi.createMock(universalMock);

        apiJsonPlus.sendPost("/api/post",
                        """
                                {
                                  "id": 8
                                }""")
                .seeResponseCodeIs(200);

        Throwable exception = assertThrows(AssertionError.class, () ->
                mocksApi.verifyMock("""
                        {
                          "httpRequest": {
                            "method": "GET",
                            "path": "/api/post",
                            "body" : {
                                  "id": 8
                                }
                          },
                          "times": {
                            "atLeast": 1,
                            "atMost": 1
                          }
                        }"""));

        MatcherAssert.assertThat(
                "Exception for verify by method failed",
                exception.getMessage(),
                StringContains.containsString("Assert is actual requests CONTAINS expected method and/or path FAILED"));
    }

    @Test
    void testVerifyMockNotAssertedOnlyMethod() {
        mocksApi.createMock(universalMock);

        apiJsonPlus.sendPost("/api/post",
                        """
                                {
                                  "id": 8
                                }""")
                .seeResponseCodeIs(200);

        Throwable exception = assertThrows(AssertionError.class, () ->
                mocksApi.verifyMock("""
                        {
                          "httpRequest": {
                            "method": "GET",
                            "body" : {
                                  "id": 8
                                }
                          },
                          "times": {
                            "atLeast": 1,
                            "atMost": 1
                          }
                        }"""));

        MatcherAssert.assertThat(
                "Exception for verify by method failed (no path)",
                exception.getMessage(),
                StringContains.containsString("Assert is actual requests CONTAINS expected method and/or path FAILED"));
    }

    @Test
    void testVerifyMockNotAssertedOnlyPath() {
        mocksApi.createMock(universalMock);

        apiJsonPlus.sendPost("/api/post",
                        """
                                {
                                  "id": 8
                                }""")
                .seeResponseCodeIs(200);

        Throwable exception = assertThrows(AssertionError.class, () ->
                mocksApi.verifyMock("""
                        {
                          "httpRequest": {
                            "path": "/api/get",
                            "body" : {
                                  "id": 8
                                }
                          },
                          "times": {
                            "atLeast": 1,
                            "atMost": 1
                          }
                        }"""));

        MatcherAssert.assertThat(
                "Exception for verify by path failed (no method)",
                exception.getMessage(),
                StringContains.containsString("Assert is actual requests CONTAINS expected method and/or path FAILED"));
    }

    @Test
    void testVerifyMockNotAssertedMethodAndPath() {
        mocksApi.createMock(universalMock);

        apiJsonPlus.sendPost("/api/post",
                        """
                                {
                                  "id": 8
                                }""")
                .seeResponseCodeIs(200);

        Throwable exception = assertThrows(AssertionError.class, () ->
                mocksApi.verifyMock("""
                        {
                          "httpRequest": {
                            "method": "GET",
                            "path": "/api/get",
                            "body" : {
                                  "id": 8
                                }
                          },
                          "times": {
                            "atLeast": 1,
                            "atMost": 1
                          }
                        }"""));

        MatcherAssert.assertThat(
                "Exception for verify by method & path failed",
                exception.getMessage(),
                StringContains.containsString("Assert is actual requests CONTAINS expected method and/or path FAILED"));
    }

    @Test
    void testVerifyMockNotAssertedMethodOrPath() {
        mocksApi.createMock(universalMock);

        apiJsonPlus.sendPost("/api/post",
                        """
                                {
                                  "id": 8
                                }""")
                .seeResponseCodeIs(200);

        api.sendGet("/api/get")
                .seeResponseCodeIs(200);

        Throwable exception = assertThrows(AssertionError.class, () ->
                mocksApi.verifyMock("""
                        {
                          "httpRequest": {
                            "method": "GET",
                            "path": "/api/wrong"
                          },
                          "times": {
                            "atLeast": 1,
                            "atMost": 1
                          }
                        }"""));

        MatcherAssert.assertThat(
                "Exception for verify by method & path failed twice",
                exception.getMessage(),
                StringContains.containsString("Assert is actual requests CONTAINS expected method and/or path FAILED"));
    }


    @Test
    void testVerifyMockAssertHeaderOnce() {
        mocksApi.createMock(universalMock);

        apiJsonPlus.sendPost("/api/post",
                        """
                                {
                                  "id": 1
                                }""")
                .seeResponseCodeIs(200);


        Throwable exception = assertThrows(AssertionError.class, () ->
                mocksApi.verifyMock("""
                        {
                          "httpRequest": {
                            "method": "POST",
                            "path": "/api/post",
                            "headers": {
                                "wrongHeader" : ["dummy/dummy"]
                            },
                            "body" : {
                                  "id": 1
                                }
                          },
                          "times": {
                            "atLeast": 1,
                            "atMost": 1
                          }
                        }"""));

        MatcherAssert.assertThat(
                "Exception for verify by headers failed",
                exception.getMessage(),
                StringContains.containsString("Assert is actual requests CONTAINS expected headers FAILED"));
    }

    @Test
    void testVerifyMockAssertQueryParamsOnce() {
        mocksApi.createMock(universalMock);

        apiJsonPlus.withQueryParam("test1", "single").withQueryParam("test_list", Arrays.asList("one", "two"))
                .sendPost("/api/post",
                        """
                                {
                                  "id": 1
                                }""")
                .seeResponseCodeIs(200);

        Throwable exception = assertThrows(AssertionError.class, () ->
                mocksApi.verifyMock("""
                        {
                          "httpRequest": {
                            "method": "POST",
                            "path": "/api/post",
                            "queryStringParameters": {
                                "test1" : ["single"],
                                "test_list" : ["wrong"]
                            },
                            "body" : {
                                  "id": 1
                                }
                          },
                          "times": {
                            "atLeast": 1,
                            "atMost": 1
                          }
                        }"""));

        MatcherAssert.assertThat(
                "Exception for verify by query params failed",
                exception.getMessage(),
                StringContains.containsString("Assert is actual requests CONTAINS expected query params FAILED"));
    }

    @Test
    void testVerifyMockNotAssertedCountWithConditions() {
        mocksApi.createMock(universalMock);

        apiJsonPlus.sendPost("/api/post",
                        """
                                {
                                  "id": 8
                                }""")
                .seeResponseCodeIs(200);

        api.sendGet("/api/get")
                .seeResponseCodeIs(200);

        Throwable exception = assertThrows(AssertionError.class, () ->
                mocksApi.verifyMock("""
                        {
                          "httpRequest": {
                            "method": "POST",
                            "headers": {
                                "Connection" : ["Keep-Alive"]
                            },
                            "body" : {
                                  "id": 8
                                }
                          },
                          "times": {
                            "atLeast": 2,
                            "atMost": 2
                          }
                        }"""));

        MatcherAssert.assertThat(
                "Exception for verify assertion not caught (issue for next releases)",
                exception.getMessage(),
                StringContains.containsString("""
                        Count of expected mock request(s) not match.
                        Expected Exactly <2> with AR <1>
                        Check report for more info"""));
    }

    @Test
    void testVerifyMockNotAssertedCountWithConditionsEmptyBody() {
        mocksApi.createMock(universalMock);

        apiJsonPlus.sendPost("/api/post",
                        """
                                {
                                  "id": 8
                                }""")
                .seeResponseCodeIs(200);

        api.sendGet("/api/get")
                .seeResponseCodeIs(200);

        Throwable exception = assertThrows(AssertionError.class, () ->
                mocksApi.verifyMock("""
                        {
                          "httpRequest": {
                            "method": "POST",
                            "headers": {
                                "Connection" : ["Keep-Alive"]
                            },
                            "body" : {}
                          },
                          "times": {
                            "atLeast": 2,
                            "atMost": 2
                          }
                        }"""));

        MatcherAssert.assertThat(
                "Exception for verify assertion not caught (issue for next releases)",
                exception.getMessage(),
                StringContains.containsString("""
                        Count of expected mock request(s) not match.
                        Expected Exactly <2> with AR <1>
                        Check report for more info"""));
    }

    @Test
    @Order(1)
    void testVerifyMockNoBodyAndHeadersExpected() {
        mocksApi.createMock(universalMock);

        apiJsonPlus.sendPost("/api/post",
                        """
                                {
                                  "id": 8888
                                }""")
                .seeResponseCodeIs(200);


        Throwable exception = assertThrows(AssertionError.class, () ->
                mocksApi.verifyMock("""
                        {
                          "httpRequest": {
                            "method": "POST",
                            "path": "/api/post"
                          },
                          "times": {
                            "atLeast": 2,
                            "atMost": 2
                          }
                        }"""));

        MatcherAssert.assertThat(
                "Exception for verify assertion (no body & headers in verify setup - skipped)",
                exception.getMessage(),
                StringContains.containsString("""
                        Count of expected mock request(s) not match.
                        Expected Exactly <2> with AR <1>
                        Check report for more info"""));


        MatcherAssert.assertThat(
                logWatcher.getLoggedEvents(Level.INFO).toString(),
                StringContains.containsString("[INFO] No <body> in verify setup : this check will be skipped"));
    }

    @Test
    @Order(2)
    void testVerifyMockNoBodyAndHeadersExpectedAllure() {
        JsonNode result = AllureResultLoader.loadByTestName("testVerifyMockNoBodyAndHeadersExpected");

        AllureAssert.assertThat(result)
                .hasStep("(MOCK)[VERIFY] Verify mock-server request")
                .hasSubStep("[MOCK-REPORT]: Enchanted mock verify report")

                .hasSubStepLeft("No <body> in verify setup : this check will be skipped")
                .hasSubStepLeft("No <headers> in verify setup : this check will be skipped")
                .hasSubStepLeft("No <query params> in verify setup : this check will be skipped");

    }

    @Test
    @Order(3)
    void testVerifyMockNoMethodAndPathExpected() {
        mocksApi.createMock(universalMock);

        apiJsonPlus.sendPost("/api/post",
                        """
                                {
                                  "id": 8888
                                }""")
                .seeResponseCodeIs(200);


        Throwable exception = assertThrows(AssertionError.class, () ->
                mocksApi.verifyMock("""
                        {
                          "httpRequest": {
                            "body": {
                                "id": 1
                            }
                          },
                          "times": {
                            "atLeast": 1,
                            "atMost": 1
                          }
                        }"""));

        MatcherAssert.assertThat(
                "Exception for verify assertion (no method & Path in verify setup - skipped)",
                exception.getMessage(),
                StringContains.containsString("Assert is actual requests CONTAINS expected body FAILED"));

        assertEquals("[[INFO] No <method and/or path> in verify setup : this check will be skipped]",
                logWatcher.getLoggedEvents(Level.INFO).toString());
    }

    @Test
    @Order(4)
    void testVerifyMockNoMethodAndPathExpectedAllure() {
        JsonNode result = AllureResultLoader.loadByTestName("testVerifyMockNoMethodAndPathExpected");

        AllureAssert.assertThat(result)
                .hasStep("(MOCK)[VERIFY] Verify mock-server request")
                .hasSubStep("[MOCK-REPORT]: Enchanted mock verify report")

                .hasSubStepLeft("No <method and/or path> in verify setup : this check will be skipped")
                .hasSubStep("Check assert for body");
    }

}
