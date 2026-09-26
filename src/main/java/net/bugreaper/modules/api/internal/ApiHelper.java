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
package net.bugreaper.modules.api.internal;

import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import net.bugreaper.core.config.ConfigMap;
import net.bugreaper.modules.api.allurereporter.AllureRestAssuredCustom;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.Filter;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import io.restassured.specification.ResponseSpecification;
import org.slf4j.Logger;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import static org.hamcrest.Matchers.lessThan;


public class ApiHelper {

    protected Logger logger;

    protected String url;
    protected int port;

    protected volatile boolean useBasicAuth = false;
    protected volatile String authToken;

    protected volatile String username;
    protected volatile String password;

    private final AtomicReference<RestAssuredConfig> config = new AtomicReference<>();

    protected volatile boolean enableLogging = false;

    protected final Map<String, Object> headers = new ConcurrentHashMap<>();

    protected final Map<String, Object> queryParams = new ConcurrentHashMap<>();


    protected ThreadLocal<LinkedHashMap<String, Object>> requestQueryParams = ThreadLocal.withInitial(LinkedHashMap::new);
    protected ThreadLocal<LinkedHashMap<String, Object>> requestHeaders = ThreadLocal.withInitial(LinkedHashMap::new);

    /**
     * Default response timeout (not brake request, only assert)
     */
    protected volatile long maxResponseMsAssert = 0;

    /**
     * Default connection and socket timeout for http connection
     */
    protected volatile int maxTimeoutMs = 5000;

    /**
     * Default content type JSON
     */
    protected volatile ContentType contentType = ContentType.JSON;

    // universal constructor
    public ApiHelper(String url, int port, ConfigMap options, Logger logger) {
        this.url = url;
        this.port = port;
        this.logger = logger;
        config.set(RestAssuredConfig.config());
        setParams(options);
    }

    // for mock module & emailMailpit
    public ApiHelper(String url, int port, Logger logger) {
        this(url, port, null, logger);
    }


    private void setParams(ConfigMap options) {
        // Apply defaults
        setBaseParams();

        // YAML overrides defaults.
        if (options != null && !options.isEmpty()) {
            setProvidedParams(options);
        }
    }

    private void setBaseParams() {
        config.updateAndGet(currentConfig -> {
            HttpClientConfig httpClientConfig = currentConfig.getHttpClientConfig()
                    .setParam("http.connection.timeout", maxTimeoutMs)
                    .setParam("http.socket.timeout", maxTimeoutMs);

            return currentConfig.httpClient(httpClientConfig);
        });
    }

    private void setProvidedParams(ConfigMap options) {
        config.updateAndGet(currentConfig -> {
            HttpClientConfig httpClientConfig = currentConfig.getHttpClientConfig();

            for (String key : options.copyKeySet()) {
                httpClientConfig = httpClientConfig.setParam(
                        key,
                        options.get(key)
                );
            }

            return currentConfig.httpClient(httpClientConfig);
        });
    }

    /**
     * Returns the configured HTTP client parameters and logs them.
     *
     * @return HTTP client parameters formatted as {@code key=value} pairs,
     *         separated by the system line separator
     */
    public String getHttpClientParams() {
        String params = config.get()
                .getHttpClientConfig()
                .params()
                .entrySet()
                .stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining(System.lineSeparator()));

