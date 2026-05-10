package net.bugreaper.modules.api.interfaces;

import net.bugreaper.modules.api.Api;
import io.restassured.http.ContentType;

import java.util.Map;

/**
 * Interface that defines helper configuration methods for helper operations.
 * Validates that all required methods are implemented.
 */
public interface ApiConfig {

    /**
     * Set content type XML
     *
     * @return this instance for method chaining
     */
    Api setContentTypeXml();

    /**
     * Set content type JSON
     *
     * @return this instance for method chaining
     */
    Api setContentTypeJson();


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
    Api setContentType(ContentType contentType);

    /**
     * Set maxResponseTimeout (does not break the connection - only assert)
     *
     * @param maxResponseMs assert ms for max response time
     * @return this instance for method chaining
     * @throws AssertionError if response time greater
     */
    Api setMaxResponseMsAssert(int maxResponseMs);

    /**
     * Set max request timeout(connection/read)
     * <p> Drop connection on timeout is up
     *
     * @param maxTimeoutMs max ms for connection/read
     * @return this instance for method chaining
     */
    Api setTimeoutMs(int maxTimeoutMs);

    /**
     * Set content type absent
     *
     * @return this instance for method chaining
     */
    Api setNoContentType();

    /**
     * Enables or disables logging manually (debug log level will print logs anyway!)
     *
     * @param enable true=request/response logging (Allure on always!)
     * @return this instance for method chaining
     */
    Api setLogging(boolean enable);

    /**
     * Add or updates header globally (add to previously set)
     *
     * @param key header key
     * @param value header value
     * @return this instance for method chaining
     */
    Api setHeader(String key, Object value);

    /**
     * Set headers globally (overwrite previously set)
     *
     * @param headers Map (key, value)
     * @return this instance for method chaining
     */
    Api setHeaders(Map<String, Object> headers);

    /**
     * Remove global headers
     *
     * @return this instance for method chaining
     */
    Api cleanSetHeaders();

    /**
     * Set query parameters globally (overwrite previously set)
     *
     * @param queryParams Map (key, value)
     * @return this instance for method chaining
     */
    Api setQueryParams(Map<String, Object> queryParams);

    /**
     * Remove global query parameters
     *
     * @return this instance for method chaining
     */
    Api cleanSetQueryParams();

    /**
     * Sets Bearer authentication
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

    /**
     * Unset all authentications
     *
     * @return this instance for method chaining
     */
    Api setNoAuth();


    /**
     * Add header for one next request (global headers will be ignored)
     * <ul>
     *     <li>use for specific request</li>
     *     <li>can be used multiple times to add multiple headers</li>
     *     <li>after request global headers are taken into account again</li>
     * </ul>
     *
     * @param key header key
     * @param value header value
     * @return this instance for method chaining
     */
    Api withHeader(String key, Object value);

    /**
     * Add headers for one next request (global headers will be ignored)
     * <ul>
     *     <li>use for specific request </li>
     *     <li>after request global headers are taken into account again</li>
     * </ul>
     *
     * @param queryParams Map (key, value)
     * @return this instance for method chaining
     */
    Api withHeaders(Map<String, Object> queryParams);


    /**
     * Add query parameter once for next request (global query parameters will be ignored)
     * <ul>
     *     <li>use before specific request</li>
     *     <li>can be used multiple times to add multiple query parameters</li>
     *     <li>after request global query parameter are taken into account again</li>
     * </ul>
     * @param key parameter key
     * @param value parameter value
     * @return this instance for method chaining
     */
    Api withQueryParam(String key, Object value);

    /**
     * Add query parameters once for next request (global query parameters will be ignored)
     * <ul>
     *     <li>use before specific request</li>
     *     <li>after request global query parameter are taken into account again</li>
     * </ul>
     * @param queryParams Map (key, value)
     * @return this instance for method chaining
     */
    Api withQueryParams(Map<String, Object> queryParams);

    /**
     * Returns and logs (at INFO level) a human-readable summary of all resolved
     * configuration values.
     * <p>
     * The summary includes values loaded from the YAML configuration file as well as
     * any fields overridden programmatically after construction. Optional fields that
     * were not present in the configuration and resolved via default values may also
     * be included.
     *
     * @return String with summary
     */
    String getConfigSummary();

}
