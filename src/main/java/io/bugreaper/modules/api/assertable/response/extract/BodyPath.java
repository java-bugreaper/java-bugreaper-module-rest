package io.bugreaper.modules.api.assertable.response.extract;

import io.qameta.allure.Allure;
import io.restassured.response.Response;

import static io.bugreaper.core.allurereporter.AllureReporter.attachCanBeNull;


public class BodyPath implements ExtractType {

    private final String path;

    public BodyPath(String path) {
        this.path = path;
    }

    @Override
    public String extract(Response response) {
        String result = response.then().extract().path(path);

        Allure.step("Extract from body: [" + path + "]");

        attachCanBeNull(path, result);

        return result;
    }

}

