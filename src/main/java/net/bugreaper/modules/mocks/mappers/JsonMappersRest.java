package net.bugreaper.modules.mocks.mappers;


import net.bugreaper.modules.mocks.exceptions.MockEnchantedException;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;


public final class JsonMappersRest {

    private JsonMappersRest() {
        throw new IllegalStateException("Utility class");
    }


    public static JSONObject getObjectFromJsonObjectByKey(JSONObject jsonObject, String key) {
        try {
            return jsonObject.getJSONObject(key);
        } catch (JSONException e) {

            String error = e.toString();

            String exceptionString = "Expected verify {0} is not JSON: {1}";

            if (error.contains("JSONArray")) {
                throw new MockEnchantedException(
                        MessageFormat.format(exceptionString, key, "JSONArray"),
                        e);
            } else if (error.contains("String")) {
                throw new MockEnchantedException(
                        MessageFormat.format(exceptionString, key, "String"),
                        e);
            } else {
                throw new MockEnchantedException(
                        MessageFormat.format(exceptionString, key, "???"),
                        e);
            }
        }
    }

}
