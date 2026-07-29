package net.bugreaper.modules.mocks.interfaces;


/**
 * Interface defines methods for facilitating helper interactions and assertions.
 * Validates that all required methods are implemented.
 */
public interface MocksInt {

    /**
     * Reset mock-server.
     *
     * <p>Clean all: logs, requests, expectations
     */
    void resetMocks();

    /**
     * Clean mock-server logs (requests/responses)
     */
    void cleanMockLogs();

    /**
     * Adds an attachment with mock-server logs (requests/responses).
     *
     * <p>At DEBUG level, logs are also printed to the console.</p>
     */
    void showMockLogs();

    /**
     * Add attachment with requests to mock-server
     */
    void showMockRequests();

    /**
     * Adds an attachment with a list of mock-server requests
     * (only HTTP method and path).
     */
    void showBaseRequestsList();

    /**
     * Creates a mock-server expectation.
     *
     * @param mockExpectation JSON string containing the mock-server expectation definition
     */
    void createMock(String mockExpectation);

    /**
     * Creates a mock-server expectation loaded from a file.
     *
     * @param description description part for step
     * @param path        path to the resource file with mock-server expectation
     * @throws IllegalArgumentException if reading the file fails or the provided data is not valid JSON
     */
    void createMockCustom(String description, String path);

    /**
     * Verifies mock-server requests according to the provided verification setup.
     *
     * @param verifySetup JSON string containing the mock-server verification setup
     * @throws AssertionError           if the assertion fails
     * @throws IllegalArgumentException if the provided data is not valid JSON
     */
    void verifyMock(String verifySetup);

    /**
     * Verifies mock-server requests according to the verification setup loaded from a file.
     *
     * @param description description part for step
     * @param path        path to the resource file with mock-server verification setup
     * @throws AssertionError           if the assertion fails
     * @throws IllegalArgumentException if reading the file fails or the provided data is not valid JSON
     */
    void verifyMockCustom(String description, String path);

    /**
     * Verifies mock-server requests according to the verification setup loaded from a file.
     *
     * @param verifySetup JSON string containing the mock-server sequence verification setup
     * @throws AssertionError           if the assertion fails
     * @throws IllegalArgumentException if the provided data is not valid JSON
     */
    void verifyMockSequence(String verifySetup);

    /**
     * Verifies mock-server requests according to the provided verification setup.
     *
     * <p><b>Uses await.</b></p>
     *
     * @param verifySetup JSON string containing the mock-server verification setup
     * @throws AssertionError           if the assertion fails
     * @throws IllegalArgumentException if the provided data is not valid JSON
     */
    void verifyMockWithAwait(String verifySetup);

    /**
     * Verifies mock-server requests according to the verification setup loaded from a file.
     * <p><b>Uses await.</b></p>
     *
     * @param description description part for step
     * @param path        path to the resource file with mock-server verification setup
     * @throws AssertionError           if the assertion fails
     * @throws IllegalArgumentException if reading the file fails or the provided data is not valid JSON
     */
    void verifyMockCustomWithAwait(String description, String path);

    /**
     * Verifies  mock-server requests according to the sequence verification setup
     *
     * <p><b>Uses await.</b></p>
     *
     * @param verifySequenceSetup JSON string containing the mock-server sequence verification setup
     * @throws AssertionError           if the assertion fails
     * @throws IllegalArgumentException if the provided data is not valid JSON
     */
    void verifyMockSequenceWithAwait(String verifySequenceSetup);

    /**
     * Asserts that the total number of requests to mock-server is within the specified range.
     *
     * <p><b>Uses await.</b></p>
     *
     * @param from minimum expected number of requests
     * @param to   maximum expected number of requests
     * @throws AssertionError           if the assertion fails
     * @throws IllegalArgumentException if the provided data is not valid JSON
     */
    void assertMocksCountWithAwait(int from, int to);

    /**
     * Asserts that the total number of requests to mock-server matches the expected count.
     *
     * @param receivedCount expected exact number of requests
     * @throws AssertionError           if the assertion fails
     * @throws IllegalArgumentException if the provided data is not valid JSON
     */
    void assertAllMocksCount(int receivedCount);

    /**
     * Asserts that the total number of requests to mock-server is within the specified range.
     *
     * @param from minimum expected number of requests
     * @param to   maximum expected number of requests
     * @throws AssertionError           if the assertion fails
     * @throws IllegalArgumentException if the provided data is not valid JSON
     */
    void assertAllMocksCount(int from, int to);

    /**
     * Returns a value extracted from a mock-server request (body, header, or other source).
     *
     * <p>Can be used to retrieve dynamic data (for example, ID, hash, or Base64 value)
     * required for subsequent assertions.</p>
     *
     * <pre>{@code
     * var value = mocksApi.getRequestValue(
     *      """
     *      {
     *           "method": "POST",
     *           "path": "/api/user"
     *      }""",
     *      "[0].body.json.name"); //grab value from key "name" from body of first request by condition
     * }</pre>
     *
     * @param mockSetup   - setup for request
     * @param extractPath - json path in request to mock (example: {@code "[0].body.json.id"})
     * @return Object (any types)
     */
    Object getRequestValue(String mockSetup, String extractPath);

}
