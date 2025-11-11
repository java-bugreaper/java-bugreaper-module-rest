package io.bugreaper.modules.rest.mocks;

import io.bugreaper.modules.rest.PreSetup;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ApiMocksEnchantedLogicTests extends PreSetup {


    @Test
    void testVerifyBaseMockCountNoRequests() {

        Throwable exception = assertThrows(AssertionFailedError.class, () ->
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

        Throwable exception = assertThrows(AssertionFailedError.class, () ->
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

        Throwable exception = assertThrows(AssertionFailedError.class, () ->
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

        Throwable exception = assertThrows(AssertionFailedError.class, () ->
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

        Throwable exception = assertThrows(AssertionFailedError.class, () ->
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

        Throwable exception = assertThrows(AssertionFailedError.class, () ->
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

        Throwable exception = assertThrows(AssertionFailedError.class, () ->
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

        Throwable exception = assertThrows(AssertionFailedError.class, () ->
                mocksApi.assertAllMocksCount(0));

        MatcherAssert.assertThat(
                "Exception for Count verify when counts != 0",
                exception.getMessage(),
                StringContains.containsString("Failed assert all mocks count: expected Exactly <0> with AR <1>"));
    }

    @Test
    void testVerifyMockNoRequests() {

        mocksApi.createMock(universalMock);

        Throwable exception = assertThrows(AssertionFailedError.class, () ->
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

        Throwable exception = assertThrows(AssertionFailedError.class, () ->
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

        Throwable exception = assertThrows(AssertionFailedError.class, () ->
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

        Throwable exception = assertThrows(AssertionFailedError.class, () ->
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

        Throwable exception = assertThrows(AssertionFailedError.class, () ->
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

        Throwable exception = assertThrows(AssertionFailedError.class, () ->
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


        Throwable exception = assertThrows(AssertionFailedError.class, () ->
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

        Throwable exception = assertThrows(AssertionFailedError.class, () ->
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

        Throwable exception = assertThrows(AssertionFailedError.class, () ->
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
    void testVerifyMockNoBodyAndHeadersExpected() {
        mocksApi.createMock(universalMock);

        apiJsonPlus.sendPost("/api/post",
                        """
                                {
                                  "id": 8888
                                }""")
                .seeResponseCodeIs(200);


        Throwable exception = assertThrows(AssertionFailedError.class, () ->
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
    }

    @Test
    void testVerifyMockNoMethodAndPathExpected() {
        mocksApi.createMock(universalMock);

        apiJsonPlus.sendPost("/api/post",
                        """
                                {
                                  "id": 8888
                                }""")
                .seeResponseCodeIs(200);


        Throwable exception = assertThrows(AssertionFailedError.class, () ->
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
    }


}
