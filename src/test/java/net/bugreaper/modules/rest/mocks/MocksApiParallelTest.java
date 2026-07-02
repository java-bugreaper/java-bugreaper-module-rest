package net.bugreaper.modules.rest.mocks;

import net.bugreaper.modules.mocks.MocksApi;
import net.bugreaper.modules.rest.PreSetup;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import testcontainers.SetupMockserver;

import java.util.concurrent.CompletableFuture;

import static java.lang.Thread.sleep;

@SuppressWarnings("java:S5778")
@Execution(ExecutionMode.CONCURRENT)
class MocksApiParallelTest extends PreSetup {

    private static final MocksApi mocksApi = new SetupMockserver().getMocksApi().setAwaitMs(200);

    @BeforeAll
    static void cleanMock() {
        mocksApi.resetMocks();
    }

    @Test
    void testParallelAwaitVerify() {
        mocksApi.setAwaitMs(2000).createMock(universalMock);

        api.sendGet("/api/test")
                .seeResponseCodeIs(200);


        CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> mocksApi.assertMocksCountWithAwait(2, 10));
        CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> pushPostWithSleep(800));

        CompletableFuture.allOf(future1, future2).join();
    }

    @Test
    void testParallelAwaitCount() {
        mocksApi.createMock(universalMock);

        api.sendGet("/api/test")
                .seeResponseCodeIs(200);


        CompletableFuture<Void> future1 = CompletableFuture.runAsync(this::verifyPostAwaiting);
        CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> pushPostWithSleep(300));
        CompletableFuture<Void> future3 = CompletableFuture.runAsync(this::verifySequenceAwait);

        CompletableFuture.allOf(future1, future2, future3).join();
    }


    private void verifyPostAwaiting() {
        mocksApi.verifyMockWithAwait("""
                {
                  "httpRequest": {
                    "method": "POST"
                  },
                  "times": {
                    "atLeast": 1,
                    "atMost": 3
                  }
                }""");
    }

    private void verifySequenceAwait() {
        mocksApi.verifyMockSequenceWithAwait(
                """
                {
                   "httpRequests":[
                      {
                         "method":"GET"
                      },
                      {
                         "method":"POST"
                      }
                   ]
                }""");
    }

    @SuppressWarnings("squid:S2925")
    private void pushPostWithSleep(int sleepMs) {
        try {
            sleep(sleepMs);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        api.sendPost("/api/test")
                .seeResponseCodeIs(200);
    }

}
