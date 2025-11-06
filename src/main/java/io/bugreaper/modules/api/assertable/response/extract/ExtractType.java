package io.bugreaper.modules.api.assertable.response.extract;

import io.restassured.response.Response;

public interface ExtractType {

    String extract(Response response);
}