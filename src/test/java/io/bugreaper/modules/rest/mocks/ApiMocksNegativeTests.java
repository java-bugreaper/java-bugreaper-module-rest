package io.bugreaper.modules.rest.mocks;

import io.bugreaper.modules.mocks.exceptions.MockEnchantedException;
import io.bugreaper.modules.rest.PreSetup;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;


class ApiMocksNegativeTests extends PreSetup {


    @Test
    void wrongVerifySetupError() {

        Throwable exception = assertThrows(IllegalArgumentException.class, () ->
                mocksApi.verifyMock("""
                        {
                          "httpRequest": {
                            "method": "GET",
                            "path": "/api/post"
                            "body" : {
                                  id: 8
                                }
                          },
                          "times": {
                            "atLeast":
                          }
                        }"""));

        MatcherAssert.assertThat(
                "Exception for wrong verify setup",
                exception.getMessage(),
                StringContains.containsString("Wrong JSON/JSONArray format"));
    }

    @Test
    void wrongVerifySetupErrorOther() {

        Throwable exception = assertThrows(MockEnchantedException.class, () ->
                mocksApi.verifyMock("""
                        [1,2,3]"""));

        MatcherAssert.assertThat(
                "Exception for wrong verify setup",
                exception.getMessage(),
                StringContains.containsString("Wrong mock verify setup"));
    }

}
