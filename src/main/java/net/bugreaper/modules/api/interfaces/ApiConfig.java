package net.bugreaper.modules.api.interfaces;

import net.bugreaper.modules.api.Api;
import io.restassured.http.ContentType;

import java.util.Map;

public interface ApiConfig {

    /**
     * Set content type XML
     *
     * @return this instance for method chaining
     */
    Api withContentTypeXml();

    /**
     * Set content type JSON
     *
     * @return this instance for method chaining
     */
    Api withContentTypeJson();


    /**
     * Set content type manually {@link ContentType}
     *
     * @param contentType {@link ContentType}:
     * <ul>
     *   <li>{@link ContentType#ANY}</li>
     *   <li>{@link ContentType#TEXT}</li>
     *   <li>{@link ContentType#JSON}</li>
     *   <li>{@link ContentType#XML}</li>
     *   <li>{@link ContentType#HTML}</li>
     *   <li>{@link ContentType#URLENC}</li>
     *   <li>{@link ContentType#BINARY}</li>
     *   <li>{@link ContentType#MULTIPART}</li>
     * </ul>
     * @return this instance for method chaining
     */
    Api withContentType(ContentType contentType);

    /**
     * Set maxResponseTimeout (does not break the connection - only assert)
     *
     * @param maxResponseMs assert ms for max response time
     * @return this instance for method chaining
     * @throws AssertionError if response time greater
     */
    Api withMaxResponseMsAssert(int maxResponseMs);

    /**
     * Set content type absent
     *
     * @return this instance for method chaining
     */
    Api withoutContentType();

    /**
     * Enables or disables logging manually (debug log level will print logs anyway!)
     *
     * @param enable true=request/response logging (Allure on always!)
     * @return this instance for method chaining
     */
    Api withLogging(boolean enable);

    /**
     * Adds or updates a header
     *
     * @param key header key
     * @param value header value
     * @return this instance for method chaining
     */
    Api setHeader(String key, String value);

    /**
     * Adds or updates a headers
     *
     * @param headers Map (key, value)
     * @return this instance for method chaining
     */
    Api setHeaders(Map<String, String> headers);


    /**
     * Adds or updates a query parameters
     *
     * @param queryParams Map (key, value)
     * @return this instance for method chaining
     */
    Api setQueryParams(Map<String, String> queryParams);

    /**
     * Adds or updates a query parameters
     *
     * @param token String with token
     * @return this instance for method chaining
     */
    Api setBearerAuth(String token);

    /**
     * Sets Basic authentication
     *
     * @param username String with username
     * @param password String with password
     * @return this instance for method chaining
     */
    Api setBasicAuth(String username, String password);


    Api setNoAuth();
}
