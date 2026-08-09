package net.bugreaper.modules.rest.mocks;

import net.bugreaper.modules.mocks.exceptions.MockEnchantedException;
import net.bugreaper.modules.rest.PreSetup;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

import static org.junit.jupiter.api.Assertions.assertThrows;


@Isolated
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
                StringContains.containsString("Invalid JSON or JSON array format (lenient)"));
    }

    @Test
    void wrongVerifySetupErrorOther() {

        Throwable exception = assertThrows(MockEnchantedException.class, () ->
                mocksApi.verifyMock("""
                        [1,2,3]"""));

        MatcherAssert.assertThat(
                "Exception for wrong verify setup",
                exception.getMessage(),
                StringContains.containsString("Wrong request verify setup"));
    }

}
