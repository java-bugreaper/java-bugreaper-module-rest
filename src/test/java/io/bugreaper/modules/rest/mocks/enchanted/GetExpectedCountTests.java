package io.bugreaper.modules.rest.mocks.enchanted;

import org.hamcrest.MatcherAssert;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.bugreaper.modules.mocks.enchanted.GetExpected.grabExpectedCount;
import static io.bugreaper.modules.mocks.mappers.JsonMappers.jsonObjectFromString;
import static org.hamcrest.Matchers.is;

class GetExpectedCountTests {

    @Test
    void grabExpectedCountWithTwoValuesTest() {

        String body = """
                {
                  "httpRequest": {
                    "body" : {
                          "id": 1
                        }
                  },
                  "times": {
                    "atLeast": 1,
                    "atMost": 2
                  }
                }""";

        JSONObject jsonBody = jsonObjectFromString(body);

        Map<String, String> value = grabExpectedCount(jsonBody);

        MatcherAssert.assertThat(
                "Map count",
                value.size(),
                is(2));

        MatcherAssert.assertThat(
                "Check atLeast(from)",
                value.get("atLeast"),
                is("1"));

        MatcherAssert.assertThat(
                "Check atMost(to)",
                value.get("atMost"),
                is("2"));
    }

    @Test
    void grabExpectedCountWithOneValueTest() {

        String body = """
                {
                  "httpRequest": {
                    "body" : {
                          "id": 1
                        }
                  },
                  "times": {
                    "atMost": 3
                  }
                }""";

        JSONObject jsonBody = jsonObjectFromString(body);

        Map<String, String> value = grabExpectedCount(jsonBody);

        MatcherAssert.assertThat(
                "Map count",
                value.size(),
                is(1));


        MatcherAssert.assertThat(
                "Check atMost(to)",
                value.get("atMost"),
                is("3"));
    }

    @Test
    void grabExpectedCountNoValuesDefaultTest() {

        String body = """
                {
                  "httpRequest": {
                    "body" : {
                          "id": 1
                        }
                  }
                }""";

        JSONObject jsonBody = jsonObjectFromString(body);

        Map<String, String> value = grabExpectedCount(jsonBody);


        MatcherAssert.assertThat(
                "Map count",
                value.size(),
                is(2));

        MatcherAssert.assertThat(
                "Check atLeast(from)",
                value.get("atLeast"),
                is("1"));

        MatcherAssert.assertThat(
                "Check atMost(to)",
                value.get("atMost"),
                is("1"));
    }

}
