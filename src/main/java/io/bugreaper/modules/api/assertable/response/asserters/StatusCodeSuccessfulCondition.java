package io.bugreaper.modules.api.assertable.response.asserters;

import io.restassured.response.Response;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class StatusCodeSuccessfulCondition implements Condition {


    @Override
    public void check(Response response) {

        int statusCode = response.getStatusCode();

        assertTrue(statusCode >= 200 && statusCode <= 299,
                "Expected successful status code, but got: " + statusCode);
    }

    @Override
    public String toString() {
        return "<status code> is SUCCESSFUL";
    }
}