package io.bugreaper.modules.api.assertable.response.asserters;

import io.restassured.response.Response;

public interface BodyCondition {

    void bodyCheck(Response response);
}