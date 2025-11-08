/*
 *  Copyright 2025 Oleksii Betin
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.bugreaper.modules.mocks.enchanted;


import io.bugreaper.modules.mocks.exceptions.MockEnchantedException;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.*;

import static io.bugreaper.core.mappers.JsonMappers.jsonToStringBeautifier;
import static io.bugreaper.core.mappers.JsonMappers.putObjectToJson;
import static io.bugreaper.modules.mocks.mappers.JsonMappersRest.*;

public class GetExpected extends EnchantedSetup {


    public static Map<String, String> grabExpectedCount(JSONObject expectedMockJson) {

        Map<String, String> countMap = new LinkedHashMap<>();

        for (String key : FROM_TO_FIELDS) {
            try {
                countMap.put(key, String.valueOf(expectedMockJson.getJSONObject("times").getInt(key)));
            } catch (JSONException ex) {
                logger_mer.debug(NO_VALUE_MESSAGE, key);
            }
        }

        // default values
        if (countMap.isEmpty()) {
            countMap.put(FROM_TO_FIELDS.get(0), "1");
            countMap.put(FROM_TO_FIELDS.get(1), "1");
        }

        return countMap;
    }

    /**
     * @param expectedMockSetup mockVerify JSON
     * @return Map(String, String) example {"json": "json"}, {"rawBody": "body"}, {"jsonSchema": "JSONSchema"}
     * (key can be: rawBody/json/absent/jsonSchema)
     * (value body or same as key or in other case - used for grab from JSON in next steps)
     */
    public static Map<String, String> getExpectedBodyType(JSONObject expectedMockSetup) {

        Map<String, String> verifyType = new LinkedHashMap<>();

        JSONObject httpRequest = getRequest(expectedMockSetup);

        if (httpRequest.has(BODY_KEY)) {

            JSONObject rawBody = getRawBody(httpRequest);

            //for empty body
            if (rawBody.isEmpty()) {
                verifyType.put(RAW_BODY_KEY, BODY_KEY);
            } else {
                verifyType.putAll(getExpectedBodyTypeMethod(rawBody, verifyType));
            }

        } else {
            verifyType.put("absent", "absent");
        }

        logger_mer.debug("Verify body type is: {}", verifyType);

        return verifyType;
    }

    private static Map<String, String> getExpectedBodyTypeMethod(JSONObject rawBody, Map<String, String> verifyType) {

        List<String> keys = new ArrayList<>();
        Iterator<String> iterator = rawBody.keys();

        while (iterator.hasNext()) {
            String key = iterator.next();
            keys.add(key);
        }

        for (String key : keys) {
            logger_mer.debug(key);

            if (SCHEMA_KEY.equalsIgnoreCase(key)) {
                verifyType.put(SCHEMA_KEY, key);
            } else if (JSON_KEY.equalsIgnoreCase(key)) {
                verifyType.put(JSON_KEY, key);
            } else {
                verifyType.put(RAW_BODY_KEY, BODY_KEY);
            }
        }

        return verifyType;
    }


    /**
     * @param expectedMockSetup    JSONObject with mockVerify
     * @param bodyType             type of body for logic from {@link GetExpected#getExpectedBodyType(JSONObject)}
     * @param bodyNotSensitiveType type of body for grab from {@link GetExpected#getExpectedBodyType(JSONObject)}
     * @return String with Expected body
     */
    public static String getExpectedBody(
            JSONObject expectedMockSetup,
            String bodyType,
            String bodyNotSensitiveType) {

        JSONObject expectedJsonObject;

        JSONObject httpRequest = getRequest(expectedMockSetup);
        JSONObject rawBody = getRawBody(httpRequest);

        expectedJsonObject = switch (bodyType) {
            case RAW_BODY_KEY -> rawBody;
            case JSON_KEY, SCHEMA_KEY -> getObjectFromJsonObjectByKey(rawBody, bodyNotSensitiveType);
            default -> {
                logger_mer.error("Error trying grab body from:\n{}\nbody type: {}/{}", expectedMockSetup, bodyType, bodyNotSensitiveType);
                throw new MockEnchantedException("Can`t grab expected verify body");
            }
        };


        String body = jsonToStringBeautifier(expectedJsonObject);

        if (logger_mer.isDebugEnabled()) {
            logger_mer.debug("Expected body({}):\n{}", bodyType, body.replace("\\", ""));
        }

        return body;
    }

    public static String getExpectedHeaders(JSONObject expectedMockSetup) {

        JSONObject expectedJsonObject = new JSONObject();

        JSONObject httpRequest = getRequest(expectedMockSetup);

        if (httpRequest.has(HEADERS_KEY)) {

            JSONObject expectedHeader = getObjectFromJsonObjectByKey(httpRequest, HEADERS_KEY);
            expectedJsonObject = putObjectToJson(expectedJsonObject, HEADERS_KEY, expectedHeader);

        } else {
            return null;

        }

        String headers = jsonToStringBeautifier(expectedJsonObject);

        if (logger_mer.isDebugEnabled()) {
            logger_mer.debug("Expected headers:\n{}", headers.replace("\\", ""));
        }

        return headers;
    }


    public static boolean getExpectedStrict(
            JSONObject expectedMockSetup,
            String bodyType,
            String bodyNotSensitiveType) {


        JSONObject httpRequest = getRequest(expectedMockSetup);
        JSONObject expectedBody = getRawBody(httpRequest);

        if (
                expectedBody.has(JSON_KEY) ||
                        (Objects.equals(bodyType, JSON_KEY) && expectedBody.has(bodyNotSensitiveType))) {

            try {
                String matchType = expectedBody.getString("matchType");
                if (Objects.equals(matchType, "STRICT")) {
                    return true;
                }
            } catch (JSONException ex) {
                logger_mer.info("matchType not found");
                return false;

            }

        }

        return false;
    }


    public static String getExpectedMethodAndPath(JSONObject expectedMockSetup) {

        JSONObject expectedJsonObject = new JSONObject();

        JSONObject httpRequest = getRequest(expectedMockSetup);

        if (httpRequest.has(BASE_FIELDS.get(0)) || httpRequest.has(BASE_FIELDS.get(1))) {

            for (String key : BASE_FIELDS) {
                try {
                    String value = httpRequest.getString(key);
                    expectedJsonObject.put(key, value);
                } catch (JSONException je) {
                    logger_mer.debug(NO_VALUE_MESSAGE, key);
                }
            }
        } else {
            return null;
        }

        String methodPath = jsonToStringBeautifier(expectedJsonObject);

        if (logger_mer.isDebugEnabled()) {
            logger_mer.debug("Expected method & path:\n{}", methodPath.replace("\\", ""));
        }

        return methodPath;
    }

    public static String assertRequestsCountText(JSONObject verifyJSON) {

        Map<String, String> expectedCount = grabExpectedCount(verifyJSON);

        String from = expectedCount.get("atLeast");
        String to = expectedCount.get("atMost");


        if (Objects.equals(from, to)) {
            return MessageFormat.format("Exactly <{0}>", from);
        } else if (from == null) {
            return MessageFormat.format("To:<{0}>", to);
        } else if (to == null) {
            return MessageFormat.format("From:<{0}>", from);
        } else if (Integer.parseInt(from) > Integer.parseInt(to)) {
            return MessageFormat.format("From:<{0}> is grater then To:<{1}>", from, to);
        } else {
            return MessageFormat.format("From:<{0}> To:<{1}>", from, to);
        }

    }

    private static JSONObject getRequest(JSONObject httpRequest) {
        return getObjectFromJsonObjectByKey(httpRequest, REQUEST_KEY);
    }

    private static JSONObject getRawBody(JSONObject verifyJSON) {
        return getObjectFromJsonObjectByKey(verifyJSON, BODY_KEY);
    }


}
