package io.bugreaper.modules.rest.api;

import io.bugreaper.modules.rest.PreSetup;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.Test;

import static io.bugreaper.modules.api.assertable.response.ResponseOperators.responseTimeLess;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("squid:S5778")
class ApiCatchConditionsTests extends PreSetup {


    @Test
    void testTimeoutCondition() {
        mocksApi.createMock(withTimeout);

        var resp = api.sendGet("/api/test");

        Throwable exception = assertThrows(AssertionError.class, () ->
                resp.shouldHave(responseTimeLess(400))

        );

        MatcherAssert.assertThat(
                "Timeout catch by condition (but wait response)",
                exception.getMessage(),
                StringContains.containsString("response time was not a value less than <400L> milliseconds, was"));
    }

}
