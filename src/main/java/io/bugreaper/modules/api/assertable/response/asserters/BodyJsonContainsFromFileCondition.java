package io.bugreaper.modules.api.assertable.response.asserters;

import io.restassured.response.Response;

import java.nio.file.Path;

import static io.bugreaper.core.allurereporter.AllureReporter.attachFromFileNoStep;
import static io.bugreaper.core.assertions.JsonAsserts.containsJson;
import static io.bugreaper.core.filereaders.FileReader.readJsonFromFile;

public class BodyJsonContainsFromFileCondition implements BodyCondition {

    private final String path;

    public BodyJsonContainsFromFileCondition(Path path) {
        this.path = String.valueOf(path);
    }

    @Override
    public void bodyCheck(Response response) {

        attachFromFileNoStep(path, String.valueOf(path));

        containsJson(readJsonFromFile(path), response.getBody().asString());
    }

    @Override
    public String toString() {
        return "CONTAINS JSON with non-strict order";
    }
}
