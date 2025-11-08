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

import io.bugreaper.modules.mocks.MocksApi;
import io.qameta.allure.Param;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.opentest4j.AssertionFailedError;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static io.bugreaper.core.allurereporter.AllureReporter.*;
import static io.bugreaper.core.assertions.ListAsserts.containsJsonInList;
import static io.bugreaper.core.assertions.ListAsserts.equalsJsonInList;
import static io.bugreaper.modules.mocks.asserts.JsonAsserts.assertJsonNotEqual;
import static io.bugreaper.modules.mocks.asserts.ListAsserts.schemaCheckInList;
import static io.bugreaper.modules.mocks.asserts.ListAssertsEvery.everyJsonAsserBuilder;
import static io.bugreaper.modules.mocks.asserts.ListAssertsEvery.everyJsonSchemaAsserBuilder;
import static io.bugreaper.modules.mocks.enchanted.EnchantedAllureLogic.mockJsonMatcherAllure;
import static io.bugreaper.modules.mocks.enchanted.EnchantedAllureLogic.mockSchemaMatcherAllure;
import static io.bugreaper.modules.mocks.enchanted.GetActual.*;
import static io.bugreaper.modules.mocks.enchanted.GetExpected.*;
import static io.bugreaper.modules.mocks.enchanted.SummaryReportForVerify.summaryReport;
import static io.bugreaper.core.mappers.JsonMappers.getObjectFromJsonByKey;
import static io.bugreaper.core.mappers.JsonMappers.jsonObjectFromString;
import static io.qameta.allure.model.Parameter.Mode.HIDDEN;
import static org.junit.jupiter.api.Assertions.fail;
import static org.skyscreamer.jsonassert.JSONCompareMode.LENIENT;
import static org.skyscreamer.jsonassert.JSONCompareMode.STRICT;


/**
 * Work only for:
 * {@link MocksApi#verifyMock(String)}
 * {@link MocksApi#assertAllMocksCount(int)}
 * <p>Supported:
 * <p> -Json contains\equal check
 * <p> -Json schema check
 * <p> -Expected count exactly, from\to
 * <p>Not supported:
 * <p> -QueryParams (in progress)
 * <p> -Verify with awaiting on fail run verify with report
 * <p> -not JSON(string, array!, xml...)
 * <p> -Regex in Verify (method, path, body, header)
 * <p> -Not expected (like "!header")
 *
 * @since 0.0.1
 */
public final class MockEnchantedDiffer extends EnchantedSetup {


    private MockEnchantedDiffer() {
        throw new IllegalStateException("Utility class");
    }


    @Step("[MOCK-REPORT]: Enchanted mock verify report")
    public static void enchantedReport(
            @Param(mode = HIDDEN) Response allMockRequests,
            @Param(mode = HIDDEN) String verifySetup) {

        //step data_1 expected mock to JSON
        final JSONObject expectedMockSetup = jsonObjectFromString(verifySetup);

        final String allMockRequestsString = allMockRequests.asString();

        logger_mer.debug("All requests to mock-server:\n{}", allMockRequestsString);


        // base COUNT check


        //check1 check are requests exists
        noRequestsCheck(allMockRequestsString);

        //data_2  count of all requests
        final int actualRequestsCount = allMockRequests.jsonPath().getList("").size();

        //check2 check only for ALL mocks count AR ER diffs
        assertRequestsCount(expectedMockSetup, actualRequestsCount);


        // METHOD & PATH check


        //check3 check are expected path & method present
        final String[][] baseCheckResult = checkMethodAndPath(expectedMockSetup, allMockRequests, actualRequestsCount);


        // BODY check


        //check4 check bodies
        final String[][] bodyCheckResult = checkBodies(expectedMockSetup, allMockRequestsString, actualRequestsCount);


        // HEADERS check

        //check5 check headers
        final String[][] headerCheckResult = checkHeaders(expectedMockSetup, allMockRequestsString, actualRequestsCount);


        // SUMMARY report


        summaryReport(
                actualRequestsCount,
                baseCheckResult,
                bodyCheckResult,
                headerCheckResult,
                expectedMockSetup);

    }

