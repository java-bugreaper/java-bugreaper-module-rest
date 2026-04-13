package net.bugreaper.modules.rest.mocks.enchanted;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static net.bugreaper.core.assertions.Asserts.assertStrings;
import static net.bugreaper.core.assertions.JsonAsserts.assertJson;
import static net.bugreaper.core.mappers.JsonMappers.jsonObjectFromString;
import static net.bugreaper.modules.mocks.enchanted.GetExpected.getExpectedQueryParams;


class GetExpectedQueryParamsTests {

    @Test
    void returnQueryParamsTest() {

        String mock = """
                {
                  "httpRequest": {
                    "queryStringParameters": {
                        "test1": [
                            "num_1"
                         ],
                        "test2": [
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
        String ar = getExpectedQueryParams(jsonObj);


        assertJson("""
                        {
                            "queryStringParameters": {
                                "test1": [
                                    "num_1"
                                 ],
                                "test2": [
                                    "num_2"
                                ]
                            }
                        }""",
                ar);

    }

    @Test
    void returnNoQueryParamsTest() {

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
        String ar = getExpectedQueryParams(jsonObj);

        assertStrings(null, ar);

    }

}
