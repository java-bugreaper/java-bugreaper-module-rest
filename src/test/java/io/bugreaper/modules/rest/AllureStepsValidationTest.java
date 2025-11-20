package io.bugreaper.modules.rest;

import org.junit.jupiter.api.Test;

import static io.bugreaper.core.utils.AllureStepsValidator.validateAllSteps;

class AllureStepsValidationTest {

    @Test
    void testStepsApi() {
        validateAllSteps("io.bugreaper.modules.api.Api");
    }

    @Test
    void testStepsMocksApi() {
        validateAllSteps("io.bugreaper.modules.mocks.MocksApi");
    }

}
