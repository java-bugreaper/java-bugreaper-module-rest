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
package io.bugreaper.modules.api;

import io.bugreaper.core.config.ConfigLoader;
import io.bugreaper.core.config.YamlUtils;
import io.bugreaper.modules.api.assertable.AssertableResponse;
import io.bugreaper.modules.api.interfaces.ApiConfig;
import io.bugreaper.modules.api.interfaces.ApiInt;
import io.bugreaper.modules.api.setup.ApiAbstract;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;

import java.util.Map;

public class Api extends ApiAbstract implements ApiInt, ApiConfig {

    /**
     * This constructor initializes client for interaction with API
     *
     * @param url  service url ("http://my-service")
     * @param port service port
     */
    public Api(String url, int port) {
        super(url, port);
    }

    /**
     * Constructs api client configuration.
     *
     * <p>Loads configuration values from a YAML file.</p>
     *
     * <p><b>Default file:</b> {@code bugreaper.yml}</p>
     * <p><b>Custom file:</b> using {@code -DbugreaperEnv=test} loads {@code bugreaper-test.yml}</p>
     *
     * <p><b>Required configuration keys:</b></p>
     * <ul>
     *     <li>{@code modules.api.url}</li>
     *     <li>{@code modules.api.port}</li>
     * </ul>
     *
     * <p><b>Optional configuration keys:</b></p>
     * <ul>
     *     <li>{@code modules.api.username}</li>
     *     <li>{@code modules.api.password}</li>
     *     <li>{@code modules.api.token}</li>
     *     <li>{@code modules.api.logging}</li>
     *     <li>{@code modules.api.max-response-ms-assert}</li>
     * </ul>
     *
     * <p>Missing required keys will result in configuration errors.
     * Missing optional keys will fall back to predefined defaults.</p>
     */
    public Api() {
        loadFromYaml();
    }


    private void loadFromYaml() {
        Map<String, Object> rawData = ConfigLoader.loadYaml();

        //required config fields
        this.url = YamlUtils.getStringValueByPath(rawData, "modules.api.url");
        this.port = YamlUtils.getIntegerValueByPath(rawData, "modules.api.port");


        //optional config fields
        Object usernameVal = YamlUtils.getValueByPath(rawData, "modules.api.username", true);
        Object passwordVal = YamlUtils.getValueByPath(rawData, "modules.api.password", true);
        if (usernameVal instanceof String stringUser && passwordVal instanceof String stringPass) {
            setBasicAuth(stringUser, stringPass);
        }

        Object tokenVal = YamlUtils.getValueByPath(rawData, "modules.api.token", true);
        if (tokenVal instanceof String token) {
            setBearerAuth(token);
        }

        Object loggingVal = YamlUtils.getValueByPath(rawData, "modules.api.logging", true);
        if (loggingVal instanceof Boolean logging) {
            withLogging(logging);
        }

        Object maxResponseMsAssertVal = YamlUtils.getValueByPath(rawData, "modules.api.max-response-ms-assert", true);
        if (maxResponseMsAssertVal instanceof Integer assertMs) {
            withMaxResponseMsAssert(assertMs);
        }
    }

    //setters

    @Override
    public Api withContentTypeXml() {
        this.contentType = ContentType.XML;
        return this;
    }

    @Override
    public Api withContentType(ContentType contentType) {
        this.contentType = contentType;
        return this;
    }

    @Override
    public Api withMaxResponseMsAssert(int maxResponseMs) {
        this.maxResponseMsAssert = maxResponseMs;
        return this;
    }

    @Override
    public Api withoutContentType() {
        this.contentType = null;
        return this;
    }

    @Override
    public Api setHeader(String key, String value) {
        this.headers.put(key, value);
        return this;
    }

    @Override
    public Api setHeaders(Map<String, String> headers) {
        this.headers.putAll(headers);
        return this;
    }

    @Override
    public Api setQueryParams(Map<String, String> queryParams) {
        this.queryParams.putAll(queryParams);
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
    public Api withLogging(boolean enable) {
        this.enableLogging = enable;
        return this;
    }

    //getters

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
            maxResponseMsAssert=%s%n""",
                this.getClass().getSimpleName(),
                url, port, username, password, useBasicAuth, authToken,
                contentType, enableLogging, maxResponseMsAssert);

        logger.info(info);
        return info;
    }

    // interactions

    @Override
    @Step("(API) Send GET {endpoint}")
    public AssertableResponse sendGet(String endpoint) {
        return new AssertableResponse(buildRequest()
                .when()
                .get(endpoint));
    }

    @Override
    @Step("(API) Send HEAD {endpoint}")
    public AssertableResponse sendHead(String endpoint) {
        return new AssertableResponse(buildRequest()
                .when()
                .head(endpoint));
    }

    @Override
    @Step("(API) Send OPTIONS {endpoint}")
    public AssertableResponse sendOptions(String endpoint) {
        return new AssertableResponse(buildRequest()
                .when()
                .options(endpoint));
    }

    @Override
    @Step("(API) Send POST {endpoint}")
    public AssertableResponse sendPost(String endpoint, Object body) {
        return new AssertableResponse(buildRequest()
                .body(body)
                .when()
                .post(endpoint));
    }

    @Override
    @Step("(API) Send POST {endpoint}")
    public AssertableResponse sendPost(String endpoint) {
        return new AssertableResponse(buildRequest()
                .when()
                .post(endpoint));
    }

    @Override
    @Step("(API) Send PUT {endpoint}")
    public AssertableResponse sendPut(String endpoint, Object body) {
        return new AssertableResponse(buildRequest()
                .body(body)
                .when()
                .put(endpoint));
    }

    @Override
    @Step("(API) Send PUT {endpoint}")
    public AssertableResponse sendPut(String endpoint) {
        return new AssertableResponse(buildRequest()
                .when()
                .put(endpoint));
    }

    @Override
    @Step("(API) Send PATCH {endpoint}")
    public AssertableResponse sendPatch(String endpoint, Object body) {
        return new AssertableResponse(buildRequest()
                .body(body)
                .when()
                .patch(endpoint));
    }

    @Override
    @Step("(API) Send DELETE {endpoint}")
    public AssertableResponse sendDelete(String endpoint, Object body) {
        return new AssertableResponse(buildRequest()
                .body(body)
                .when()
                .delete(endpoint));
    }

    @Override
    @Step("(API) Send DELETE {endpoint}")
    public AssertableResponse sendDelete(String endpoint) {
        return new AssertableResponse(buildRequest()
                .when()
                .delete(endpoint));
    }

}
