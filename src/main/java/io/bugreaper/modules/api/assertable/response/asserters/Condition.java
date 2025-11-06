package io.bugreaper.modules.api.assertable.response.asserters;

import io.restassured.response.Response;

public interface Condition {

    void check(Response response);
}