package net.bugreaper.modules.rest;

import org.junit.jupiter.api.Test;

import static net.bugreaper.core.utils.AllureStepsValidator.validateAllSteps;

class AllureStepsValidationTest {

    @Test
    void testStepsApi() {
        validateAllSteps("net.bugreaper.modules.api.Api");
    }

    @Test
    void testStepsMocksApi() {
        validateAllSteps("net.bugreaper.modules.mocks.MocksApi");
    }

    @Test
    void testStepsAssertableResponse() {
        validateAllSteps("net.bugreaper.modules.api.assertable.AssertableResponse");
    }

}
