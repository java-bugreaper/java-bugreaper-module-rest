package io.bugreaper.modules.api.assertable.response.asserters;

import io.qameta.allure.Allure;
import io.restassured.response.Response;

import static io.bugreaper.core.assertions.JsonAsserts.containsStrictOrderJson;


public class BodyJsonContainsStrictOrderCondition implements BodyCondition {

    private final String expectedBody;

    public BodyJsonContainsStrictOrderCondition(String expectedBody) {
        this.expectedBody = expectedBody;
    }

    @Override
    public void bodyCheck(Response response) {

        Allure.addAttachment("expected part", "application/json", expectedBody);

        containsStrictOrderJson(expectedBody, response.getBody().asString());
    }

    @Override
    public String toString() {
        return "CONTAINS JSON with strict order";
    }
}
