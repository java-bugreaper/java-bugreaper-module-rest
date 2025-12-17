package net.bugreaper.modules.api.interfaces;

import net.bugreaper.modules.api.assertable.AssertableResponse;

public interface ApiInt {

    /**
     * Send GET request
     * 
     * @param endpoint String with endpoint (/api/user1)
     * @return {@link AssertableResponse}
     */
    AssertableResponse sendGet(String endpoint);

    /**
     * Send HEAD request
     *
     * @param endpoint String with endpoint (/api/user1)
     * @return {@link AssertableResponse}
     */
    AssertableResponse sendHead(String endpoint);

    /**
     * Send OPTIONS request
     *
     * @param endpoint String with endpoint (/api/user1)
     * @return {@link AssertableResponse}
     */
    AssertableResponse sendOptions(String endpoint);

    /**
     * Send POST request with body
     *
     * @param endpoint String with endpoint (/api/user1)
     * @param body String with body
     * @return {@link AssertableResponse}
     */
    AssertableResponse sendPost(String endpoint, Object body);

    /**
     * Send POST request without body
     *
     * @param endpoint String with endpoint (/api/user1)
     * @return {@link AssertableResponse}
     */
    AssertableResponse sendPost(String endpoint);

    /**
     * Send PUT request with body
     *
     * @param endpoint String with endpoint (/api/user1)
     * @param body String with body
     * @return {@link AssertableResponse}
     */
    AssertableResponse sendPut(String endpoint, Object body);

    /**
     * Send PUT request without body
     *
     * @param endpoint String with endpoint (/api/user1)
     * @return {@link AssertableResponse}
     */
    AssertableResponse sendPut(String endpoint);

    /**
     * Send PATCH request with body
     *
     * @param endpoint String with endpoint (/api/user1)
     * @param body String with body
     * @return {@link AssertableResponse}
     */
    AssertableResponse sendPatch(String endpoint, Object body);

    /**
     * Send DELETE request with body
     *
     * @param endpoint String with endpoint (/api/user1)
     * @param body String with body
     * @return {@link AssertableResponse}
     */
    AssertableResponse sendDelete(String endpoint, Object body);

    /**
     * Send DELETE request without body
     *
     * @param endpoint String with endpoint (/api/user1)
     * @return {@link AssertableResponse}
     */
    AssertableResponse sendDelete(String endpoint);
}
