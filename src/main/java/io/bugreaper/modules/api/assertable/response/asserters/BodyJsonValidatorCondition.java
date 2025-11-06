package io.bugreaper.modules.api.assertable.response.asserters;

import io.restassured.response.Response;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static io.bugreaper.core.allurereporter.AllureReporter.*;

public class BodyJsonValidatorCondition implements BodyCondition {

    private final String path;

    public BodyJsonValidatorCondition(String path) {
        this.path = path;
    }

    @Override
    public void bodyCheck(Response response) {
        attachFromFileNoStep(path, path);
        response.then().assertThat().body(matchesJsonSchemaInClasspath(path));
    }

    @Override
    public String toString() {
        return "have correct JSON schema";
    }
}
