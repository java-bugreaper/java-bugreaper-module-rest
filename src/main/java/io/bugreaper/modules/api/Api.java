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
    public Api withMaxResponseMsAssert(long maxResponseMs) {
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

    public Api setBasicAuth(String username, String password) {
        this.username = username;
        this.password = password;
        this.useBasicAuth = true;
        return this;
    }

    public Api withLogging(boolean enable) {
        this.enableLogging = enable;
        return this;
    }

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
