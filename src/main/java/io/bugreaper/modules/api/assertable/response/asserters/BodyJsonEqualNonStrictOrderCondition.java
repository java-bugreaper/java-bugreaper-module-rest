package io.bugreaper.modules.api.assertable.response.asserters;

import io.qameta.allure.Allure;
import io.restassured.response.Response;

import static io.bugreaper.core.assertions.JsonAsserts.assertNoStrictOrderJson;


public class BodyJsonEqualNonStrictOrderCondition implements BodyCondition {

    private final String expectedBody;

    public BodyJsonEqualNonStrictOrderCondition(String expectedBody) {
        this.expectedBody = expectedBody;
    }

    @Override
    public void bodyCheck(Response response) {

        Allure.addAttachment("expected part", "application/json", expectedBody);

        assertNoStrictOrderJson(expectedBody, response.getBody().asString());
    }

    @Override
    public String toString() {
        return "be EQUAL to JSON with non-strict order";
    }
}
