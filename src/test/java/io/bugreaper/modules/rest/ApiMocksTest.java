package io.bugreaper.modules.rest;


import io.bugreaper.modules.api.Api;
import io.bugreaper.modules.mocks.MocksApi;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;
import testcontainers.SetupMockserver;

import java.nio.file.Path;
import java.util.Map;

import static io.bugreaper.core.assertions.JsonAsserts.assertJson;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("squid:S2699")
class ApiMocksTest extends PreSetup {


    @Test
    void testVerifyBaseMockCountNoRequestsNoEnchant() {

        MocksApi mocksApiNoEnch = new SetupMockserver().getMocksApi().withEnchantedReport(false);

        Throwable exception = assertThrows(AssertionFailedError.class, () ->
                mocksApiNoEnch.assertAllMocksCount(1));

        MatcherAssert.assertThat(
                "Exception for Count verify when no requests found",
                exception.getMessage(),
                StringContains.containsString("Count of expected mock request(s) not match"));
    }

    @Test
    void testVerifySequenceFailed() {

        Throwable exception = assertThrows(AssertionFailedError.class, () ->
                mocksApi.verifyMockSequence("""
                        {
                           "httpRequests":[
                              {
                                 "path":"/some/path/one"
                              },
                              {
                                 "path":"/some/path/two"
                              }
                           ]
                        }"""));

        MatcherAssert.assertThat(
                "Exception for Sequence failed",
                exception.getMessage(),
                StringContains.containsString("Expected mock sequence not match"));
    }


    @Test
    void testVerifySequencePassed() {

        mocksApi.createMock(universalMock);

        api.sendGet("/api/get")
                .seeResponseCodeIs(200);
        api.sendPost("/api/post",
                        """
                                {"id": 1}""")
                .seeResponseCodeIs(200);


        mocksApi.verifyMockSequence("""
                {
                   "httpRequests":[
                      {
                         "method":"GET"
                      },
                      {
                         "method":"POST"
                      }
                   ]
                }""");

    }

    @Test
    void testVerifySequenceFailedOrder() {

        mocksApi.createMock(universalMock);

        api.sendPost("/api/post",
                        """
                                {"id": 1}""")
                .seeResponseCodeIs(200);
        api.sendGet("/api/get")
                .seeResponseCodeIs(200);

        Throwable exception = assertThrows(AssertionFailedError.class, () ->
                mocksApiAwait.verifyMockSequenceWithAwait("""
                        {
                           "httpRequests":[
                              {
                                 "method":"GET"
                              },
                              {
                                 "method":"POST"
                              }
                           ]
                        }"""));

        MatcherAssert.assertThat(
                "Exception for Sequence(order) failed with awaiting",
                exception.getMessage(),
                StringContains.containsString("""
                        Expected mock sequence not match ==> expected: <202> but was: <406>"""));

    }

    @Test
    void testBodyResponseIsJson() {

        mocksApi.createMock(universalMock);

        api.sendPost("/api/post",
                        """
                                {"id": 1}""")
                .seeResponseCodeIs(200)
                .seeResponseIsJsonType();
    }

    @Test
    void testCreateGetMockDoRequestCheckResponseVerifyMock() {
        mocksApi.createMock("""
                {
                   "httpRequest": {
                     "method": "GET"
                   },
                   "httpResponse": {
                     "statusCode": 200,
                     "body": {
                       "id": 1,
                       "name":"Alex"
                     }
                   }
                 }""");

        api.sendGet("/api/get")
                .seeResponseCodeIs(200)
                .seeResponseBodyFieldMatch("id", is(1))
                .seeResponseBodyFieldMatch("name", is("Alex"));

        mocksApi.verifyMock("""
                {
                  "httpRequest": {
                    "method": "GET",
                    "path": "/api/get"
                  },
                  "times": {
                    "atLeast": 1,
                    "atMost": 1
                  }
                }""");
    }


