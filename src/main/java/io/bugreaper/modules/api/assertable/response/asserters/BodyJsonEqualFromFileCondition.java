package io.bugreaper.modules.api.assertable.response.asserters;

import io.restassured.response.Response;

import java.nio.file.Path;

import static io.bugreaper.core.allurereporter.AllureReporter.attachFromFileNoStep;
import static io.bugreaper.core.assertions.JsonAsserts.assertJson;
import static io.bugreaper.core.filereaders.FileReader.readJsonFromFile;

public class BodyJsonEqualFromFileCondition implements BodyCondition {

    private final String path;

    public BodyJsonEqualFromFileCondition(Path path) {
        this.path = String.valueOf(path);
    }

    @Override
    public void bodyCheck(Response response) {

        attachFromFileNoStep(path, String.valueOf(path));

        assertJson(readJsonFromFile(path), response.getBody().asString());
    }

    @Override
    public String toString() {
        return "be EQUAL to JSON with strict order";
    }
}
