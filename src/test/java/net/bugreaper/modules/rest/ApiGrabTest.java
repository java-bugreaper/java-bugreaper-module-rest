package net.bugreaper.modules.rest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

import java.util.List;
import java.util.Map;

import static net.bugreaper.core.assertions.JsonAsserts.assertJson;
import static org.junit.jupiter.api.Assertions.*;


@SuppressWarnings("squid:S2699")
@Isolated
class ApiGrabTest extends PreSetup {

    //body

    @Test
    void testGrabFullBodyValidJson() {

        mocksApi.createMock("""
                {
                  "httpRequest": {
                  },
                  "httpResponse": {
                    "statusCode": 200,
                    "body": {
                      "result": "ok"
                    }
                  }
                }""");

        var expected = """
                {
                  "result": "ok"
                }""";

        var response = api.sendPost("/api/post")
                .seeResponseCodeIs(200)

                .seeResponseExactlyMatchJson("""
                        {
                          "result": "ok"
                        }""")
                .grabResponseBody();

        assertJson(expected, response);
    }

    @Test
    void testGrabFullBodyNotJson() {

        mocksApi.createMock("""
                {
                  "httpRequest": {
                  },
                  "httpResponse": {
                    "statusCode": 200,
                    "body": "12345"
                  }
                }""");


        var response = api.sendPost("/api/post")
                .seeResponseCodeIs(200)
                .grabResponseBody();

        assertEquals("12345", response, "Extracted full body (int converted to string)");
    }

    @Test
    void testGrabFullBodyAbsent() {

        mocksApi.createMock("""
                {
                  "httpRequest": {
                  },
                  "httpResponse": {
                    "statusCode": 200
                  }
                }""");


        var response = api.sendPost("/api/post")
                .seeResponseCodeIs(200)
                .grabResponseBody();

        assertEquals("", response, "Extracted full body (int converted to string)");
    }

    // fields

    @Test
    void testGrabStringDataFromResponse() {

        mocksApi.createMock("""
                {
                  "httpRequest": {
                  },
                  "httpResponse": {
                    "statusCode": 200,
                    "body":  {
                          "status": "active"
                        }
                  }
                }""");

        var status = api.sendPost("/api/post")
                .seeResponseCodeIs(200)
                .grabDataFromResponseByPath("status");

        assertEquals("active", status, "Extracted string field is valid");

        String status2 = api.sendPost("/api/post")
                .seeResponseCodeIs(200)
                .grabStringFromResponseByPath("status");

        assertEquals("active", status2, "Extracted string field is valid");

    }

    @Test
    void testGrabString2DataFromResponse() {

        mocksApi.createMock("""
                {
                  "httpRequest": {
                  },
                  "httpResponse": {
                    "statusCode": 200,
                    "body":  {
                          "status": "active"
                        }
                  }
                }""");

        String string = (String) api.sendPost("/api/post")
                .seeResponseCodeIs(200)

                .seeResponseExactlyMatchJson("""
                        {
                          "status": "active"
                        }""")
                .grabDataFromResponseByPath("status");

        assertEquals("active", string, "Extracted string field is valid");

    }


    @Test
    void testGrabIntDataFromResponse() {

        mocksApi.createMock("""
                {
                  "httpRequest": {
                  },
                  "httpResponse": {
                    "statusCode": 200,
                    "body":  {
                          "id": 125
                        }
                  }
                }""");

        var id = api.sendPost("/api/post")
                .seeResponseCodeIs(200)

                .seeResponseExactlyMatchJson("""
                        {
                          "id": 125
                        }""")
                .grabDataFromResponseByPath("id");

        assertEquals(125, id, "Extracted int field is valid");

        String id2 = api.sendPost("/api/post")
                .seeResponseCodeIs(200)
                .grabStringFromResponseByPath("id");

        assertEquals("125", id2, "Extracted int to string field is valid");

    }

    @Test
    void testGrabIntPathDataFromResponse() {

        mocksApi.createMock("""
                {
                  "httpRequest": {
                  },
                  "httpResponse": {
                    "statusCode": 200,
                    "body":  {
                        "user": {
                            "id": 125
                        }
                    }
                  }
                }""");

        var id = api.sendPost("/api/post")
                .seeResponseCodeIs(200)
                .grabDataFromResponseByPath("user.id");

        assertEquals(125, id, "Extracted int field is valid");

        String id2 = api.sendPost("/api/post")
                .seeResponseCodeIs(200)
                .grabStringFromResponseByPath("user.id");

        assertEquals("125", id2, "Extracted int to string field is valid");

    }