        logger.info(params);
        return params;
    }

    // setters

    public ApiHelper setLogging(boolean enable) {
        enableLogging = enable;
        return this;
    }

    public ApiHelper setContentType(ContentType contentType) {
        this.contentType = contentType;
        return this;
    }

    public ApiHelper setMaxResponseMsAssert(int maxResponseMs) {
        this.maxResponseMsAssert = maxResponseMs;
        return this;
    }

    public ApiHelper setTimeoutMs(int maxTimeoutMs) {
        this.maxTimeoutMs = maxTimeoutMs;
        //rebuild config params
        setBaseParams();
        return this;
    }

    // Auth

    public ApiHelper setBearerAuth(String token) {
        this.authToken = token;
        this.useBasicAuth = false;
        return this;
    }

    public ApiHelper setBasicAuth(String username, String password) {
        this.username = username;
        this.password = password;
        this.useBasicAuth = true;
        this.authToken = null;
        return this;
    }

    public ApiHelper setNoAuth() {
        this.authToken = null;
        this.username = null;
        this.password = null;
        this.useBasicAuth = false;
        return this;
    }

    //headers

    public ApiHelper setHeaders(Map<String, Object> headers) {
        synchronized (this.headers) {
            this.headers.clear();
            this.headers.putAll(headers);
        }
        return this;
    }

    public ApiHelper setHeader(String key, Object value) {
        this.headers.put(key, value);
        return this;
    }

    public ApiHelper cleanSetHeaders() {
        this.headers.clear();
        return this;
    }

    public ApiHelper withHeader(String key, Object value) {
        this.requestHeaders.get().put(key, value);
        return this;
    }

    public ApiHelper withHeaders(Map<String, Object> headers) {
        this.requestHeaders.get().putAll(headers);
        return this;
    }

    //queryParams

    public ApiHelper setQueryParams(Map<String, Object> queryParams) {
        synchronized (this.queryParams) {
            this.queryParams.clear();
            this.queryParams.putAll(queryParams);
        }
        return this;
    }

    public ApiHelper cleanSetQueryParams() {
        this.queryParams.clear();
        return this;
    }

    public ApiHelper withQueryParam(String key, Object value) {
        this.requestQueryParams.get().put(key, value);
        return this;
    }

    public ApiHelper withQueryParams(Map<String, Object> queryParams) {
        this.requestQueryParams.get().putAll(queryParams);
        return this;
    }

    // getters

    public Boolean getLogging() {
        return enableLogging;
    }

    public String getConfigSummary(Class<?> clazz) {

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
                clazz.getSimpleName(),
                url, port, username, password, useBasicAuth, authToken,
                contentType, enableLogging, maxResponseMsAssert, maxTimeoutMs);

        logger.info(info);
        return info;
    }

    public RequestSpecification buildRequest() {

        RequestSpecification request = RestAssured.given().config(config.get());

        request.baseUri(url).port(port);

        // Use set or request query params
        Map<String, Object> requestParams = requestQueryParams.get();
        if (!requestParams.isEmpty()) {
            request.queryParams(requestParams);
            requestQueryParams.remove();
        } else {
            synchronized (queryParams) {
                if (!queryParams.isEmpty()) {
                    request.queryParams(queryParams);
                }
            }
        }

        if (contentType != null) {
            request.contentType(contentType);
            request.accept(contentType);
        }

        // Use set or request headers
        Map<String, Object> specificHeaders = requestHeaders.get();
        if (!specificHeaders.isEmpty()) {
            request.headers(specificHeaders);
            requestHeaders.remove();
        } else {
            synchronized (headers) {
                if (!headers.isEmpty()) {
                    request.headers(new LinkedHashMap<>(headers));
                }
            }
        }

        // Apply authentication
        if (useBasicAuth && username != null && password != null) {
            request.auth().preemptive().basic(username, password);
        } else if (authToken != null) {
            request.header("Authorization", "Bearer " + authToken);
        }

        request.filters(apiFilters());

        //response time pre-check
        if (maxResponseMsAssert > 0) {
            request.then()
                    .spec(setMaxResponseTimeAssert());
        }

        return request;
    }

    //use for await asserts (where no need to attach filters on every attempt)
    public RequestSpecification buildSimpleRequest() {

        RequestSpecification request = RestAssured.given();

        request.baseUri(url).port(port);

        request.filters(List.of());

        return request;
    }

    private ResponseSpecification setMaxResponseTimeAssert() {
        return new ResponseSpecBuilder()
                .expectResponseTime(lessThan(maxResponseMsAssert), MILLISECONDS)
                .build();
    }


    private List<Filter> apiFilters() {
        if (logger.isDebugEnabled() || enableLogging) {
            return Arrays.asList(new RequestLoggingFilter(), new ResponseLoggingFilter(), new AllureRestAssuredCustom());
        }
        return Collections.singletonList(new AllureRestAssuredCustom());
    }


}