    public static void noRequestsCheck(String requestsString) {
        try {
            assertJsonNotEqual("[]", requestsString);
        } catch (AssertionError s2) {
            fail("No requests to mock-server in test");
        }
    }

    private static void assertRequestsCount(JSONObject verifyJSON, int allRequestsCount) {
        if (getObjectFromJsonByKey(verifyJSON, REQUEST_KEY).length() == 0) {

            fail(MessageFormat.format("Failed assert all mocks count: expected {0} with AR <{1}>",
                    assertRequestsCountText(verifyJSON), allRequestsCount));
        }
    }

    private static String[][] checkMethodAndPath(
            JSONObject expectedMockSetup,
            Response allMockRequests,
            int actualRequestsCount) {

        //data_3 get object with method and/or path for check
        String expectedMethodPath = getExpectedMethodAndPath(expectedMockSetup);

        //data_3 get list with actual methods & path's (if no expected part than only for info)
        List<String> actualRequestsBaseList = getActualMethodsAndPathsList(allMockRequests, actualRequestsCount);

        //check_3 check are expected path & method present
        return assertObjectsVerify("method and/or path", expectedMethodPath, actualRequestsBaseList, false);
    }

    private static String[][] checkBodies(
            JSONObject expectedMockSetup,
            String allMockRequestsString,
            int actualRequestsCount) {


        // get expected setup body type & actual (can be different case-sensitive by user)
        Map<String, String> expectedBodyType = getExpectedBodyType(expectedMockSetup);

        String bodyType = "";
        String bodyNotSensitiveType = "";
        for (Map.Entry<String, String> entry : expectedBodyType.entrySet()) {
            bodyType = entry.getKey();
            bodyNotSensitiveType = entry.getValue();
        }


        String[][] bodyCheckResult = new String[actualRequestsCount][];
        //body Json check
        if (!Objects.equals(bodyType, "absent")) {

            //data_4 get expected body
            String expectedBody = getExpectedBody(expectedMockSetup, bodyType, bodyNotSensitiveType);

            //data_4 is strict check flag
            Boolean isStrict = getExpectedStrict(expectedMockSetup, bodyType, bodyNotSensitiveType);
            logger_mer.debug("is Body check STRICT: {}", isStrict);

            //data_4 get list with actual bodies (if no expected part than only for info)
            List<String> actualBodiesList = getActualBodiesList(allMockRequestsString, actualRequestsCount);

            if (!Objects.equals(bodyType, SCHEMA_KEY)) {
                //check_4a check bodies(JSON)
                return assertObjectsVerify(BODY_KEY, expectedBody, actualBodiesList, isStrict);
            } else {
                //check_4b check schema(JSON)
                return assertSchemaVerify(expectedBody, actualBodiesList);
            }

        } else {
            return skippedAssert(actualRequestsCount, bodyCheckResult, BODY_KEY);
        }
    }

    private static String[][] checkHeaders(
            JSONObject expectedMockSetup,
            String allMockRequestsString,
            int actualRequestsCount) {

        //data_5 get expected headers
        String expectedHeaders = getExpectedHeaders(expectedMockSetup);

        //data_5 get list with actual headers (if no expected part than only for info)
        List<String> actualHeadersList = getActualHeadersList(allMockRequestsString, actualRequestsCount);

        return assertObjectsVerify("headers", expectedHeaders, actualHeadersList, false);
    }