    @Test
    void testGrabLongDataFromResponse() {

        mocksApi.createMock("""
                {
                  "httpRequest": {
                  },
                  "httpResponse": {
                    "statusCode": 200,
                    "body":  {
                          "id": 9223372036854775807
                        }
                  }
                }""");

        var id = api.sendPost("/api/post")
                .seeResponseCodeIs(200)

                .seeResponseExactlyMatchJson("""
                        {
                          "id": 9223372036854775807
                        }""")
                .grabDataFromResponseByPath("id");

        assertEquals(9223372036854775807L, id, "Extracted long field is valid");

        String id2 = api.sendPost("/api/post")
                .seeResponseCodeIs(200)
                .grabStringFromResponseByPath("id");

        assertEquals("9223372036854775807", id2, "Extracted long to string field is valid");

    }

    @Test
    void testGrabFloatDataFromResponse() {

        mocksApi.createMock("""
                {
                  "httpRequest": {
                  },
                  "httpResponse": {
                    "statusCode": 200,
                    "body":  {
                          "amount": 777.95
                        }
                  }
                }""");

        var amount = api.sendPost("/api/post")
                .seeResponseCodeIs(200)

                .seeResponseExactlyMatchJson("""
                        {
                          "amount": 777.95
                        }""")
                .grabDataFromResponseByPath("amount");

        assertEquals(777.95f, amount, "Extracted decimal field is valid");

        String amount2 = api.sendPost("/api/post")
                .seeResponseCodeIs(200)
                .grabStringFromResponseByPath("amount");

        assertEquals("777.95", amount2, "Extracted float to string field is valid");

    }

    @Test
    void testGrabBoolDataFromResponse() {

        mocksApi.createMock("""
                {
                  "httpRequest": {
                  },
                  "httpResponse": {
                    "statusCode": 200,
                    "body":  {
                          "isAdmin": true
                        }
                  }
                }""");

        var bool = api.sendPost("/api/post")
                .seeResponseCodeIs(200)

                .seeResponseExactlyMatchJson("""
                        {
                          "isAdmin": true
                        }""")
                .grabDataFromResponseByPath("isAdmin");

        assertEquals(true, bool, "Extracted boolean field is valid");

        String bool2 = api.sendPost("/api/post")
                .seeResponseCodeIs(200)
                .grabStringFromResponseByPath("isAdmin");

        assertEquals("true", bool2, "Extracted boolean to string field is valid");

    }

    @Test
    void testGrabArrayDataFromResponse() {

        mocksApi.createMock("""
                {
                  "httpRequest": {
                  },
                  "httpResponse": {
                    "statusCode": 200,
                    "body":  {
                          "array": [1, 2, 3]
                        }
                  }
                }""");

        var arr = api.sendPost("/api/post")
                .seeResponseCodeIs(200)

                .seeResponseExactlyMatchJson("""
                        {
                          "array": [1, 2, 3]
                        }""")
                .grabDataFromResponseByPath("array");

        assertEquals(List.of(1, 2, 3), arr, "Extracted array field is valid");

        String arr2 = api.sendPost("/api/post")
                .seeResponseCodeIs(200)
                .grabStringFromResponseByPath("array");

        assertEquals("[1, 2, 3]", arr2, "Extracted array to string field is valid");
    }

    @Test
    void testGrabJsonMapDataFromResponse() {

        mocksApi.createMock("""
                {
                  "httpRequest": {
                  },
                  "httpResponse": {
                    "statusCode": 200,
                    "body":  {
                      "js": {
                        "id": 1
                       }
                    }
                  }
                }""");

        var js = api.sendPost("/api/post")
                .grabDataFromResponseByPath("js");

        assertEquals(Map.of("id", 1), js, "Extracted JSON field is valid");

        String js2 = api.sendPost("/api/post")
                .seeResponseCodeIs(200)
                .grabStringFromResponseByPath("js");

        assertEquals("{id=1}", js2, "Extracted JSON to string field is valid");
    }

    @Test
    void testGrabNullDataFromResponse() {

        mocksApi.createMock("""
                {
                  "httpRequest": {
                  },
                  "httpResponse": {
                    "statusCode": 200,
                    "body":  {
                          "amount": null
                        }
                  }
                }""");

        var noData = api.sendPost("/api/post")
                .seeResponseCodeIs(200)

                .seeResponseExactlyMatchJson("""
                        {
                          "amount": null
                        }""")
                .grabDataFromResponseByPath("amount");

        assertNull(noData, "Extracted null field is valid");

    }


    @Test
    void testGrabWrongPathDataFromResponse() {

        mocksApi.createMock("""
                {
                  "httpRequest": {
                  },
                  "httpResponse": {
                    "statusCode": 200,
                    "body":  {
                          "status": "active"
                        }
                  }
                }""");

        var noData = api.sendPost("/api/post")
                .seeResponseCodeIs(200)
                .seeResponseCodeIs(200)
                .seeResponseExactlyMatchJson("""
                        {
                          "status": "active"
                        }""")
                .grabDataFromResponseByPath("wrong");

        assertNull(noData, "Extracted wrong field be NULL");

    }

}
