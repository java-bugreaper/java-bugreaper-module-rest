package net.bugreaper.modules.api.interfaces;

import net.bugreaper.modules.api.assertable.AssertableResponse;

/**
 * Interface defines methods for facilitating helper interactions and assertions.
 * Validates that all required methods are implemented.
 */
public interface ApiInt {

    /**
     * Send GET request
     * 
     * @param path String with path (/api/user1)
     * @return {@link AssertableResponse}
     */
    AssertableResponse sendGet(String path);

    /**
     * Send HEAD request
     *
     * @param path String with path (/api/user1)
     * @return {@link AssertableResponse}
     */
    AssertableResponse sendHead(String path);

    /**
     * Send OPTIONS request
     *
     * @param path String with path (/api/user1)
     * @return {@link AssertableResponse}
     */
    AssertableResponse sendOptions(String path);

    /**
     * Send POST request with body
     *
     * @param path String with path (/api/user1)
     * @param body String with body
     * @return {@link AssertableResponse}
     */
    AssertableResponse sendPost(String path, Object body);

    /**
     * Send POST request without body
     *
     * @param path String with path (/api/user1)
     * @return {@link AssertableResponse}
     */
    AssertableResponse sendPost(String path);

    /**
     * Send PUT request with body
     *
     * @param path String with path (/api/user1)
     * @param body String with body
     * @return {@link AssertableResponse}
     */
    AssertableResponse sendPut(String path, Object body);

    /**
     * Send PUT request without body
     *
     * @param path String with path (/api/user1)
     * @return {@link AssertableResponse}
     */
    AssertableResponse sendPut(String path);

    /**
     * Send PATCH request with body
     *
     * @param path String with path (/api/user1)
     * @param body String with body
     * @return {@link AssertableResponse}
     */
    AssertableResponse sendPatch(String path, Object body);

    /**
     * Send DELETE request with body
     *
     * @param path String with path (/api/user1)
     * @param body String with body
     * @return {@link AssertableResponse}
     */
    AssertableResponse sendDelete(String path, Object body);

    /**
     * Send DELETE request without body
     *
     * @param path String with path (/api/user1)
     * @return {@link AssertableResponse}
     */
    AssertableResponse sendDelete(String path);
}
