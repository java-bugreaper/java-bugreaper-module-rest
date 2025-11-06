package io.bugreaper.modules.api.assertable.response.asserters;

import io.qameta.allure.Allure;
import io.restassured.response.Response;

import static io.bugreaper.core.assertions.JsonAsserts.assertJson;


public class BodyJsonEqualCondition implements BodyCondition {

    private final String expectedBody;

    public BodyJsonEqualCondition(String expectedBody) {
        this.expectedBody = expectedBody;
    }

    @Override
    public void bodyCheck(Response response) {

        Allure.addAttachment("expected json", "application/json", expectedBody);

        assertJson(expectedBody, response.getBody().asString());
    }

    @Override
    public String toString() {
        return "be EQUAL to JSON with strict order";
    }
}
