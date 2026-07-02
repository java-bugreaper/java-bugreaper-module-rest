package net.bugreaper.modules.rest.api;

import net.bugreaper.modules.api.assertable.AssertableResponse;
import net.bugreaper.modules.rest.PreSetup;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

import static org.junit.jupiter.api.Assertions.assertThrows;


@SuppressWarnings("squid:S5778")
@Isolated
class ApiCatchConditionsTests extends PreSetup {


    @Test
    void testMaxResponseTimeCondition() {
        mocksApi.createMock(withTimeout);

        var resp = api.sendGet("/api/test");

        Throwable exception = assertThrows(AssertionError.class, () ->
                resp.seeResponseTimeLess(400)
        );

        MatcherAssert.assertThat(
                "Timeout catch by condition (but wait response)",
                exception.getMessage(),
                StringContains.containsString("response time was not a value less than <400L> milliseconds, was"));
    }

    @Test
    void testArrayCountCatch() {

        mocksApi.createMock(arrayMock);

        AssertableResponse result = api.sendGet("/api/test")
                .seeResponseCodeIsSuccessful();

        Throwable exception = assertThrows(AssertionError.class, () ->
                result.seeResponseBodyElementsCount(2));

        MatcherAssert.assertThat(
                "Catch array count",
                exception.getMessage(),
                StringContains.containsString("""
                        JSON path size() doesn't match.
                        Expected: is <2>
                          Actual: <3>"""));
    }

}
