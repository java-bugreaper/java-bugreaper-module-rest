package io.bugreaper.modules.rest;

import org.hamcrest.MatcherAssert;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class ApiMocksConfigureValidationTests extends PreSetup {


    @Test
    void configMinusAwaitTest() {


        Throwable exception = assertThrows(IllegalArgumentException.class, () ->
                mocksApi.withAwaitMs(-1));

        MatcherAssert.assertThat(
                "Error on config .withAwaitMs negative validation",
                exception.getMessage(),
                StringContains.containsString("awaitMs too small (can`t bee less 200ms)"));
    }

    @Test
    void configLessAwaitTest() {


        Throwable exception = assertThrows(IllegalArgumentException.class, () ->
                mocksApi.withAwaitMs(199));

        MatcherAssert.assertThat(
                "Error on config .withAwaitMs negative validation",
                exception.getMessage(),
                StringContains.containsString("awaitMs too small (can`t bee less 200ms)"));
    }

}
