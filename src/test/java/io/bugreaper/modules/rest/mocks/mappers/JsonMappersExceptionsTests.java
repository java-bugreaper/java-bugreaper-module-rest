package io.bugreaper.modules.rest.mocks.mappers;


import io.bugreaper.core.exceptions.JsonMappersException;
import io.bugreaper.modules.mocks.exceptions.MockEnchantedException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.StringContains;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static io.bugreaper.core.mappers.JsonMappers.*;
import static io.bugreaper.modules.mocks.mappers.JsonMappersRest.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JsonMappersExceptionsTests {

    String validJson = """
            {
              "id": 1
            }""";

    //important for enchanted report

    @Test
    void getObjectFromJsonObjectByKeyException() {

        JSONObject json = jsonObjectFromString(validJson);

        Throwable exception = assertThrows(MockEnchantedException.class, () ->
                getObjectFromJsonObjectByKey(json, "id"));

        MatcherAssert.assertThat(
                "Exception jsonObjectFromString for expected verify body not JSON (something else)",
                exception.getMessage(),
                StringContains.containsString("Expected verify id is not JSON: ???"));
    }

    @Test
    void getObjectFromJsonObjectByKeyStringException() {

        JSONObject json = jsonObjectFromString("""
                {"id": "text"}""");

        Throwable exception = assertThrows(MockEnchantedException.class, () ->
                getObjectFromJsonObjectByKey(json, "id"));

        MatcherAssert.assertThat(
                "Exception jsonObjectFromString for expected verify body not JSON String",
                exception.getMessage(),
                StringContains.containsString("Expected verify id is not JSON: String"));
    }

    @Test
    void getObjectFromJsonObjectByKeyArrayException() {

        JSONObject json = jsonObjectFromString("""
                {"id": ["text"]}""");

        Throwable exception = assertThrows(MockEnchantedException.class, () ->
                getObjectFromJsonObjectByKey(json, "id"));

        MatcherAssert.assertThat(
                "Exception jsonObjectFromString for expected verify body not JSON JSONArray",
                exception.getMessage(),
                StringContains.containsString("Expected verify id is not JSON: JSONArray"));
    }


    @Test
    void getStringFromJsonObjectByKeyException() {

        JSONObject json = jsonObjectFromString(validJson);

        Throwable exception = assertThrows(JsonMappersException.class, () ->
                getStringFromJsonObjectByKey(json, "id2"));

        MatcherAssert.assertThat(
                "Exception getStringFromJsonObjectByKey key is absent",
                exception.getMessage(),
                StringContains.containsString("JSONObject[\"id2\"] not found."));
    }


}
