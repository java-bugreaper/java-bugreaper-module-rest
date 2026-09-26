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
package net.bugreaper.modules.api;

import net.bugreaper.core.config.YamlUtils;
import net.bugreaper.modules.api.assertable.AssertableResponse;
import net.bugreaper.modules.api.interfaces.ApiConfig;
import net.bugreaper.modules.api.interfaces.ApiInt;
import net.bugreaper.modules.api.internal.ApiHelper;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;

import java.util.Map;

import static net.bugreaper.core.config.YamlUtils.getConfigMapValueByPath;
import static net.bugreaper.modules.api.logger.Log.LOGGER;

/**
 * API helper that provides a common API for operating with HTTP requests using RestAssured.
 *
 * <p>It is recommended to use a single instance:
 * {@code Api api = Api.getInstance();}
 * </p>
 * <p>
 * Responses can be checked by {@link AssertableResponse}
 *
 * <p>This client provides a convenient fluent API for:</p>
 * <ul>
 *     <li>Sending GET, POST, PUT and DELETE requests</li>
 *     <li>Configuring global and per-request headers and query parameters</li>
 *     <li>Using Basic or Bearer authentication</li>
 *     <li>Enabling request/response logging</li>
 *     <li>Support BugReaper YML config</li>
 * </ul>
 *
 * @author Oleksii Betin "ambu550"
 * @since 1.0.0
 */
public class Api implements ApiInt, ApiConfig {

    private final ApiHelper apiApiHelper;

    private static Api instance;

    private static final String YML_PATH = "modules.api";


    /**
     * Creates an API client with the specified service connection settings.
     *
     * @param url  service url (for example: {@code "http://my-service"})
     * @param port service port
     */
    public Api(String url, int port) {
        apiApiHelper = new ApiHelper(url, port, LOGGER);
    }

    /**
     * Returns the instance of {@link Api} with config builder {@link #Api()}.
     * <p>
     * This implementation is thread-safe using method-level synchronization.
     *
     * @return the shared instance of {@link Api}
     * @see #Api() config setup
     */
    public static synchronized Api getInstance() {
        if (instance == null) {
            instance = new Api();
        }

        return instance;
    }

    /**
     * Constructs an Api client using YAML configuration.
     *
     * <p>Loads configuration values from a YAML file.</p>
     *
     * <p><b>Default file:</b> {@code bugreaper.yml}</p>
     * <p><b>Custom file:</b> using {@code -DbugreaperEnv=test} loads {@code bugreaper-test.yml}</p>
     *
     * <pre>
     * modules:
     *   api:
     *     url: http://localhost
     *     port: 1082
     *     username: user_1 # (optional)
     *     password: pass123 # (optional)
     *     token: my-token # (optional) more priority than base auth!
     *     logging: true # (optional)
     *     max-response-ms-assert: 2000 # (optional)
     *     max-timeout-ms: 1000 # (optional)
     * </pre>
     *
     * <p>Missing required keys will result in configuration errors.
     * Missing optional keys will fall back to predefined defaults.</p>
     *
     * @throws IllegalArgumentException if the configuration contains invalid values
     */
    public Api() {
        this("");
    }

    /**
     * Constructs an Api client using YAML configuration.
     *
     * @param suffix concatenation of Api client (example Api("-2") keys will be "modules.api<b>-2</b>")
     *
     * <p>Loads configuration values from a YAML file.</p>
     *
     * <p><b>Default file:</b> {@code bugreaper.yml}</p>
     * <p><b>Custom file:</b> using {@code -DbugreaperEnv=test} loads {@code bugreaper-test.yml}</p>
     *
     * <pre>
     * modules:
     *   api{suffix}: # example new Api("-2") key will be "api-2"
     *     url: http://localhost
     *     port: 8080
     *     username: user_2 # (optional)
     *     password: pass123 # (optional)
     *     token: my-token # (optional) more priority than base auth!
     *     logging: true # (optional)
     *     max-response-ms-assert: 2000 # (optional)
     *     max-timeout-ms: 1000 # (optional)
     * </pre>
     *
     * <p>Missing required keys will result in configuration errors.
     * Missing optional keys will fall back to predefined defaults.</p>
     */
    public Api(String suffix) {

        if (suffix == null) {
            throw new IllegalArgumentException("suffix can`t be null");
        }

        apiApiHelper = new ApiHelper(
                YamlUtils.getStringValueByPath(YML_PATH + suffix + ".url"),
                YamlUtils.getIntegerValueByPath(YML_PATH + suffix + ".port"),
                getConfigMapValueByPath(YML_PATH + suffix + ".options", true),
                LOGGER);


        loadYmlSetters(suffix);
    }


    private void loadYmlSetters(String suffix) {

        //optional config fields
        Object usernameVal = YamlUtils.getValueByPath(YML_PATH + suffix + ".username", true);
        Object passwordVal = YamlUtils.getValueByPath(YML_PATH + suffix + ".password", true);
        if (usernameVal instanceof String stringUser && passwordVal instanceof String stringPass) {
            setBasicAuth(stringUser, stringPass);
        }

        Object tokenVal = YamlUtils.getValueByPath(YML_PATH + suffix + ".token", true);
        if (tokenVal instanceof String token) {
            setBearerAuth(token);
        }

        Object loggingVal = YamlUtils.getValueByPath(YML_PATH + suffix + ".logging", true);
        if (loggingVal instanceof Boolean logging) {
            setLogging(logging);
        }

        Object maxResponseMsAssertVal = YamlUtils.getValueByPath(YML_PATH + suffix + ".max-response-ms-assert", true);
        if (maxResponseMsAssertVal instanceof Integer assertMs) {
            setMaxResponseMsAssert(assertMs);
        }

        Object maxTimeoutMsVal = YamlUtils.getValueByPath(YML_PATH + suffix + ".max-timeout-ms", true);
        if (maxTimeoutMsVal instanceof Integer timeoutMs) {
            setTimeoutMs(timeoutMs);
        }
    }

