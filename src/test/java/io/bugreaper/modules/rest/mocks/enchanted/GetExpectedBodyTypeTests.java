package io.bugreaper.modules.rest.mocks.enchanted;

import org.hamcrest.MatcherAssert;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.bugreaper.modules.mocks.enchanted.GetExpected.getExpectedBodyType;
import static io.bugreaper.core.mappers.JsonMappers.jsonObjectFromString;
import static org.hamcrest.Matchers.is;


class GetExpectedBodyTypeTests {

    @Test
    void returnExpectedTypeRawBodyTest() {

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
        Map<String, String> type = getExpectedBodyType(jsonObj);

        MatcherAssert.assertThat(
                "Check type: rawBody",
                type.get("rawBody"),
                is("body"));

    }

    @Test
    void returnExpectedTypeEmptyRawBodyTest() {

        String mock = """
                {
                  "httpRequest": {
                    "body": {}
                  }
                }""";

        JSONObject jsonObj = jsonObjectFromString(mock);
        Map<String, String> type = getExpectedBodyType(jsonObj);

        MatcherAssert.assertThat(
                "Check type: rawBody empty {}",
                type.get("rawBody"),
                is("body"));

    }

    @Test
    void returnExpectedTypeJsonBodyTest() {

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
        Map<String, String> type = getExpectedBodyType(jsonObj);

        MatcherAssert.assertThat(
                "Check type: json",
                type.get("json"),
                is("json"));
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
                                    "type": "JsON",
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

        JSONObject jsonObj = jsonObjectFromString(mockSetup);
        Map<String, String> type = getExpectedBodyType(jsonObj);

        MatcherAssert.assertThat(
                "Check type: json case-sensitive JsOn",
                type.get("json"),
                is("JsOn"));
    }

    @Test
    void returnExpectedBodyTypeJsonSchemaCaseSensitiveTest() {

        String mockSetup =
                """ 
                        {
                            "httpRequest": {
                                "method": "POST",
                                "path": "/api/post",
                                "body": {
                                    "jsonSCHEMA": {
                                        "type": "object",
                                        "properties": {
                                             "id_wrong": {
                                             "type": "integer"
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
                        }""";

        JSONObject jsonObj = jsonObjectFromString(mockSetup);
        Map<String, String> type = getExpectedBodyType(jsonObj);

        MatcherAssert.assertThat(
                "Check type: jsonSchema case-sensitive",
                type.get("jsonSchema"),
                is("jsonSCHEMA"));
    }

    @Test
    void returnExpectedBodyTypeAbsentTest() {

        String mockSetup =
                """ 
                        {
                            "httpRequest": {
                                "method": "POST",
                                "path": "/api/post"
                            },
                            "times": {
                                "atLeast": 1,
                                "atMost": 1
                            }
                        }""";

        JSONObject jsonObj = jsonObjectFromString(mockSetup);
        Map<String, String> type = getExpectedBodyType(jsonObj);

        MatcherAssert.assertThat(
                "Check type: absent",
                type.get("absent"),
                is("absent"));
    }

}
