package io.bugreaper.modules.rest.mocks.mappers;

import io.bugreaper.modules.mocks.exceptions.JsonMappersException;
import io.bugreaper.modules.mocks.exceptions.MockEnchantedException;
import io.bugreaper.modules.mocks.mappers.JsonMappers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.StringContains;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static io.bugreaper.modules.mocks.mappers.JsonMappers.*;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JsonMappersExceptionsTests {

    String brokenJson = """
            {
              "id: 1,
            }""";

    String validJson = """
            {
              "id": 1
            }""";

    String validArray = """
            [{
              "id": 1
            }]""";
    String invalidArray = """
            [{
              "id": 2
            }""";


    @Test
    void utilityClass() throws NoSuchMethodException {
        Constructor<JsonMappers> constructor = JsonMappers.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        InvocationTargetException thrown = assertThrows(InvocationTargetException.class, constructor::newInstance);

        Throwable cause = thrown.getCause();
        assert (cause instanceof IllegalStateException);
        assert ("Utility class".equals(cause.getMessage()));
    }


    @Test
    void jsonObjectFromStringException() {


        Throwable exception = assertThrows(MockEnchantedException.class, () ->
                jsonObjectFromString(brokenJson));

        MatcherAssert.assertThat(
                "Exception for jsonObjectFromString wrong JSON ",
                exception.getMessage(),
                is("Failed to validate JSON"));
    }

    @Test
    void jsonArrayFromStringException() {
        Throwable exception = assertThrows(JsonMappersException.class, () ->
                jsonArrayFromString(invalidArray));

        MatcherAssert.assertThat(
                "Exception for jsonArrayFromString wrong ARRAY ",
                exception.getMessage(),
                StringContains.containsString("Failed convert string to JsonArray"));
    }

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

    @Test
    void getObjectFromJsonArrayByNumException() {

        JSONArray array = jsonArrayFromString(validArray);

        Throwable exception = assertThrows(JsonMappersException.class, () ->
                getObjectFromJsonArrayByNum(array, 1));

        MatcherAssert.assertThat(
                "Exception getObjectFromJsonArrayByNum element is absent",
                exception.getMessage(),
                StringContains.containsString("JSONArray[1] not found"));
    }


}
