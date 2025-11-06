package io.bugreaper.modules.api.assertable.response.asserters;

import io.restassured.response.Response;

import static io.bugreaper.core.allurereporter.AllureReporter.attachFromFileNoStep;
import static io.restassured.matcher.RestAssuredMatchers.matchesXsdInClasspath;

public class BodyXmlValidatorCondition implements BodyCondition {

    private final String path;

    public BodyXmlValidatorCondition(String path) {
        this.path = path;
    }

    @Override
    public void bodyCheck(Response response) {
        attachFromFileNoStep(path, path);
        response.then().assertThat().body(matchesXsdInClasspath(path));
    }

    @Override
    public String toString() {
        return "have correct XML schema";
    }
}
