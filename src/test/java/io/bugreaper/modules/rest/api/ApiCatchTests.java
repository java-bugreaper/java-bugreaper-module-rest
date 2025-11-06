package io.bugreaper.modules.rest.api;

import io.bugreaper.modules.api.Api;
import io.bugreaper.modules.rest.PreSetup;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.Test;
import testcontainers.SetupMockserver;

import static org.junit.jupiter.api.Assertions.assertThrows;


class ApiCatchTests extends PreSetup {


    @Test
    void testTimeoutSetter() {
        mocksApi.resetMocks();
        mocksApi.createMock(withTimeout);

        Api apiTime = new SetupMockserver().getApi().withMaxResponseMsAssert(100);

        Throwable exception = assertThrows(AssertionError.class, () ->
                apiTime.sendGet("/api/test"));

        MatcherAssert.assertThat(
                "Timeout catch by setter (but wait response)",
                exception.getMessage(),
                StringContains.containsString("Expected response time was not a value less than <100L> milliseconds"));

    }

}