    @Test
    void testCreateGetMockDoRequestCheckJsonSchema() {
        mocksApi.createMock("""
                {
                   "httpRequest": {
                     "method": "GET"
                   },
                   "httpResponse": {
                     "statusCode": 200,
                     "body": {
                       "id": 1,
                       "name":"Alex"
                     }
                   }
                 }""");

        api.sendGet("/api/get")
                .seeResponseCodeIs(200)
                .seeResponseMatchesJsonSchema(Path.of("testdata/schemas/post_1.json"));
    }

    @Test
    void testCreateGetMockFromFileDoRequestCheckResponseContainsVerifyMockFromFile() {
        mocksApi.createMockCustom(
                "from file",
                "testdata/mocks/get_1.json");

        api.sendGet("/api/get")
                .seeResponseCodeIs(200)
                .seeResponseContainsJson(Path.of("testdata/responses/get_1_part.json"))
                .seeResponseContainsJson("""
                        {
                          "status": 11,
                          "statusName": "Something"
                        }""")
                .seeResponseCodeIsSuccessful();

        mocksApi.verifyMockCustom(
                "from file",
                "testdata/mocks/verify_get_1.json");

        mocksApi.verifyMockCustomWithAwait(
                "from file",
                "testdata/mocks/verify_get_1.json");
    }


    @Test
    void testCreateGetMockFromFileDoRequestCheckResponseEqualJsonFromFile() {
        mocksApi.createMockCustom(
                "from file",
                "testdata/mocks/get_1.json");

        api.sendGet("/api/get")
                .seeResponseCodeIs(200)
                .seeResponseExactlyMatchJson(Path.of("testdata/responses/get_1_equal.json"));
    }

    @Test
    void testCreateGetMockFromFileDoRequestCheckResponseEqualJson() {
        mocksApi.createMockCustom(
                "from file",
                "testdata/mocks/get_1.json");

        api.sendGet("/api/get")
                .seeResponseCodeIs(200)
                .seeResponseExactlyMatchJson("""
                        {
                          "statusName": "Something",
                          "id": 901,
                          "status": 11
                        }""");
    }

    @Test
    void testCheckResponseStrictOrderJson() {
        mocksApi.createMockCustom(
                "from file",
                "testdata/mocks/get_order.json");

        api.sendGet("/api/get")
                .seeResponseCodeIs(200)
                .seeResponseContainsJsonStrictOrder("""
                        {
                          "array": [
                            {
                              "id": 901,
                              "test": "one"
                            },
                            {
                              "id": 902,
                              "test": "two"
                            }
                          ]
                        }""");
    }


    @Test
    void testCreatePostMockStringBody() {

        mocksApi.createMock("""
                {
                   "httpRequest": {
                   },
                   "httpResponse": {
                     "statusCode": 299,
                     "body": {
                       "result": "ok"
                     }
                   }
                 }""");

        apiText.sendPost("/api/post", "some_string")
                .seeResponseCodeIsSuccessful();

        mocksApi.verifyMock("""
                {
                  "httpRequest": {
                    "method": "POST",
                    "path": "/api/post",
                    "body" : "some_string"
                  },
                  "times": {
                    "atLeast": 1,
                    "atMost": 1
                  }
                }""");
    }


    @Test
    void testGetBaseRequestsList() {

        mocksApi.createMock(universalMock);

        api.sendGet("/api/get")
                .seeResponseCodeIs(200);

        apiText.sendPost("/api/post", "some_string")
                .seeResponseCodeIs(200);

        api.sendGet("/api/get")
                .seeResponseCodeIs(200);

        mocksApi.showBaseRequestsList();
    }

    @Test
    void testGetBaseRequestsList_empty() {
        mocksApi.showBaseRequestsList();
    }

    @Test
    void testGrabStringDataFromResponse() {

        mocksApi.createMockCustom(
                "from file",
                "testdata/mocks/post_1.json");

        var response = api.sendPost("/api/post",
                        """
                                {"mainId": 555}""")
                .seeResponseCodeIs(200)

                .seeResponseExactlyMatchJson("""
                        {
                          "id": 125,
                          "status": "active",
                          "amount": 100.99,
                          "isTrue": true
                        }""")
                .grabDataFromResponseByPath("status");

        assertEquals("active", response, "Extracted field is valid");

    }

