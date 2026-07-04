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
package net.bugreaper.modules.api.setup;

import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
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
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import static org.hamcrest.Matchers.lessThan;

public abstract class ApiAbstract {

    protected Logger logger = LoggerFactory.getLogger("bugreaper-module-api");

    protected String url;
    protected int port;

    protected volatile boolean useBasicAuth = false;
    protected volatile String authToken;

    protected volatile String username;
    protected volatile String password;

    /**
     * Default timeout for http connection
     */
    protected volatile int maxTimeoutMs = 5000;

    protected volatile boolean enableLogging = false;
    //assert not break!!
    protected volatile long maxResponseMsAssert = 0;

    protected final Map<String, Object> headers = new ConcurrentHashMap<>();

    protected final Map<String, Object> queryParams = new ConcurrentHashMap<>();


    protected ThreadLocal<LinkedHashMap<String,Object>> requestQueryParams = ThreadLocal.withInitial(LinkedHashMap::new);
    protected ThreadLocal<LinkedHashMap<String,Object>> requestHeaders = ThreadLocal.withInitial(LinkedHashMap::new);

    protected volatile ContentType contentType = ContentType.JSON;


    protected ApiAbstract() {
    }

    protected ApiAbstract(String url, int port) {
        this.url = url;
        this.port = port;
    }

    // for mock module
    protected ApiAbstract(String url, int port, Logger logger) {
        this.url = url;
        this.port = port;
        this.logger = logger;
    }


    protected RequestSpecification buildRequest() {

        RestAssuredConfig config = RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", maxTimeoutMs)
                        .setParam("http.socket.timeout", maxTimeoutMs)
                );

        RequestSpecification request = RestAssured.given().config(config);

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
        if (maxResponseMsAssert > 0 ) {
            request.then()
                    .spec(setMaxResponseTimeAssert());
        }

        return request;
    }

    //use for await asserts (where no need to attach filters on every attempt)
    protected RequestSpecification buildSimpleRequest() {

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
