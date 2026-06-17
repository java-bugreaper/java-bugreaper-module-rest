package net.bugreaper.modules.mocks.interfaces;


import org.opentest4j.AssertionFailedError;

/**
 * Interface defines methods for facilitating helper interactions and assertions.
 * Validates that all required methods are implemented.
 */
public interface MocksInt {

    /**
     * Reset mock server
     *
     * <p>Clean all: logs, requests, expectations
     */
    void resetMocks();

    /**
     * Clean mock-server logs (requests/responses)
     */
    void cleanMockLogs();

    /**
     * Add attachment with mock server logs (requests/responses)
     * <p>On DEBUG level also print to console
     */
    void showMockLogs();

    /**
     * Add attachment with requests to mock-server
     */
    void showMockRequests();

    /**
     * Add attachment with mock requests list
     * (only method and path)
     */
    void showBaseRequestsList();

    /**
     * Create mock expectation
     *
     * @param mockExpectation - String with mock expectation Json
     */
    void createMock(String mockExpectation);

    /**
     * Create mock expectation
     *
     * @param description - description part for step
     * @param path - path to file in resources
     * @throws IllegalArgumentException on wrong JSON type
     */
    void createMockCustom(String description, String path);

    /**
     * Verify requests to mock-server by verify setup
     *
     * @param verifySetup - String with mock verify Json
     * @throws AssertionFailedError an assert fail
     * @throws IllegalArgumentException on wrong JSON type
     */
    void verifyMock(String verifySetup);

    /**
     * Verify requests to mock-server by setup from file
     *
     * @param description - description part for step
     * @param path - path to file in resources
     *
     * @throws AssertionFailedError an assert fail
     * @throws IllegalArgumentException on wrong JSON type
     */
    void verifyMockCustom(String description, String path);

    /**
     * Verify sequence of requests to mock-server by verify setup
     *
     * @param verifySetup - String with mock verify Json
     *
     * @throws AssertionFailedError an assert fail
     * @throws IllegalArgumentException on wrong JSON type
     */
    void verifyMockSequence(String verifySetup);

    /**
     * Verify requests to mock-server by verify setup
     * <p><b>with await</b>
     *
     * @param verifySetup - String with mock verify Json
     *
     * @throws AssertionFailedError an assert fail
     * @throws IllegalArgumentException on wrong JSON type
     */
    void verifyMockWithAwait(String verifySetup);

    /**
     * Verify requests to mock-server by setup from file
     * <p><b>with await</b>
     *
     * @param description - description part for step
     * @param path - path to file in resources
     *
     * @throws AssertionFailedError an assert fail
     * @throws IllegalArgumentException on wrong JSON type
     */
    void verifyMockCustomWithAwait(String description, String path);

    /**
     * Verify sequence of requests to mock server by verify setup
     * <p><b>with await</b>
     *
     * @param verifySetup - String with mock verify Json
     *
     * @throws AssertionFailedError an assert fail
     * @throws IllegalArgumentException on wrong JSON type
     */
    void verifyMockSequenceWithAwait(String verifySetup);

    /**
     * Assert of all requests to mock-server count from to
     * <p><b>with await</b>
     *
     * @param from int minimum expected requests
     * @param to int maximum expected requests
     *
     * @throws AssertionFailedError an assert fail
     * @throws IllegalArgumentException on wrong JSON type
     */
    void assertMocksCountWithAwait(int from, int to);

    /**
     * Assert of all requests to mock-server count
     *
     * @param receivedCount - expected EXACTLY count of requests
     *
     * @throws AssertionFailedError an assert fail
     * @throws IllegalArgumentException on wrong JSON type
     */
    void assertAllMocksCount(int receivedCount);

    /**
     * Assert of all requests to mock-server count from to
     *
     * @param from int expected from
     * @param to int expected to
     *
     * @throws AssertionFailedError an assert fail
     * @throws IllegalArgumentException on wrong JSON type
     */
    void assertAllMocksCount(int from, int to);

    /**
     * Return value from request body
     * <p> Can be used to get some info (id, hash, base64...) for next check
     *
     * <pre>{@code
     * var value = mocksApi.getRequestBodyValue(
     *      """
     *      {
     *           "method": "POST",
     *           "path": "/api/user"
     *      }""",
     *      "[0].body.name");
     * }</pre>
     * @param mockSetup   - setup for request
     * @param extractPath - json path in body (example: "[0].body.parameters.name")
     * @return String (other types converted to String)
     */
    String getRequestBodyValue(String mockSetup, String extractPath);

}