    @Test
    void testGrabWrongPathDataFromResponse() {

        mocksApi.createMock(universalMock);

        var response = api.sendPost("/api/post",
                        """
                                {"id": 1}""")
                .seeResponseCodeIs(200)
                .seeResponseExactlyMatchJson("""
                        {
                          "result": "ok"
                        }""")
                .grabDataFromResponseByPath("wrong");

        assertNull(response, "Extracted wrong field be NULL");

    }

    @Test
    void testGrabFullBody() {

        mocksApi.createMock(universalMock);

        var expected = """
                {
                  "result": "ok"
                }""";

        var response = api.sendPost("/api/post",
                        """
                                {"id": 555}""")
                .seeResponseCodeIs(200)

                .seeResponseExactlyMatchJson("""
                        {
                          "result": "ok"
                        }""")
                .grabResponseBody();

        assertJson(expected, response);
    }

    @Test
    void testGrabEmptyBody() {

        mocksApi.createMock(noBody);


        var response = api.sendPost("/api/post",
                        """
                                {"id": 555}""")
                .seeResponseCodeIs(200)
                .grabResponseBody();

        assertEquals("", response, "Extracted body is empty");
    }

    @Test
    void testBodyJsonEqualNoStrictOrder() {

        mocksApi.createMock("""
                {
                  "httpRequest": {
                  },
                  "httpResponse": {
                    "statusCode": 201,
                    "body": {
                      "id": 1,
                      "array": [3,2,1]
                    }
                  }
                }""");

        api.sendPost("/api/post")
                .seeResponseCodeIsSuccessful()
                .seeResponseExactlyMatchJsonIgnoringOrder("""
                        {
                          "id": 1,
                          "array": [1,3,2]
                        }""");

    }

    @Test
    void testCreatePostMockDoRequestGrabValue() {
        mocksApi.createMock("""
                {
                   "httpRequest": {
                   },
                   "httpResponse": {
                     "statusCode": 505,
                     "body": {
                       "result": "not ok"
                     }
                   }
                 }""");

        api.sendPost("/api/post",
                        """
                                {"testNum": "111"}""")
                .seeResponseCodeIs(505);

        var num = mocksApi.getRequestBodyValue("""
                        {
                            "method": "POST",
                            "path": "/api/post"
                          }
                        """,
                "[0].body.json.testNum");

        assertEquals("111", num, "value(string) grabbed successfully");
    }

    @Test
    void testCreatePostMockDoRequestGrabValueIntToString() {
        mocksApi.createMock(universalMock);

        api.sendPost("/api/post",
                        """
                                {"testNum": 555}""")
                .seeResponseCodeIs(200);

        var num = mocksApi.getRequestBodyValue("""
                        {
                            "method": "POST",
                            "path": "/api/post"
                          }
                        """,
                "[0].body.json.testNum");

        assertEquals("555", num, "value(int) grabbed successfully");
    }

    @Test
    void testCreatePostMockDoRequestGrabValueBooleanToString() {
        mocksApi.createMock(universalMock);

        api.sendPost("/api/post",
                        """
                                {"testNum": true}""")
                .seeResponseCodeIs(200);

        var num = mocksApi.getRequestBodyValue("""
                        {
                            "method": "POST",
                            "path": "/api/post"
                          }
                        """,
                "[0].body.json.testNum");

        assertEquals("true", num, "value(bool) grabbed successfully");
    }


    @Test
    void testCreatePostMockDoRequestGrabValue2() {
        var text = "test text";

        mocksApi.createMock(universalMock);

        apiJsonPlus.sendPost("/api/post",
                        """
                                {"text": "test text"}""")
                .seeResponseCodeIs(200);

        var value = mocksApi.getRequestBodyValue("""
                        {
                            "method": "POST",
                            "path": "/api/post"
                          }
                        """,
                "[0].body.text");

        assertEquals(text, value, "value grabbed successfully");
    }


