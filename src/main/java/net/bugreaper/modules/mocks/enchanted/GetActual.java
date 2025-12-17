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
package net.bugreaper.modules.mocks.enchanted;

import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static net.bugreaper.core.mappers.JsonMappers.*;
import static net.bugreaper.core.mappers.StringMappers.listToString;
import static net.bugreaper.modules.mocks.mappers.JsonMappersRest.*;


public class GetActual extends EnchantedSetup {


    public static List<String> getActualMethodsAndPathsList(Response mockRequests, int allRequestsCount) {
        List<String> actualRequestsBaseList = new ArrayList<>();
        for (int i = 0; i < allRequestsCount; i++) {

            JSONObject jsonObject = new JSONObject();

            String method = mockRequests.jsonPath().getString("[" + i + "].method");

            String path = mockRequests.jsonPath().getString("[" + i + "].path");

            jsonObject = putStringToJson(jsonObject, "method", method);
            jsonObject = putStringToJson(jsonObject, "path", path);

            String methodPath = jsonToStringBeautifier(jsonObject);

            if (logger_mer.isDebugEnabled()) {
                logger_mer.debug("Actual method & path {}:\n{}", i + 1, methodPath.replace("\\", ""));
            }

            actualRequestsBaseList.add(methodPath);
        }
        return actualRequestsBaseList;
    }

    public static List<String> getActualBodiesList(String mockRequests, int allRequestsCount) {

        JSONArray requestsArrayToMock = jsonArrayFromString(mockRequests);

        ArrayList<String> actualBodiesList = new ArrayList<>();

        for (int i = 0; i < allRequestsCount; i++) {

            JSONObject requestsToMock;

            requestsToMock = getObjectFromJsonArrayByNum(requestsArrayToMock, i);

            // check is body present in request, if not replace with enum
            if (requestsToMock.has(BODY_KEY)) {
                actualBodiesList.add(getActualBody(requestsToMock, i));
            } else {
                actualBodiesList.add(returnEmptyObject(BODY_KEY, i + 1));
            }
        }

        if (logger_mer.isDebugEnabled()) {
            logger_mer.debug(listToString(actualBodiesList));
        }

        return actualBodiesList;
    }


    private static String getActualBody(JSONObject requestsToMock, int i) {
        JSONObject jsonObject = new JSONObject();

        JSONObject rawBody = getObjectFromJsonObjectByKey(requestsToMock, BODY_KEY);
        logger_mer.debug("Raw body is: {}", rawBody);


        if (rawBody.has("type")) {
            String type = getStringFromJsonObjectByKey(rawBody, "type");
            if (!Objects.equals(type, "JSON")) {
                logger_mer.info("Actual body will be replaced because it's not JSON type: {}", type);
                jsonObject = putStringToJson(jsonObject, BODY_WRONG_KEY, "true");
            } else {
                JSONObject subBody = getObjectFromJsonObjectByKey(rawBody, "json");
                logger_mer.debug("Grab actual body from 'json' object:\n{}", subBody);
                jsonObject = subBody;
            }
        } else {
            logger_mer.debug("Grab actual body :\n{}", rawBody);
            jsonObject = rawBody;
        }

        String body = jsonToStringBeautifier(jsonObject);

        if (logger_mer.isDebugEnabled()) {
            logger_mer.debug("Actual body {}:\n{}", i + 1, body.replace("\\", ""));
        }

        return body;
    }


    public static List<String> getActualHeadersList(String mockRequests, int allRequestsCount) {

        JSONArray requestsArrayToMock = jsonArrayFromString(mockRequests);

        List<String> actualHeadersList = new ArrayList<>();

        for (int i = 0; i < allRequestsCount; i++) {

            JSONObject requestsToMock;

            requestsToMock = getObjectFromJsonArrayByNum(requestsArrayToMock, i);

            JSONObject jsonObject = new JSONObject();
            if (requestsToMock.has(HEADERS_KEY)) {


                JSONObject headers = getObjectFromJsonObjectByKey(requestsToMock, HEADERS_KEY);

                jsonObject = putObjectToJson(jsonObject, HEADERS_KEY, headers);

                String actualHeader = jsonToStringBeautifier(jsonObject);

                if (logger_mer.isDebugEnabled()) {
                    logger_mer.debug("Actual headers {}:\n{}", i + 1, actualHeader.replace("\\", ""));
                }

                actualHeadersList.add(actualHeader);

            } else {
                //impossible to be empty
                actualHeadersList.add(returnEmptyObject(HEADERS_KEY, i + 1));
            }
        }

        return actualHeadersList;
    }

    private static String returnEmptyObject(String key, int num) {
        JSONObject jsonObject = new JSONObject();
        jsonObject = putStringToJson(jsonObject, BODY_ABSENT_KEY, "true");

        String empty = jsonToStringBeautifier(jsonObject);

        if (logger_mer.isDebugEnabled()) {
            logger_mer.debug(REPLACE_ABSENT_MESSAGE, key, empty.replace("\\", ""));
            logger_mer.debug("Actual {} {}:\n{}", key, num, empty);
        }

        return empty;
    }


}
