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
package net.bugreaper.modules.mocks;

import net.bugreaper.core.config.YamlUtils;
import net.bugreaper.modules.api.setup.ApiAbstract;
import net.bugreaper.modules.mocks.exceptions.MockEnchantedException;
import net.bugreaper.modules.mocks.interfaces.MocksConfig;
import net.bugreaper.modules.mocks.interfaces.MocksInt;
import io.qameta.allure.Allure;
import io.qameta.allure.Param;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.awaitility.core.ConditionTimeoutException;
import org.slf4j.LoggerFactory;

import java.util.Base64;
import java.util.List;
import java.util.Map;

import static net.bugreaper.core.allurereporter.AllureReporter.attachJson;
import static net.bugreaper.core.allurereporter.AllureReporter.attachObject;
import static net.bugreaper.core.assertions.JsonAsserts.assertLenientValidJson;
import static net.bugreaper.core.filereaders.FileReader.readJsonFromFile;
import static net.bugreaper.core.mappers.StringMappers.*;
import static net.bugreaper.core.utils.AwaitUtils.awaitCustom;
import static net.bugreaper.modules.mocks.enchanted.GetActual.getActualMethodsAndPathsList;
import static net.bugreaper.modules.mocks.enchanted.MockEnchantedDiffer.enchantedReport;
import static io.qameta.allure.model.Parameter.Mode.HIDDEN;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * API helper that provides a common API for operating with  <a href="https://www.mock-server.com/">mock-server</a> using RestAssured.
 *
 * <p>It is recommended to use a single instance:
 * {@code MocksApi mockApi = MocksApi.getInstance();}
 * </p>
 *
 * <p> Enchanted Report for mocks verify default:ON: {@link MocksApi#enchantedReport}, can be disabled by: {@link MocksApi#setEnchantedReport(boolean)})
 * <p>Default await timeout for assertions with await is configured by {@link #awaitMs}.
 * It can be changed using {@link #setAwaitMs(int)} or configuration.</p>
 *
 * @author Oleksii Betin "ambu550"
 * @since 1.0.0
 */
@SuppressWarnings("squid:S5960")
public class MocksApi extends ApiAbstract implements MocksInt, MocksConfig {

    private static MocksApi instance;

    /**
     * enchanted report feature
     */
    private volatile boolean enchantedReport = true;

    /**
     * default ms await in tests
     */
    private volatile int awaitMs = 2000;

    /**
     * specific ms await in specific assert (configure with {@link #withAwaitMs(int)})
     */
    private final ThreadLocal<Integer> specificAwaitMs = ThreadLocal.withInitial(() -> 0);


    /**
     * This constructor initializes client for interaction with mock-server
     *
     * @param url  mock-server url ({@code "http://localhost"})
     * @param port mock-server port
     */
    public MocksApi(String url, int port) {
        super(url, port, LoggerFactory.getLogger("bugreaper-module-mocks"));
    }

    /**
     * Returns the instance of {@link MocksApi} with config builder {@link #MocksApi()}.
     * <p>
     * This implementation is thread-safe using method-level synchronization.
     *
     * @return the shared instance of {@link MocksApi}
     * @see #MocksApi() config setup
     *
     * @throws IllegalArgumentException if the configuration contains invalid values
     */
    public static synchronized MocksApi getInstance() {
        if (instance == null) {
            instance = new MocksApi();
        }

        return instance;
    }

    /**
     * Constructs a mock-server Api client using YAML configuration.
     *
     * <p>Loads configuration values from a YAML file.</p>
     *
     * <p><b>Default file:</b> {@code bugreaper.yml}</p>
     * <p><b>Custom file:</b> using {@code -DbugreaperEnv=test} loads {@code bugreaper-test.yml}</p>
     *
     * <pre>
     * modules:
     *   mocks:
     *     url: http://localhost
     *     port: 1082
     *     await: 440 # (optional)
     *     logging: true # (optional)
     *     enchanted-report: false # (optional)
     * </pre>
     *
     * <p>Missing required keys will result in configuration errors.
     * Missing optional keys will fall back to predefined defaults.</p>
     *
     * @throws IllegalArgumentException if the configuration contains invalid values
     */
    public MocksApi() {
        loadFromYaml();
    }

    private void loadFromYaml() {

        //required config fields
        this.url = YamlUtils.getStringValueByPath("modules.mocks.url");
        this.port = YamlUtils.getIntegerValueByPath("modules.mocks.port");


        //optional config fields
        Object loggingVal = YamlUtils.getValueByPath("modules.mocks.logging", true);
        if (loggingVal instanceof Boolean logging) {
            setLogging(logging);
        }

        Object enchantedReportVal = YamlUtils.getValueByPath("modules.mocks.enchanted-report", true);
        if (enchantedReportVal instanceof Boolean logging) {
            setEnchantedReport(logging);
        }

        Object awaitVal = YamlUtils.getValueByPath("modules.mocks.await", true);
        if (awaitVal instanceof Integer assertMs) {
            setAwaitMs(assertMs);
        }

    }

    @Override
    public MocksApi setAwaitMs(int awaitMs) {
        if (awaitMs < 200) {
            throw new IllegalArgumentException("awaitMs too small (can`t bee less 200ms)");
        }
        this.awaitMs = awaitMs;
        return this;
    }

    @Override
    public MocksApi withAwaitMs(int specificAwaitMs) {
        if (specificAwaitMs < 200) {
            throw new IllegalArgumentException("specificAwaitMs too small (can`t bee less 200ms)");
        }
        this.specificAwaitMs.set(specificAwaitMs);
        return this;
    }

    @Override
    public MocksApi setLogging(boolean enable) {
        this.enableLogging = enable;
        return this;
    }

    @Override
    public MocksApi setEnchantedReport(boolean enchantedReport) {
        this.enchantedReport = enchantedReport;
        return this;
    }

    //getters

    @Override
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
    @Step("(MOCK)[RESET]: reset mock-server")
    public void resetMocks() {
        sendPut("reset");
    }

    @Override
    @Step("(MOCK)[CLEAN] Clean all recorded log messages from mock-server")
    public void cleanMockLogs() {
        sendPut("clear?type=log");
    }

    @Override
    @Step("(MOCK)[LOGS] Retrieve all recorded log messages from mock-server")
    public void showMockLogs() {
        sendPut("retrieve?type=LOGS");
    }

    @Override
    @Step("(MOCK)[LOGS] Retrieve all requests to mock-server")
    public void showMockRequests() {sendPut("retrieve?type=LOGS");
    }

    @Override
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
    @Step("(MOCK)[CREATE] Create mock-server expectation")
    public void createMock(@Param(mode = HIDDEN) String mockExpectation) {

        try {
            assertEquals(
                    201,
                    sendPut("expectation", mockExpectation)
                            .then()
                            .extract()
                            .statusCode());
        } catch (AssertionError e) {
            logger.error("Expectation creation failed:", e);
            throw new IllegalArgumentException("Failed to create mock-server expectation");
        }
    }

    @Override
    public void createMockCustom(String description, String path) {
        //step
        Allure.step(String.format("(MOCK)[CREATE] custom expectation: %s", description),
                (Allure.ThrowableContextRunnableVoid<Allure.StepContext>) step ->
                        createMock(readJsonFromFile(path))
        );
    }

    //Verify / asserts

    @Override
    @Step("(MOCK)[VERIFY] Mock-server received exactly <{receivedCount}> requests")
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
    @Step("(MOCK)(VERIFY) Mock-server received requests from <{from}> to <{to}>")
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
        Allure.step(String.format("(MOCK)(VERIFY) Verify mock request: %s", description),
                (Allure.ThrowableContextRunnableVoid<Allure.StepContext>) step ->
                        verifyMock(readJsonFromFile(path))
        );
    }


    @Step("(MOCK)[LOGS] Retrieve list with requests methods/path's to mock-server ({cnt})")
    private void getBaseRequestsListReport(int cnt, @Param(mode = HIDDEN) List<String> list) {

        String content = listToString(list).replace("\\", "");

        logger.info("\nRequests to mock-server list({}):\n{}", cnt, content);
        attachJson("Requests to mock-server list(%d): ".formatted(cnt), content);
    }

    @Step("(MOCK)[VERIFY] Verify mock-server request")
    public void verifyMock(@Param(mode = HIDDEN) String verifySetup) {

        assertLenientValidJson(verifySetup);

        try {
            assertEquals(
                    202,
                    sendPut("verify", verifySetup)
                            .then()
                            .extract()
                            .statusCode());
        } catch (AssertionError e) {
            if (e.toString().contains("<202> but was: <400>")) {
                logger.error("Wrong request verify setup:\n{}", verifySetup);
                throw new MockEnchantedException("Wrong request verify setup");
            }

            if (enchantedReport) {
                logger.info("\nMock-server request verification FAILED, starting enhanced mock verification report generation.");
                enchantedReport(getRequestBody("{}"), verifySetup);
            }

            // if enchanted off
            logger.warn("\nTurn on enchanted report for more info: by setter .setEnchantedReport(true) or in config modules:mocks:enchanted-report:true");
            throw new AssertionError("Mock-server request verification failed");
        }
    }

    @Override
    @Step("(MOCK)[VERIFY] Verify mock-server request sequence")
    public void verifyMockSequence(String verifySequenceSetup) {

        assertEquals(
                202,
                sendPut("verifySequence", verifySequenceSetup)
                        .then()
                        .extract()
                        .statusCode(),
                "Mock-server request sequence does not match");

    }

    // verify with awaiting


    @Override
    public void verifyMockCustomWithAwait(String description, String path) {

        Allure.step(String.format("(MOCK)(VERIFY) Verify mock-server request using await: %s", description),
                (Allure.ThrowableContextRunnableVoid<Allure.StepContext>) step ->
                        verifyMockWithAwait(readJsonFromFile(path))
        );
    }

    @Override
    @Step("(MOCK)[VERIFY] Verify mock-server request sequence using await")
    public void verifyMockSequenceWithAwait(@Param(mode = HIDDEN) String verifySequenceSetup) {
        verifyMockSequenceWithAwait(verifySequenceSetup, await());
    }

    private void verifyMockSequenceWithAwait(String verifySequenceSetup, int providedAwaitMs) {

        attachJson("Sequence within " + formatMilliseconds(providedAwaitMs), verifySequenceSetup);

        assertLenientValidJson(verifySequenceSetup);

        try {
            awaitCustom(providedAwaitMs).untilAsserted(
                    () -> verifyMockSequenceNoLogs(verifySequenceSetup));
        } catch (ConditionTimeoutException e) {
            verifyMockSequence(verifySequenceSetup);
        }
    }

    @Override
    @Step("(MOCK)[VERIFY] Verify mock-server request using await")
    public void verifyMockWithAwait(@Param(mode = HIDDEN) String verifySetup) {
        verifyMockWithAwaitMethod(verifySetup, await());
    }

    private void verifyMockWithAwaitMethod(String verifySetup, int providedAwaitMs) {

        attachJson("Mock verify setup within " + formatMilliseconds(providedAwaitMs), verifySetup);

        assertLenientValidJson(verifySetup);
        try {
            awaitCustom(providedAwaitMs).untilAsserted(
                    () -> verifyMockNoLogs(verifySetup));
        } catch (ConditionTimeoutException e) {
            verifyMock(verifySetup);
        }
    }

    @Override
    @Step("(MOCK)(VERIFY) Mock-server received requests from <{from}> to <{to}> using await")
    public void assertMocksCountWithAwait(int from, int to) {
        assertMocksCountWithAwaitMethod(from, to, await());
    }

    private void assertMocksCountWithAwaitMethod(int from, int to, int providedAwaitMs) {
        try {
            awaitCustom(providedAwaitMs).untilAsserted(
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
                buildSimpleRequest() //no logs no attachments !!!!
                        .body(verifySetup)
                        .put("/mockserver/verifySequence")
                        .then()
                        .extract()
                        .statusCode());

    }


    // for retry awaiting no steps/no logs!
    private void verifyMockNoLogs(String verifySetup) {

            assertEquals(
                    202,
                    buildSimpleRequest() //no logs no attachments !!!!
                            .body(verifySetup)
                            .put("/mockserver/verify")
                            .then()
                            .extract()
                            .statusCode());
    }

    //Grab

    @Override
    @Step("[MOCK]: Get value from request {extractPath}")
    public Object getRequestValue(String mockSetup, String extractPath) {
        Object obj = buildRequest()
                .body(mockSetup)
                .put("retrieve?type=REQUESTS")
                .then()
                .extract()
                .path(extractPath);

        attachObject(extractPath, obj);

        return obj;
    }


    //move to core
    public static String baseAuthGenerate(String user, String pass) {
        return "Basic " + new String(Base64.getEncoder().encode(
                (user + ":" + pass).getBytes())
        );
    }

    /**
     * Retrieves the request body configured in the mock setup.
     *
     * <p>Used by reporting methods.</p>
     *
     * @param mockSetup mock setup for the request
     * @return response containing the request body
     */
    private Response getRequestBody(String mockSetup) {
        return (Response) buildSimpleRequest() //no logs no attachments !!!!
                .body(mockSetup)
                .put("retrieve?type=REQUESTS")
                .then()
                .extract()
                .body();
    }

    // private sub-methods

    private int await() {
        if (specificAwaitMs.get() != 0) {
            int result = specificAwaitMs.get();
            specificAwaitMs.remove();
            return result;
        } else {
            return awaitMs;
        }
    }

}
