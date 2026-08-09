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
import net.bugreaper.modules.api.setup.ApiAbstract;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;

import java.util.Map;

/**
 * API helper that provides a common API for operating with HTTP requests using RestAssured.
 *
 * <p>It is recommended to use a single instance:
 * {@code Api api = Api.getInstance();}
 * </p>
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
public class Api extends ApiAbstract implements ApiInt, ApiConfig {

    private static Api instance;

    private static final String YML_KEY = "modules.api";

    /**
     * Creates an API client with the specified service connection settings.
     *
     * @param url  service url (for example: {@code "http://my-service"})
     * @param port service port
     */
    public Api(String url, int port) {
        super(url, port);
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
        loadFromYaml("");
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
        if (suffix == null || suffix.isBlank()) {
            throw new IllegalArgumentException("suffix can`t be empty or null");
        }
        loadFromYaml(suffix);
    }


    private void loadFromYaml(String num) {

        //required config fields
        this.url = YamlUtils.getStringValueByPath(YML_KEY + num + ".url");
        this.port = YamlUtils.getIntegerValueByPath(YML_KEY + num + ".port");


        //optional config fields
        Object usernameVal = YamlUtils.getValueByPath(YML_KEY + num + ".username", true);
        Object passwordVal = YamlUtils.getValueByPath(YML_KEY + num + ".password", true);
        if (usernameVal instanceof String stringUser && passwordVal instanceof String stringPass) {
            setBasicAuth(stringUser, stringPass);
        }

        Object tokenVal = YamlUtils.getValueByPath(YML_KEY + num + ".token", true);
        if (tokenVal instanceof String token) {
            setBearerAuth(token);
        }

        Object loggingVal = YamlUtils.getValueByPath(YML_KEY + num + ".logging", true);
        if (loggingVal instanceof Boolean logging) {
            setLogging(logging);
        }

        Object maxResponseMsAssertVal = YamlUtils.getValueByPath(YML_KEY + num + ".max-response-ms-assert", true);
        if (maxResponseMsAssertVal instanceof Integer assertMs) {
            setMaxResponseMsAssert(assertMs);
        }

        Object maxTimeoutMsVal = YamlUtils.getValueByPath(YML_KEY + num + ".max-timeout-ms", true);
        if (maxTimeoutMsVal instanceof Integer timeoutMs) {
            setTimeoutMs(timeoutMs);
        }
    }

    //setters

    @Override
    public Api setContentTypeJson() {
        this.contentType = ContentType.JSON;
        return this;
    }

    @Override
    public Api setContentTypeXml() {
        this.contentType = ContentType.XML;
        return this;
    }

    @Override
    public Api setContentType(ContentType contentType) {
        this.contentType = contentType;
        return this;
    }

    @Override
    public Api setMaxResponseMsAssert(int maxResponseMs) {
        this.maxResponseMsAssert = maxResponseMs;
        return this;
    }

    @Override
    public Api setTimeoutMs(int maxTimeoutMs) {
        this.maxTimeoutMs = maxTimeoutMs;
        return this;
    }


    @Override
    public Api setNoContentType() {
        this.contentType = null;
        return this;
    }

    @Override
    public Api setHeader(String key, Object value) {
        this.headers.put(key, value);
        return this;
    }


    @Override
    public Api setHeaders(Map<String, Object> headers) {
        synchronized (this.headers) {
            this.headers.clear();
            this.headers.putAll(headers);
        }
        return this;
    }

    @Override
    public Api cleanSetHeaders() {
        this.headers.clear();
        return this;
    }

    @Override
    public Api withHeader(String key, Object value) {
        this.requestHeaders.get().put(key, value);
        return this;
    }

    @Override
    public Api withHeaders(Map<String, Object> queryParams) {
        this.requestHeaders.get().putAll(queryParams);
        return this;
    }

    @Override
    public Api setQueryParams(Map<String, Object> queryParams) {
        synchronized (this.queryParams) {
            this.queryParams.clear();
            this.queryParams.putAll(queryParams);
        }
        return this;
    }

    @Override
    public Api cleanSetQueryParams() {
        this.queryParams.clear();
        return this;
    }

    @Override
    public Api withQueryParam(String key, Object value) {
        this.requestQueryParams.get().put(key, value);
        return this;
    }

    @Override
    public Api withQueryParams(Map<String, Object> queryParams) {
        this.requestQueryParams.get().putAll(queryParams);
        return this;
    }

    @Override
    public Api setBearerAuth(String token) {
        this.authToken = token;
        this.useBasicAuth = false;
        return this;
    }

    @Override
    public Api setBasicAuth(String username, String password) {
        this.username = username;
        this.password = password;
        this.useBasicAuth = true;
        this.authToken = null;
        return this;
    }

    @Override
    public Api setNoAuth() {
        this.authToken = null;
        this.username = null;
        this.password = null;
        this.useBasicAuth = false;
        return this;
    }

    @Override
    public Api setLogging(boolean enable) {
        this.enableLogging = enable;
        return this;
    }

    //getters

    @Override
    public String getConfigSummary() {
        String info = String.format("""
        %s:
            url=%s
            port=%d
            username=%s
            password=%s
            useBasicAuth=%b
            authToken=%s
            contentType=%s
            enableLogging=%b
            maxResponseMsAssert=%d
            maxTimeoutMs=%d%n""",
                this.getClass().getSimpleName(),
                url, port, username, password, useBasicAuth, authToken,
                contentType, enableLogging, maxResponseMsAssert, maxTimeoutMs);

        logger.info(info);
        return info;
    }

    // interactions

    @Override
    @Step("(API) Send GET {path}")
    public AssertableResponse sendGet(String path) {
        return new AssertableResponse(buildRequest()
                .when()
                .get(path));
    }

    @Override
    @Step("(API) Send HEAD {path}")
    public AssertableResponse sendHead(String path) {
        return new AssertableResponse(buildRequest()
                .when()
                .head(path));
    }

    @Override
    @Step("(API) Send OPTIONS {path}")
    public AssertableResponse sendOptions(String path) {
        return new AssertableResponse(buildRequest()
                .when()
                .options(path));
    }

    @Override
    @Step("(API) Send POST {path}")
    public AssertableResponse sendPost(String path, Object body) {
        return new AssertableResponse(buildRequest()
                .body(body)
                .when()
                .post(path));
    }

    @Override
    @Step("(API) Send POST {path}")
    public AssertableResponse sendPost(String path) {
        return new AssertableResponse(buildRequest()
                .when()
                .post(path));
    }

    @Override
    @Step("(API) Send PUT {path}")
    public AssertableResponse sendPut(String path, Object body) {
        return new AssertableResponse(buildRequest()
                .body(body)
                .when()
                .put(path));
    }

    @Override
    @Step("(API) Send PUT {path}")
    public AssertableResponse sendPut(String path) {
        return new AssertableResponse(buildRequest()
                .when()
                .put(path));
    }

    @Override
    @Step("(API) Send PATCH {path}")
    public AssertableResponse sendPatch(String path, Object body) {
        return new AssertableResponse(buildRequest()
                .body(body)
                .when()
                .patch(path));
    }

    @Override
    @Step("(API) Send DELETE {path}")
    public AssertableResponse sendDelete(String path, Object body) {
        return new AssertableResponse(buildRequest()
                .body(body)
                .when()
                .delete(path));
    }

    @Override
    @Step("(API) Send DELETE {path}")
    public AssertableResponse sendDelete(String path) {
        return new AssertableResponse(buildRequest()
                .when()
                .delete(path));
    }

}