    /**
     * @param assertKey         key for assert (body, headers...)
     * @param expectedObject    expected object for assert
     * @param actualObjectsList List<String> list with actual objects (bodies, headers...)
     * @param isStrict          flag for STRICT assert (used only foe body assert)
     * @return String[][] = [][№, passed/failed/skipped, actualObject]
     */
    private static String[][] assertObjectsVerify(
            @Param(mode = HIDDEN) String assertKey,
            @Param(mode = HIDDEN) String expectedObject,
            @Param(mode = HIDDEN) List<String> actualObjectsList,
            @Param(mode = HIDDEN) Boolean isStrict) {


        //return skipped if null
        if (expectedObject == null) {

            final int actualRequestsCount = actualObjectsList.size();
            String[][] assertResult = new String[actualRequestsCount][];

            return skippedAssert(actualRequestsCount, assertResult, assertKey);
        }

        return assertLogicMethod(assertKey, expectedObject, actualObjectsList, isStrict);

    }

    @Step("Check assert for {assertKey}")
    private static String[][] assertLogicMethod(
            @Param(mode = HIDDEN) String assertKey,
            @Param(mode = HIDDEN) String expectedObject,
            @Param(mode = HIDDEN) List<String> actualObjectsList,
            @Param(mode = HIDDEN) Boolean isStrict) {

        String[][] assertResult;


        String assertion;
        //Strict logic on/off
        if (Boolean.TRUE.equals(isStrict)) {
            assertion = "EQUAL";
            assertResult = everyJsonAsserBuilder(expectedObject, actualObjectsList, STRICT);
        } else {
            assertion = "CONTAINS";
            assertResult = everyJsonAsserBuilder(expectedObject, actualObjectsList, LENIENT);
        }


        //allure ER AR report
        String expectedAllureName = "Expected " + assertion + " " + assertKey;
        String actualListAllureName = "Actual list: " + assertKey;
        logger_mer.debug("\n{}{}", expectedAllureName, expectedObject);

        attachJson(expectedAllureName, expectedObject);
        attachFromList(actualListAllureName, actualObjectsList);

        //allure difference report
        mockJsonMatcherAllure(
                assertResult,
                assertKey,
                expectedObject,
                isStrict,
                assertion);

        //Assert part

        if (Boolean.TRUE.equals(isStrict)) {
            try {
                equalsJsonInList(expectedObject, actualObjectsList);
            } catch (AssertionFailedError e) {
                fail("Assert is actual requests EQUAL expected " + assertKey + " FAILED");
            }
        } else {
            try {
                containsJsonInList(expectedObject, actualObjectsList);
            } catch (AssertionFailedError e) {
                fail("Assert is actual requests CONTAINS expected " + assertKey + " FAILED");
            }
        }
        return assertResult;
    }


    /**
     * @param expectedSchema   schema setup
     * @param actualBodiesList List(String) with actual bodies
     * @return String[][] = [][№, passed/failed, difference]
     */
    @Step("Check assert for jsonSchema")
    public static String[][] assertSchemaVerify(
            @Param(mode = HIDDEN) String expectedSchema,
            @Param(mode = HIDDEN) List<String> actualBodiesList) {


        //allure ER AR report
        String expectedAllureName = "Expected schema";
        String actualListAllureName = "Actual list: ";
        logger_mer.debug("\n{}{}", expectedAllureName, expectedSchema);

        attachJson(expectedAllureName, expectedSchema);
        attachFromList(actualListAllureName, actualBodiesList);

        String[][] assertResult = everyJsonSchemaAsserBuilder(expectedSchema, actualBodiesList);


        mockSchemaMatcherAllure(assertResult, actualBodiesList);

        //  Assert part

        try {
            schemaCheckInList(expectedSchema, actualBodiesList);
        } catch (AssertionFailedError e) {
            fail("Schema check in actual requests FAILED" + e);
        }
        return assertResult;
    }

    private static String[][] skippedAssert(int actualRequestsCount, String[][] bodyCheckResult, String assertKey) {

        reporter(SKIPPED_MESSAGE.formatted(assertKey)); //allure vs allure log info

        for (int i = 0; i < actualRequestsCount; i++) {

            String[] add;

            add = new String[]{String.valueOf(i + 1), "skipped"};
            bodyCheckResult[i] = add;

        }
        return bodyCheckResult;
    }

}
