package io.bugreaper.modules.rest.mocks.enchanted;

import io.bugreaper.modules.mocks.exceptions.MockEnchantedException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.StringContains;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static io.bugreaper.core.assertions.JsonAsserts.assertJson;
import static io.bugreaper.modules.mocks.enchanted.GetExpected.getExpectedBody;
import static io.bugreaper.core.mappers.JsonMappers.jsonObjectFromString;
import static org.junit.jupiter.api.Assertions.assertThrows;


class GetExpectedBodyTests {

    @Test
    void returnExpectedRawBodyTest() {

        String mock = """
                {
                  "httpRequest": {
                    "body" : {
                      "id": 8,
                      "text": "tests"
                    }
                  }
                }""";

        JSONObject jsonObj = jsonObjectFromString(mock);
        String ar = getExpectedBody(jsonObj, "rawBody", "body");


        assertJson("""
                        {
                         "id": 8,
                         "text": "tests"
                        }""",
                ar);

    }

    @Test
    void returnExpectedJsonBodyTest() {

        String mock = """
                {
                  "httpRequest": {
                    "body" :{
                        "type": "JSON",
                        "json":
                        {
                          "id": 8,
                          "text": "tests"
                        }
                    }
                  }
                }""";

        JSONObject jsonObj = jsonObjectFromString(mock);
        String ar = getExpectedBody(jsonObj, "json", "json");


        assertJson("""
                        {
                         "id": 8,
                         "text": "tests"
                        }""",
                ar);

    }

    @Test
    void returnExpectedBodyTypeJsonTest() {

        String mockSetup =
                """ 
                        {
                            "httpRequest": {
                                "method": "POST",
                                "path": "/api/post",
                                "body": {
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
                        }""";

        JSONObject expectedMockSetup = jsonObjectFromString(mockSetup);
        String body = getExpectedBody(expectedMockSetup, "json", "json");

        assertJson("""
                        {
                            "id" : 8888
                        }""",
                body);
    }

    @Test
    void returnExpectedBodyTypeJsonCaseSensitiveTest() {

        String mockSetup =
                """ 
                        {
                            "httpRequest": {
                                "method": "POST",
                                "path": "/api/post",
                                "body": {
                                    "type": "JSON",
                                    "JsOn": {
                                        "id": 777
                                    },
                                    "matchType": "STRICT"
                                }
                            },
                            "times": {
                                "atLeast": 1,
                                "atMost": 1
                            }
                        }""";

        JSONObject expectedMockSetup = jsonObjectFromString(mockSetup);
        String body = getExpectedBody(expectedMockSetup, "json", "JsOn");

        assertJson("""
                        {
                            "id" : 777
                        }""",
                body);
    }

    @Test
    void returnExpectedBodyTypeJsonCaseSensitiveTypeTest() {

        String mockSetup =
                """ 
                        {
                            "httpRequest": {
                                "method": "POST",
                                "path": "/api/post",
                                "body": {
                                    "type": "jSOn",
                                    "json": {
                                        "id": 777
                                    },
                                    "matchType": "STRICT"
                                }
                            },
                            "times": {
                                "atLeast": 1,
                                "atMost": 1
                            }
                        }""";

        JSONObject expectedMockSetup = jsonObjectFromString(mockSetup);
        String body = getExpectedBody(expectedMockSetup, "json", "json");

        assertJson("""
                        {
                            "id" : 777
                        }""",
                body);
    }

    @Test
    void returnExpectedBodyEmptyJsonTest() {

        String mockSetup =
                """ 
                        {
                            "httpRequest": {
                                "method": "POST",
                                "path": "/api/post",
                                "body": {}
                            },
                            "times": {
                                "atLeast": 1,
                                "atMost": 1
                            }
                        }""";

        JSONObject expectedMockSetup = jsonObjectFromString(mockSetup);
        String body = getExpectedBody(expectedMockSetup, "rawBody", "body");

        assertJson("""
                        {}""",
                body);
    }

    @Test
    void returnExpectedBodyExceptionTest() {

        String mockSetup =
                """ 
                        {
                            "httpRequest": {
                                "method": "POST",
                                "path": "/api/post",
                                "body": {
                                    "id": 2
                                }
                            },
                            "times": {
                                "atLeast": 1,
                                "atMost": 1
                            }
                        }""";

        JSONObject expectedMockSetup = jsonObjectFromString(mockSetup);

        Throwable exception = assertThrows(MockEnchantedException.class, () ->
                getExpectedBody(expectedMockSetup, "dummy", "dummy"));

        MatcherAssert.assertThat(
                "Exception for wrong body type grab",
                exception.getMessage(),
                StringContains.containsString("Can`t grab expected verify body"));
    }

}
