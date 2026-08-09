package net.bugreaper.modules.rest.mocks;

import net.bugreaper.modules.mocks.MocksApi;
import net.bugreaper.modules.rest.PreSetup;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

import static org.junit.jupiter.api.Assertions.assertThrows;


@SuppressWarnings("java:S5778")
@Isolated
class MocksApiCatchTest extends PreSetup {

    protected MocksApi mocksApi = getMocksApi();

    @BeforeEach
    void cleanMock() {
        mocksApi.resetMocks();
    }

    @Test
    void testWithAwaitAssertFailed() {

        Throwable exception = assertThrows(AssertionError.class, () ->
                mocksApi.withAwaitMs(600).verifyMockWithAwait("""
                        {
                            "httpRequest": {
                                "method": "GET",
                                "path": "/api/no-req"
                            },
                            "times": {
                                "atLeast": 1
                            }
                        }"""));

        MatcherAssert.assertThat(
                exception.getMessage(),
                StringContains.containsString("No requests to mock-server in test"));
    }

    @Test
    void testWithAwaitSequenceFailed() {

        Throwable exception = assertThrows(AssertionError.class, () ->
                mocksApi.withAwaitMs(400).verifyMockSequenceWithAwait("""
                        {
                           "httpRequests":[
                              {
                                 "method":"GET"
                              },
                              {
                                 "method":"POST"
                              }
                           ]
                        }"""));

        MatcherAssert.assertThat(
                exception.getMessage(),
                StringContains.containsString("Mock-server request sequence does not match"));
    }

    @Test
    void testWithAwaitAssertCountFailed() {

        Throwable exception = assertThrows(AssertionError.class, () ->
                mocksApi.withAwaitMs(400).assertMocksCountWithAwait(1,2));

        MatcherAssert.assertThat(
                exception.getMessage(),
                StringContains.containsString("No requests to mock-server in test"));
    }


    @Test
    void testBrokenMockCreate() {

        Throwable exception = assertThrows(IllegalArgumentException.class, () ->
                mocksApi.createMock("afasfasf"));

        MatcherAssert.assertThat(
                "Broken JSON for mock creation",
                exception.getMessage(),
                StringContains.containsString("Failed to create mock-server expectation"));
    }

    @Test
    void testBrokenMockVerify() {

        Throwable exception = assertThrows(IllegalArgumentException.class, () ->
                mocksApi.verifyMock("""
                        {
                            "httpRequest": {
                                "method": "GET"
                            },
                            "times": {
                                "atLeast"afqefqf: 2
                            }
                        }"""));

        MatcherAssert.assertThat(
                "Broken JSON for mock verify",
                exception.getMessage(),
                StringContains.containsString("Invalid JSON or JSON array format (lenient)"));
    }

}
