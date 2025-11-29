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
package io.bugreaper.modules.mocks;

import io.bugreaper.core.config.ConfigLoader;
import io.bugreaper.core.config.YamlUtils;
import io.bugreaper.modules.api.setup.ApiAbstract;
import io.bugreaper.modules.mocks.exceptions.MockEnchantedException;
import io.bugreaper.modules.mocks.interfaces.MocksConfig;
import io.bugreaper.modules.mocks.interfaces.MocksInt;
import io.qameta.allure.Allure;
import io.qameta.allure.Param;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.awaitility.core.ConditionTimeoutException;
import org.opentest4j.AssertionFailedError;
import org.slf4j.LoggerFactory;

import java.util.Base64;
import java.util.List;
import java.util.Map;

import static io.bugreaper.core.allurereporter.AllureReporter.attachJson;
import static io.bugreaper.core.assertions.JsonAsserts.assertLenientValidJson;
import static io.bugreaper.core.filereaders.FileReader.readJsonFromFile;
import static io.bugreaper.core.mappers.StringMappers.*;
import static io.bugreaper.modules.mocks.enchanted.GetActual.getActualMethodsAndPathsList;
import static io.bugreaper.modules.mocks.enchanted.MockEnchantedDiffer.enchantedReport;
import static io.qameta.allure.model.Parameter.Mode.HIDDEN;
import static java.time.Duration.ofMillis;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Class for API integration with <a href="https://www.mock-server.com/">mock-server</a>
 * <p> Create @Step for every Created and Verified mock in your mock Class for allure report
 *
 * <p> Enchanted Report for mocks verify default:ON: {@link MocksApi#enchantedReport}, can be disabled by: {@link MocksApi#withEnchantedReport(boolean)})
 * <p> Await for some assert default: {@link MocksApi#awaitMs}, can be changed by: {@link MocksApi#withAwaitMs(int)}
 */
@SuppressWarnings("squid:S5960")
public class MocksApi extends ApiAbstract implements MocksInt, MocksConfig {

    /**
     * enchanted report feature
     */
    private boolean enchantedReport = true;

    /**
     * default ms await in tests
     */
    private int awaitMs = 2000;

    /**
     * This constructor initializes client for interaction with mock-server
     *
     * @param url  mock-server url ("http://localhost")
     * @param port mock-server port
     */
    public MocksApi(String url, int port) {
        super(url, port, LoggerFactory.getLogger("bugreaper-module-mocks"));
    }

    /**
     * Constructs mock-server client configuration.
     *
     * <p>Loads configuration values from a YAML file.</p>
     *
     * <p><b>Default file:</b> {@code bugreaper.yml}</p>
     * <p><b>Custom file:</b> using {@code -DbugreaperEnv=test} loads {@code bugreaper-test.yml}</p>
     *
     * <p><b>Required configuration keys:</b></p>
     * <ul>
     *     <li>{@code modules.mocks.url}</li>
     *     <li>{@code modules.mocks.port}</li>
     * </ul>
     *
     * <p><b>Optional configuration keys:</b></p>
     * <ul>
     *     <li>{@code modules.mocks.await}</li>
     *     <li>{@code modules.mocks.logging}</li>
     *     <li>{@code modules.mocks.enchanted-report}</li>
     * </ul>
     *
     * <p>Missing required keys will result in configuration errors.
     * Missing optional keys will fall back to predefined defaults.</p>
     */
    public MocksApi() {
        loadFromYaml();
    }

    private void loadFromYaml() {
        Map<String, Object> rawData = ConfigLoader.loadYaml();

        //required config fields
        this.url = YamlUtils.getStringValueByPath(rawData, "modules.mocks.url");
        this.port = YamlUtils.getIntegerValueByPath(rawData, "modules.mocks.port");


        //optional config fields
        Object loggingVal = YamlUtils.getValueByPath(rawData, "modules.mocks.logging", true);
        if (loggingVal instanceof Boolean logging) {
            withLogging(logging);
        }

        Object enchantedReportVal = YamlUtils.getValueByPath(rawData, "modules.mocks.enchanted-report", true);
        if (enchantedReportVal instanceof Boolean logging) {
            withEnchantedReport(logging);
        }

        Object awaitVal = YamlUtils.getValueByPath(rawData, "modules.mocks.await", true);
        if (awaitVal instanceof Integer assertMs) {
            withAwaitMs(assertMs);
        }

    }

    @Override
    public MocksApi withAwaitMs(int awaitMs) {
        if (awaitMs < 200) {
            throw new IllegalArgumentException("awaitMs too small (can`t bee less 200ms)");
        }
        this.awaitMs = awaitMs;
        return this;
    }

    @Override
    public MocksApi withLogging(boolean enable) {
        this.enableLogging = enable;
        return this;
    }

    @Override
    public MocksApi withEnchantedReport(boolean enchantedReport) {
        this.enchantedReport = enchantedReport;
        return this;
    }

    //getters

    public String getConfigSummary() {
        String info = String.format("""
        %s:
            url=%s
            port=%d
            await=%d
            enableLogging=%b
            enchantedReport=%b%n""",
                this.getClass().getSimpleName(),
                url, port, awaitMs,
               enableLogging, enchantedReport);

        logger.info(info);
        return info;
    }

    // interactions

    private Response sendPut(String endpoint, Object body) {
        return buildRequest()
                .body(body)
                .when()
                .put("/mockserver/" + endpoint);
    }

    private void sendPut(String endpoint) {
        buildRequest()
                .when()
                .put("/mockserver/" + endpoint);
    }


    @Override
    @Step("(MOCK)[RESET]: reset Mocks")
    public void resetMocks() {
        sendPut("reset");
    }

    @Override
    @Step("(MOCK)[CLEAN] Clean all recorded log messages from mock-server")
    public void cleanMockLogs() {
        sendPut("clear?type=log");
    }

    @Override
    @Step("(MOCK)[LOGS] Retrieve all recorded log messages from MOCK")
    public void showMockLogs() {
        sendPut("retrieve?type=LOGS");
    }

    @Override
    @Step("(MOCK)[LOGS] Retrieve all requests methods/path's from MOCK")
    public void showBaseRequestsList() {

        final Response mockRequests = getRequestBody("{}");
        final int requestsCount = mockRequests.jsonPath().getList("").size();
        List<String> actualRequestsBaseList = null;

        if (requestsCount != 0) {
            actualRequestsBaseList = getActualMethodsAndPathsList(mockRequests, requestsCount);
        }

        getBaseRequestsListReport(requestsCount, actualRequestsBaseList);
    }

    //Create

    @Override
    @Step("(MOCK)[CREATE] Create Mock")
    public void createMock(@Param(mode = HIDDEN) String mockExpectation) {

        try {
            assertEquals(
                    201,
                    sendPut("expectation", mockExpectation)
                            .then()
                            .extract()
                            .statusCode());
        } catch (AssertionFailedError e) {
            logger.error("Mock creation failed:", e);
            throw new IllegalArgumentException("Wrong mock creation setup");
        }
    }

    @Override
    public void createMockCustom(String description, String path) {
        sendPut("expectation", readJsonFromFile(path));
        //step
        Allure.step(String.format("(MOCK)[CREATE] custom mock: %s", description),
                (Allure.ThrowableContextRunnableVoid<Allure.StepContext>) step ->
                        createMock(readJsonFromFile(path))
        );
    }

    //Verify / asserts

    @Override
    @Step("(MOCK)[VERIFY] Assert count of ALL requests to Mock-server {receivedCount}")
    public void assertAllMocksCount(int receivedCount) {
        verifyMock(stringMapper("""
                        {
                            "httpRequest": {},
                            "times": {
                                "atLeast": ${receivedCount},
                                "atMost": ${receivedCount}
                            }
                        }""",
                Map.of(
                        "receivedCount", receivedCount
                )));
    }

    @Override
    @Step("(MOCK)(VERIFY) Assert count of ALL requests to Mock-server from:{from} to{to}")
    public void assertAllMocksCount(int from, int to) {
        verifyMock(stringMapper("""
                        {
                            "httpRequest": {},
                            "times": {
                                "atLeast": ${from},
                                "atMost": ${to}
                            }
                        }""",
                Map.of(
                        "from", from,
                        "to", to
                )));
    }

    @Override
    public void verifyMockCustom(String description, String path) {
        //step
        Allure.step(String.format("(MOCK)(VERIFY) Verify custom mock: %s", description),
                (Allure.ThrowableContextRunnableVoid<Allure.StepContext>) step ->
                        verifyMock(readJsonFromFile(path))
        );
    }


    @Step("(MOCK)[LOGS] Retrieve list with requests to mock-server ({cnt})")
    private void getBaseRequestsListReport(int cnt, @Param(mode = HIDDEN) List<String> list) {

        String content = listToString(list).replace("\\", "");

        logger.info("\nRequests to mock-server list({}):\n{}", cnt, content);
        attachJson("Requests to mock-server list(%d): ".formatted(cnt), content);
    }

    @Step("(MOCK)[VERIFY] Verify mock")
    public void verifyMock(@Param(mode = HIDDEN) String verifySetup) {

        assertLenientValidJson(verifySetup);

        try {
            assertEquals(
                    202,
                    sendPut("verify", verifySetup)
                            .then()
                            .extract()
                            .statusCode());
        } catch (AssertionFailedError e) {
            if (e.toString().contains("<202> but was: <400>")) {
                logger.error("Wrong mock verify setup:\n{}", verifySetup);
                throw new MockEnchantedException("Wrong mock verify setup");
            }

            if (enchantedReport) {
                logger.info("\nMock verify assertion FAILED, start mock verify enchanted report build");
                enchantedReport(getRequestBody("{}"), verifySetup);
            }

            // if enchanted off
            logger.warn("\nTurn on enchanted report for more info: .withEnchantedReport(true)");
            fail("Count of expected mock request(s) not match");
        }
    }

    @Override
    @Step("(MOCK)[VERIFY] Verify sequence")
    public void verifyMockSequence(String verifySetup) {

        assertEquals(
                202,
                sendPut("verifySequence", verifySetup)
                        .then()
                        .extract()
                        .statusCode(),
                "Expected mock sequence not match");

    }

    // verify with awaiting


    @Override
    public void verifyMockCustomWithAwait(String description, String path) {

        Allure.step(String.format("(MOCK)(VERIFY) Verify custom mock(with await): %s", description),
                (Allure.ThrowableContextRunnableVoid<Allure.StepContext>) step ->
                        verifyMockWithAwait(readJsonFromFile(path))
        );
    }

    @Override
    @Step("(MOCK)[VERIFY] Verify sequence with await")
    public void verifyMockSequenceWithAwait(@Param(mode = HIDDEN) String verifySetup) {
        attachJson("Sequence within" + formatMilliseconds(awaitMs), verifySetup);
        assertLenientValidJson(verifySetup);

        try {
        await().pollDelay(ofMillis(0)).atMost(ofMillis(awaitMs))
                .untilAsserted(
                        () -> verifyMockSequenceNoLogs(verifySetup));
        } catch (ConditionTimeoutException e) {
                verifyMockSequence(verifySetup);
        }
    }

    @Override
    @Step("(MOCK)[VERIFY] Verify mock with await")
    public void verifyMockWithAwait(@Param(mode = HIDDEN) String verifySetup) {
        attachJson("Mock verify setup within" + formatMilliseconds(awaitMs), verifySetup);
        assertLenientValidJson(verifySetup);
        try {
            await().pollDelay(ofMillis(0)).atMost(ofMillis(awaitMs))
                    .untilAsserted(
                            () -> verifyMockNoLogs(verifySetup));
        } catch (ConditionTimeoutException e) {
            verifyMock(verifySetup);
        }
    }

    @Override
    @Step("(MOCK)(VERIFY) Assert count of ALL requests to Mock-server from:{from} to{to} with await")
    public void assertMocksCountWithAwait(int from, int to) {
        try {
            await().pollDelay(ofMillis(0)).atMost(ofMillis(awaitMs))
                    .untilAsserted(
                            () -> assertAllMocksCountNoLogs(from, to));
        } catch (ConditionTimeoutException e) {
            assertAllMocksCount(from, to);
        }
    }

    private void assertAllMocksCountNoLogs(int from, int to) {
        verifyMockNoLogs(stringMapper("""
                        {
                            "httpRequest": {},
                            "times": {
                                "atLeast": ${from},
                                "atMost": ${to}
                            }
                        }""",
                Map.of(
                        "from", from,
                        "to", to
                )));
    }

    // for retry awaiting no steps/no logs!
    private void verifyMockSequenceNoLogs(String verifySetup) {

        assertEquals(
                202,
                buildRequest()
                        .body(verifySetup)
                        .when()
                        .filters(List.of()) //no logs no attachments !!!!
                        .put("/mockserver/verifySequence")
                        .then()
                        .extract()
                        .statusCode());

    }


    // for retry awaiting no steps/no logs!
    private void verifyMockNoLogs(String verifySetup) {
            assertEquals(
                    202,
                    buildRequest()
                            .body(verifySetup)
                            .when()
                            .filters(List.of()) //no logs no attachments !!!!
                            .put("/mockserver/verify")
                            .then()
                            .extract()
                            .statusCode());
    }

    //Grab

    @Override
    @Step("[MOCK]: Get value from request body {extractPath}")
    public String getRequestBodyValue(String mockSetup, String extractPath) {
        return buildRequest()
                .body(mockSetup)
                .put("retrieve?type=REQUESTS")
                .then()
                .extract()
                .path(extractPath).toString();
    }


    //move to core
    public static String baseAuthGenerate(String user, String pass) {
        return "Basic " + new String(Base64.getEncoder().encode(
                (user + ":" + pass).getBytes())
        );
    }

    /**
     * Method to get request body
     * <p> Used for report methods
     *
     * @param mockSetup -  setup for request
     * @return Response
     */
    private Response getRequestBody(String mockSetup) {
        return (Response) buildRequest()
                .body(mockSetup)
                .put("retrieve?type=REQUESTS")
                .then()
                .extract()
                .body();
    }

}
