package io.bugreaper.modules.api.assertable.response.asserters;

import io.qameta.allure.Allure;
import io.restassured.response.Response;

import static io.bugreaper.core.assertions.JsonAsserts.containsJson;


public class BodyJsonContainsCondition implements BodyCondition {

    private final String expectedBody;

    public BodyJsonContainsCondition(String expectedBody) {
        this.expectedBody = expectedBody;
    }

    @Override
    public void bodyCheck(Response response) {

        Allure.addAttachment("expected json part", "application/json", expectedBody);

        containsJson(expectedBody, response.getBody().asString());
    }

    @Override
    public String toString() {
        return "CONTAINS JSON with non-strict order";
    }
}
