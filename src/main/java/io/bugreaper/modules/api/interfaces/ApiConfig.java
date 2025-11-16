package io.bugreaper.modules.api.interfaces;

import io.bugreaper.modules.api.Api;
import io.restassured.http.ContentType;

import java.util.Map;

public interface ApiConfig {

    /**
     * Set content type XML
     *
     * @return this instance {@link Api}
     */
    Api withContentTypeXml();

    /**
     * Set content type manually {@link ContentType}
     *
     * @param contentType {@link ContentType}
     * @return this instance {@link Api}
     */
    Api withContentType(ContentType contentType);

    /**
     * Set maxResponseTimeout (does not break the connection - only assert)
     *
     * @param maxResponseMs assert ms for max response time
     * @return this instance {@link Api}
     * @throws AssertionError if response time greater
     */
    Api withMaxResponseMsAssert(long maxResponseMs);

    /**
     * Set content type absent
     *
     * @return this
     */
    Api withoutContentType();

    /**
     * Enables or disables logging manually (debug log level will print logs anyway!)
     *
     * @param enable true=request/response logging (Allure on always!)
     * @return this instance {@link Api}
     */
    Api withLogging(boolean enable);

    /**
     * Adds or updates a header
     *
     * @param key header key
     * @param value header value
     * @return this instance {@link Api}
     */
    Api setHeader(String key, String value);

    /**
     * Adds or updates a headers
     *
     * @param headers Map (key, value)
     * @return this instance {@link Api}
     */
    Api setHeaders(Map<String, String> headers);


    /**
     * Adds or updates a query parameters
     *
     * @param queryParams Map (key, value)
     * @return this instance {@link Api}
     */
    Api setQueryParams(Map<String, String> queryParams);

    /**
     * Adds or updates a query parameters
     *
     * @param token String with token
     * @return this instance {@link Api}
     */
    Api setBearerAuth(String token);

    /**
     * Sets Basic authentication
     *
     * @param username String with username
     * @param password String with password
     * @return this instance {@link Api}
     */
    Api setBasicAuth(String username, String password);


}
