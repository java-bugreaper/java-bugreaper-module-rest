package io.bugreaper.modules.api.assertable.response.extract;

import io.qameta.allure.Allure;
import io.restassured.response.Response;

public class Header implements ExtractType {

    private final String headerName;

    public Header(String header) {
        this.headerName = header;
    }

    @Override
    public String extract(Response response) {
        String result = response.header(headerName);
        Allure.step("Extract header: [" + headerName + "] = " + result);
        return result;
    }

}

