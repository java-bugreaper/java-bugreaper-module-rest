package net.bugreaper.modules.rest.mocks.enchanted;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static net.bugreaper.core.assertions.Asserts.assertStrings;
import static net.bugreaper.core.assertions.JsonAsserts.assertJson;
import static net.bugreaper.core.mappers.JsonMappers.jsonObjectFromString;
import static net.bugreaper.modules.mocks.enchanted.GetExpected.getExpectedHeaders;


class GetExpectedHeadersTests {

    @Test
    void returnHeadersTest() {

        String mock = """
                {
                  "httpRequest": {
                    "headers": {
                        "first": [
                            "num_1"
                         ],
                        "second": [
                            "num_2"
                        ]
                    },
                    "body" : {
                      "id": 8,
                      "text": "tests"
                    }
                  }
                }""";

        JSONObject jsonObj = jsonObjectFromString(mock);
        String ar = getExpectedHeaders(jsonObj);


        assertJson("""
                        {
                            "headers": {
                                "first": [
                                    "num_1"
                                 ],
                                "second": [
                                    "num_2"
                                ]
                            }
                        }""",
                ar);

    }

    @Test
    void returnNoHeadersTest() {

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
        String ar = getExpectedHeaders(jsonObj);

        assertStrings(null, ar);

    }

}