    @Test
    void testCreatePostXmlDoRequestCheckXmlVerifyWithAwaiting() {
        var data = "some_data";

        createMockTestPostXml(data, 200);

        apiXml.sendPost("/api/post-xml",
                        "<response> <key>some_data</key> </response>")
                .seeResponseCodeIs(200)
                .seeResponseMatchesXmlSchema(Path.of("testdata/schemas/post_2_xml.xsd"))
                .seeResponseBodyFieldMatch("response.id", is("285"))
                .seeResponseBodyFieldMatch("response.status", is("ok"));


        verifyMockTestPostXml(data, 1);
        mocksApi.showMockLogs();
    }


    @Test
    void testCreatePostWithMapper() {
        mocksApi.createMock(testMock1("Alex", 42, true));

        api.sendGet("/api/get")
                .seeResponseCodeIs(200)
                .seeResponseBodyFieldMatch("name", is("Alex"))
                .seeResponseBodyFieldMatch("age", is(42))
                .seeResponseBodyFieldMatch("name2", is("Alex again"))
                .seeResponseBodyFieldMatch("isRegistered", is(true));
    }

    @Test
    void testCreatePostWithMapperMap() {

        mocksApi.createMock(testMock2(
                Map.of(
                        "name", "Jonn",
                        "age", 5)
        ));

        api.sendGet("/api/get")
                .seeResponseCodeIs(200)
                .seeResponseBodyFieldMatch("name", is("Jonn"))
                .seeResponseBodyFieldMatch("age", is(5))
                .seeResponseBodyFieldMatch("name2", is("Jonn again"));
    }

    @Test
    void testHeaderApi() {
        mocksApi.createMockCustom(
                "from file",
                "testdata/mocks/get_1_header.json");

        api.sendGet("/api/get")
                .seeResponseCodeIs(200)
                .seeResponseHeaderMatch("some_header_1", is("test1"))
                .seeResponseHeaderMatch("some_header_2", is("head/test2"))
                .seeResponseHeaderMatch("some_header_1", containsString("test"));
    }

    @Test
    void testGrabHeaderApi() {
        mocksApi.createMockCustom(
                "from file",
                "testdata/mocks/get_1_header.json");

        String header = api.sendGet("/api/get")
                .seeResponseCodeIs(200)
                .grabResponseHeader("some_header_2");

        assertEquals("head/test2", header, "Extracted header is valid");
    }

    @Test
    void testJsonSchemaMockVerify() {
        mocksApi.createMock(universalMock);

        api.sendPost("/api/post",
                        """
                                {"num": "911"}""")
                .seeResponseCodeIs(200);

        mocksApi.verifyMock("""
                {
                    "httpRequest": {
                        "method": "POST",
                        "path": "/api/post",
                        "body": {
                            "jsonSchema": {
                                "type": "object",
                                "properties": {
                                     "num": {
                                     "type": "string"
                                     }
                                },
                                "additionalProperties": false
                            }
                        }
                    },
                    "times": {
                        "atLeast": 1,
                        "atMost": 1
                    }
                }""");
    }

    @Test
    void testAuthorization() {
        mocksApi.createMock(universalMock);

        Api apiAuth = new SetupMockserver().getApi().setBasicAuth("user1", "password2");

        apiAuth.sendGet("/api/get")
                .seeResponseCodeIs(200);

        mocksApi.verifyMock(checkAuth("user1", "password2", 1));
    }

    @Test
    void testCleanMocksLogs() {
        mocksApi.createMock(withTimeout);
        api.sendPost("api/test",
                        """
                                {"id": 1}""")
                .seeResponseCodeIs(200);

        mocksApi.assertAllMocksCount(1);
        mocksApi.cleanMockLogs();
        mocksApi.assertAllMocksCount(0);
    }

    @Test
    void testResetMocks() {
        mocksApi.createMock(withTimeout);
        api.sendPost("api/test",
                        """
                                {"id": 1}""")
                .seeResponseCodeIs(200);

        mocksApi.assertAllMocksCount(1);
        mocksApi.resetMocks();
        mocksApi.assertAllMocksCount(0);
    }

}
