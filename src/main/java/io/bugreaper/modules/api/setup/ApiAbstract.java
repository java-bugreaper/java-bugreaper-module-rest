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
package io.bugreaper.modules.api.setup;

import io.bugreaper.modules.api.allurereporter.AllureRestAssuredCustom;
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
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.lessThan;

public abstract class ApiAbstract {

    protected Logger logger = LoggerFactory.getLogger("bugreaper-module-api");

    private final String url;
    private final int port;

    protected Map<String, String> headers = new HashMap<>();
    protected Map<String, String> queryParams = new HashMap<>();

    protected boolean useBasicAuth = false;
    protected String authToken;

    protected String username;
    protected String password;

    protected ContentType contentType = ContentType.JSON;
    protected boolean enableLogging = false;
    //assert not break!!
    protected long maxResponseMsAssert = 5000;


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

        RequestSpecification request = RestAssured.given();

        request.baseUri(url).port(port);

        if (!queryParams.isEmpty()) {
            request.queryParams(queryParams);
        }

        if (contentType != null) {
            request.contentType(contentType);
            request.accept(contentType);
        }

        if (!headers.isEmpty()) {
            request.headers(headers);
        }

        // Apply authentication
        if (useBasicAuth && username != null && password != null) {
            request.auth().preemptive().basic(username, password);
        } else if (authToken != null) {
            request.header("Authorization", "Bearer " + authToken);
        }

        request.filters(apiFilters());
        request.then()
                .spec(setMaxResponseTimeAssert());

        return request;
    }

    private ResponseSpecification setMaxResponseTimeAssert() {
        return new ResponseSpecBuilder()
                .expectResponseTime(lessThan(maxResponseMsAssert), TimeUnit.MILLISECONDS)
                .build();
    }


    private List<Filter> apiFilters() {
        if (logger.isDebugEnabled() || enableLogging) {
            return Arrays.asList(new RequestLoggingFilter(), new ResponseLoggingFilter(), new AllureRestAssuredCustom());
        }
        return Collections.singletonList(new AllureRestAssuredCustom());
    }


}
