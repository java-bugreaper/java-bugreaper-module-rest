package net.bugreaper.modules.rest.mocks;

import net.bugreaper.modules.mocks.MocksApi;
import net.bugreaper.modules.rest.PreSetup;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testcontainers.SetupMockserver;

import java.util.concurrent.CompletableFuture;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("java:S5778")
class MocksApiCatchTest extends PreSetup {

    protected MocksApi mocksApi = new SetupMockserver().getMocksApi();

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
                StringContains.containsString("Expected mock sequence not match"));
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
                StringContains.containsString("Wrong mock creation setup"));
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
                StringContains.containsString("Invalid lenient JSON/JSONArray"));
    }

    @Test
    void testParallelAwaitVerify() {
        mocksApi.createMock(universalMock);

        api.sendGet("/api/test")
                .seeResponseCodeIs(200);


        CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> mocksApi.assertMocksCountWithAwait(2, 3));
        CompletableFuture<Void> future2 = CompletableFuture.runAsync(this::pushWithSleep);

        CompletableFuture.allOf(future1, future2).join();
    }

    @Test
    void testParallelAwaitCount() {
        mocksApi.createMock(universalMock);

        api.sendGet("/api/test")
                .seeResponseCodeIs(200);


        CompletableFuture<Void> future1 = CompletableFuture.runAsync(this::verifyPostAwaiting);
        CompletableFuture<Void> future2 = CompletableFuture.runAsync(this::pushWithSleep);

        CompletableFuture.allOf(future1, future2).join();
    }

    private void verifyPostAwaiting() {
        mocksApi.verifyMockWithAwait("""
                {
                  "httpRequest": {
                    "method": "POST"
                  },
                  "times": {
                    "atLeast": 1,
                    "atMost": 1
                  }
                }""");
    }

    @SuppressWarnings("squid:S2925")
    private void pushWithSleep() {
        try {
            sleep(700);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        api.sendPost("/api/test")
                .seeResponseCodeIs(200);
    }

}
