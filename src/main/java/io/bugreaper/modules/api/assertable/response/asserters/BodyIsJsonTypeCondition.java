package io.bugreaper.modules.api.assertable.response.asserters;

import io.restassured.response.Response;

import static io.bugreaper.core.assertions.JsonAsserts.assertValidJson;


public class BodyIsJsonTypeCondition implements BodyCondition {


    @Override
    public void bodyCheck(Response response) {


        assertValidJson(response.getBody().asString());
    }

    @Override
    public String toString() {
        return "be JSON type";
    }
}
