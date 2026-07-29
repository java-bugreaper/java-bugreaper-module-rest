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


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@Isolated
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ApiMocksEnchantedLogicBodiesTests extends PreSetup {


    private LogWatcher logWatcher;

    @BeforeAll
    static void clean(){
        AllureResultLoader.cleanResultsDir();
    }

    @BeforeEach
    void setup() {
        logWatcher = new LogWatcher("MockEnchantedReport", Level.DEBUG);
    }

    @AfterEach
    void teardown() {
        logWatcher.detach();
    }


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

        Throwable exception = assertThrows(AssertionError.class, () ->
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

        Throwable exception = assertThrows(AssertionError.class, () ->
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

        Throwable exception = assertThrows(AssertionError.class, () ->
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

        Throwable exception = assertThrows(AssertionError.class, () ->
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

        Throwable exception = assertThrows(AssertionError.class, () ->
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
    @Order(1)
    void testVerifyMockAssertBodyNotStrictNoMatchType() {
        mocksApi.createMock(universalMock);

        api.sendPost("/api/post",
                        """
                                {
                                  "id": 8889
                                }""")
                .seeResponseCodeIs(200);
        Throwable exception = assertThrows(AssertionError.class, () ->
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


        assertEquals("[[INFO] matchType not found]",
                logWatcher.getLoggedEvents(Level.INFO).toString());
    }

    @Test
    @Order(2)
    void testVerifyMockAssertBodyNotStrictNoMatchTypeAllure() {
        JsonNode result = AllureResultLoader.loadByTestName("testVerifyMockAssertBodyNotStrictNoMatchType");

        AllureAssert.assertThat(result)
                .hasStep("(MOCK)[VERIFY] Verify mock-server request")
                .hasSubStep("[MOCK-REPORT]: Enchanted mock verify report")

                .hasSubStep("Check assert for body")

                .hasAttachment("Expected CONTAINS body","""
                        {
                          "json": {"id": 8888},
                          "type": "JSON"
                        }""");
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

        Throwable exception = assertThrows(AssertionError.class, () ->
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
    @Order(3)
    void testVerifyMockAssertBodyStrict() {
        mocksApi.createMock(universalMock);

        apiJsonPlus.sendPost("/api/post",
                        """
                                {
                                  "id": 8888,
                                  "text": "test"
                                }""")
                .seeResponseCodeIs(200);

        Throwable exception = assertThrows(AssertionError.class, () ->
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

    @Test
    @Order(4)
    void testVerifyMockAssertBodyStrictAllure() {
        JsonNode result = AllureResultLoader.loadByTestName("testVerifyMockAssertBodyStrict");

        AllureAssert.assertThat(result)
                .hasStep("(MOCK)[VERIFY] Verify mock-server request")
                .hasSubStep("[MOCK-REPORT]: Enchanted mock verify report")

                .hasSubStep("Check assert for body")

                .hasAttachment("Expected EQUAL body","""
                        {"id": 8888}""")
                .hasAttachment("EQUAL body №1 failed:","""
                        
                        Actual body
                        {
                          "id": 8888,
                          "text": "test"
                        }
                        ========================
                                                
                        Extensive data in Actual Result (for strict match):
                        /text: test
                        """);
    }

}
