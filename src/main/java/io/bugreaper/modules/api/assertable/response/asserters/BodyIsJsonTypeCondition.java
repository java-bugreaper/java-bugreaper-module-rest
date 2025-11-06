package io.bugreaper.modules.api.assertable.response.asserters;

import io.restassured.response.Response;

import static io.bugreaper.core.assertions.JsonAsserts.checkJson;


public class BodyIsJsonTypeCondition implements BodyCondition {


    @Override
    public void bodyCheck(Response response) {


        checkJson(response.getBody().asString());
    }

    @Override
    public String toString() {
        return "be JSON type";
    }
}