    //setters

    @Override
    public Api setContentTypeJson() {
        apiApiHelper.setContentType(ContentType.JSON);
        return this;
    }

    @Override
    public Api setContentTypeXml() {
        apiApiHelper.setContentType(ContentType.XML);
        return this;
    }

    @Override
    public Api setContentType(ContentType contentType) {
        apiApiHelper.setContentType(contentType);
        return this;
    }

    @Override
    public Api setNoContentType() {
        apiApiHelper.setContentType(null);
        return this;
    }

    @Override
    public Api setMaxResponseMsAssert(int maxResponseMs) {
        apiApiHelper.setMaxResponseMsAssert(maxResponseMs);
        return this;
    }

    @Override
    public Api setTimeoutMs(int maxTimeoutMs) {
        apiApiHelper.setTimeoutMs(maxTimeoutMs);
        return this;
    }

    @Override
    public Api setHeader(String key, Object value) {
        apiApiHelper.setHeader(key, value);
        return this;
    }

    @Override
    public Api setHeaders(Map<String, Object> headers) {
        apiApiHelper.setHeaders(headers);
        return this;
    }

    @Override
    public Api cleanSetHeaders() {
        apiApiHelper.cleanSetHeaders();
        return this;
    }

    @Override
    public Api withHeader(String key, Object value) {
        apiApiHelper.withHeader(key, value);
        return this;
    }

    @Override
    public Api withHeaders(Map<String, Object> headers) {
        apiApiHelper.withHeaders(headers);
        return this;
    }

    @Override
    public Api setQueryParams(Map<String, Object> queryParams) {
        apiApiHelper.setQueryParams(queryParams);
        return this;
    }

    @Override
    public Api cleanSetQueryParams() {
        apiApiHelper.cleanSetQueryParams();
        return this;
    }

    @Override
    public Api withQueryParam(String key, Object value) {
        apiApiHelper.withQueryParam(key, value);
        return this;
    }

    @Override
    public Api withQueryParams(Map<String, Object> queryParams) {
        apiApiHelper.withQueryParams(queryParams);
        return this;
    }

    @Override
    public Api setBearerAuth(String token) {
        apiApiHelper.setBearerAuth(token);
        return this;
    }

    @Override
    public Api setBasicAuth(String username, String password) {
        apiApiHelper.setBasicAuth(username, password);
        return this;
    }

    @Override
    public Api setNoAuth() {
        apiApiHelper.setNoAuth();
        return this;
    }

    @Override
    public Api setLogging(boolean enable) {
        apiApiHelper.setLogging(enable);
        return this;
    }

    //getters

    @Override
    public String getConfigSummary() {
        return apiApiHelper.getConfigSummary(this.getClass());
    }

    @Override
    public String getHttpClientParams() {
        return  apiApiHelper.getHttpClientParams();
    }

    // interactions

    @Override
    @Step("(API) Send GET {path}")
    public AssertableResponse sendGet(String path) {
        return new AssertableResponse(apiApiHelper.buildRequest()
                .when()
                .get(path));
    }

    @Override
    @Step("(API) Send HEAD {path}")
    public AssertableResponse sendHead(String path) {
        return new AssertableResponse(apiApiHelper.buildRequest()
                .when()
                .head(path));
    }

    @Override
    @Step("(API) Send OPTIONS {path}")
    public AssertableResponse sendOptions(String path) {
        return new AssertableResponse(apiApiHelper.buildRequest()
                .when()
                .options(path));
    }

    @Override
    @Step("(API) Send POST {path}")
    public AssertableResponse sendPost(String path, Object body) {
        return new AssertableResponse(apiApiHelper.buildRequest()
                .body(body)
                .when()
                .post(path));
    }

    @Override
    @Step("(API) Send POST {path}")
    public AssertableResponse sendPost(String path) {
        return new AssertableResponse(apiApiHelper.buildRequest()
                .when()
                .post(path));
    }

    @Override
    @Step("(API) Send PUT {path}")
    public AssertableResponse sendPut(String path, Object body) {
        return new AssertableResponse(apiApiHelper.buildRequest()
                .body(body)
                .when()
                .put(path));
    }

    @Override
    @Step("(API) Send PUT {path}")
    public AssertableResponse sendPut(String path) {
        return new AssertableResponse(apiApiHelper.buildRequest()
                .when()
                .put(path));
    }

    @Override
    @Step("(API) Send PATCH {path}")
    public AssertableResponse sendPatch(String path, Object body) {
        return new AssertableResponse(apiApiHelper.buildRequest()
                .body(body)
                .when()
                .patch(path));
    }

    @Override
    @Step("(API) Send DELETE {path}")
    public AssertableResponse sendDelete(String path, Object body) {
        return new AssertableResponse(apiApiHelper.buildRequest()
                .body(body)
                .when()
                .delete(path));
    }

    @Override
    @Step("(API) Send DELETE {path}")
    public AssertableResponse sendDelete(String path) {
        return new AssertableResponse(apiApiHelper.buildRequest()
                .when()
                .delete(path));
    }

}
