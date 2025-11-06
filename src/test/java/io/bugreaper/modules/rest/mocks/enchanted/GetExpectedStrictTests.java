package io.bugreaper.modules.rest.mocks.enchanted;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static io.bugreaper.core.assertions.Asserts.assertBooleans;
import static io.bugreaper.modules.mocks.enchanted.GetExpected.getExpectedStrict;
import static io.bugreaper.modules.mocks.mappers.JsonMappers.jsonObjectFromString;


class GetExpectedStrictTests {


    @Test
    void returnExpectedStrictTest() {

        String mockSetup =
                """ 
                        {
                            "httpRequest": {
                                "method": "POST",
                                "path": "/api/post",
                                "body": {
                                    "type": "JSON",
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

        JSONObject jsonObj = jsonObjectFromString(mockSetup);
        boolean isStrict = getExpectedStrict(jsonObj, "json", "json");

        assertBooleans(isStrict, true);
    }

    @Test
    void returnExpectedStrictCaseSensitiveTest() {

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

        JSONObject jsonObj = jsonObjectFromString(mockSetup);
        boolean isStrict = getExpectedStrict(jsonObj, "json", "JsOn");

        assertBooleans(isStrict, true);
    }

    @Test
    void returnExpectedStrictRawBodyTest() {

        String mockSetup =
                """ 
                        {
                            "httpRequest": {
                                "method": "POST",
                                "body": {
                                    "id": 777
                                }
                            }
                        }""";

        JSONObject jsonObj = jsonObjectFromString(mockSetup);
        boolean isStrict = getExpectedStrict(jsonObj, "rawBody", "body");

        assertBooleans(isStrict, false);
    }

}
