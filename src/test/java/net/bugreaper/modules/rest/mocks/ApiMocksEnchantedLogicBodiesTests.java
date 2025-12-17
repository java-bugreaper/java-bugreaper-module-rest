package net.bugreaper.modules.rest.mocks;

import net.bugreaper.modules.rest.PreSetup;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ApiMocksEnchantedLogicBodiesTests extends PreSetup {


    @Test
    void testVerifyMockAssertBodyOnce() {
        mocksApi.createMock(universalMock);

        apiJsonPlus.sendPost("/api/post",
                        """
                                {
                                  "id": 8888,
                                  "text": "some/not_li\\"nk"
                                }""")
                .seeResponseCodeIs(200);

        Throwable exception = assertThrows(AssertionFailedError.class, () ->
                mocksApi.verifyMock("""
                        {
                          "httpRequest": {
                            "method": "POST",
                            "path": "/api/post",
                            "body" : {
                                  "id": 1,
                                  "text": "some/link"
                                }
                          },
                          "times": {
                            "atLeast": 1,
                            "atMost": 1
                          }
                        }"""));

        MatcherAssert.assertThat(
                "Exception for verify body failed (base)",
                exception.getMessage(),
                StringContains.containsString("Assert is actual requests CONTAINS expected body FAILED"));
    }

    @Test
    void testVerifyMockAssertBodyOnceJson() {
        mocksApi.createMock(universalMock);

        apiText.sendPost("/api/post", "103")
                .seeResponseCodeIs(200);

        Throwable exception = assertThrows(AssertionFailedError.class, () ->
                mocksApi.verifyMock("""
                        {
                          "httpRequest": {
                            "method": "POST",
                            "path": "/api/post",
                            "body" : {
                                  "id": 2,
                                  "text": "some/link"
                                }
                          },
                          "times": {
                            "atLeast": 1,
                            "atMost": 1
                          }
                        }"""));

        MatcherAssert.assertThat(
                """
                        Exception for verify body failed (base) with 'type' : 'JSON'""",
                exception.getMessage(),
                StringContains.containsString("Assert is actual requests CONTAINS expected body FAILED"));
    }

    @Test
    void testVerifyMockAssertBodyActualNotOnlyJsonXml() {
        mocksApi.createMock(
                """
                        {
                          "httpRequest": {
                          "path": "/api/post",
                          },
                          "httpResponse": {
                            "statusCode": 200,
                            "body": {
                              "result": "ok"
                            }
                          }
                        }""");


        apiJsonPlus.sendPost("/api/post",
                        """
                                {
                                  "id": 8888
                                }""")
                .seeResponseCodeIs(200);

        xmlExpectationDefault();
        apiXml.sendPost("/api/post-xml",
                        """
                                "<request> <key>id55</key> </request>"
                                """)
                .seeResponseCodeIs(200);

        Throwable exception = assertThrows(AssertionFailedError.class, () ->
                mocksApi.verifyMock("""
                        {
                          "httpRequest": {
                            "method": "POST",
                            "path": "/api/post",
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
                "Exception for verify body failed (base) with AR: xml",
                exception.getMessage(),
                StringContains.containsString("Assert is actual requests CONTAINS expected body FAILED"));
    }

    @Test
    void testVerifyMockAssertBodyActualNotOnlyJsonString() {
        mocksApi.createMock(universalMock);

        api.sendPost("/api/post",
                        """
                                {
                                  "id": 8888
                                }""")
                .seeResponseCodeIs(200);

        apiText.sendPost("/api/post", "some_string")
                .seeResponseCodeIs(200);

        Throwable exception = assertThrows(AssertionFailedError.class, () ->
                mocksApi.verifyMock("""
                        {
                          "httpRequest": {
                            "method": "POST",
                            "path": "/api/post",
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
                "Exception for verify body failed (base) with AR: string",
                exception.getMessage(),
                StringContains.containsString("Assert is actual requests CONTAINS expected body FAILED"));
    }

    @Test
    void testVerifyMockWithActualEmptyAndWrongComaInVerify() {
        mocksApi.createMock(universalMock);

        apiJsonPlus.sendPost("/api/post", "{}")
                .seeResponseCodeIs(200);

        api.sendPost("/api/post",
                        """
                                {
                                  "id": 8888
                                }""")
                .seeResponseCodeIs(200);

        apiText.sendPost("/api/post", "some_string")
                .seeResponseCodeIs(200);

        api.sendGet("/api/get")
                .seeResponseCodeIs(200);

        Throwable exception = assertThrows(AssertionFailedError.class, () ->
                mocksApi.verifyMock("""
                        {
                           "httpRequest": {
                             "method": "POST",
                             "path": "/api/post",
                             "body" : {}
                           },
                           "times": {
                             "atLeast": 5,
                             "atMost": 5,
                           }
                         }"""));

        MatcherAssert.assertThat(
                "Assert with several types of body",
                exception.getMessage(),
                StringContains.containsString("""
                        Count of expected mock request(s) not match.
                        Expected Exactly <5> with AR <3>
                        Check report for more info"""));
    }

    @Test
    void testVerifyMockAssertBodyNotStrictNoMatchType() {
        mocksApi.createMock(universalMock);

        api.sendPost("/api/post",
                        """
                                {
                                  "id": 8889
                                }""")
                .seeResponseCodeIs(200);
        Throwable exception = assertThrows(AssertionFailedError.class, () ->
                mocksApi.verifyMock("""
                        {
                          "httpRequest": {
                            "method": "POST",
                            "path": "/api/post",
                            "body" : {
                                 "type": "JSON",
                                 "json": {
                                    "id": 8888
                                 }
                            }
                          },
                          "times": {
                            "atLeast": 1,
                            "atMost": 1
                          }
                        }"""));

        MatcherAssert.assertThat(
                "Exception for verify by NOT STRICT body failed",
                exception.getMessage(),
                StringContains.containsString("Assert is actual requests CONTAINS expected body FAILED"));
    }


    @Test
    void testVerifyMockAssertBodyNotStrictNoMatchTypeDummy() {
        mocksApi.createMock(universalMock);

        apiJsonPlus.sendPost("/api/post",
                        """
                                {
                                  "id": 8889
                                }""")
                .seeResponseCodeIs(200);

        Throwable exception = assertThrows(AssertionFailedError.class, () ->
                mocksApi.verifyMock("""
                        {
                          "httpRequest": {
                            "method": "POST",
                            "path": "/api/post",
                            "body" : {
                                 "type": "JSON",
                                 "json": {
                                    "id": 8888,
                                 },
                                 "matchType": "DUMMY",
                            }
                          },
                          "times": {
                            "atLeast": 1,
                            "atMost": 1
                          }
                        }"""));

        MatcherAssert.assertThat(
                "Exception for verify by NOT STRICT body failed",
                exception.getMessage(),
                StringContains.containsString("Assert is actual requests CONTAINS expected body FAILED"));
    }


    @Test
    void testVerifyMockAssertBodyStrict() {
        mocksApi.createMock(universalMock);

        apiJsonPlus.sendPost("/api/post",
                        """
                                {
                                  "id": 8888,
                                  "text": "test"
                                }""")
                .seeResponseCodeIs(200);

        Throwable exception = assertThrows(AssertionFailedError.class, () ->
                mocksApi.verifyMock("""
                        {
                          "httpRequest": {
                            "method": "POST",
                            "path": "/api/post",
                            "body" : {
                                 "type": "JSON",
                                 "json": {
                                    "id": 8888
                                 },
                                 "matchType": "STRICT"
                            }
                          },
                          "times": {
                            "atLeast": 1,
                            "atMost": 1
                          }
                        }"""));


        MatcherAssert.assertThat(
                "Exception for verify by STRICT body failed",
                exception.getMessage(),
                StringContains.containsString("Assert is actual requests EQUAL expected body FAILED"));
    }

}
