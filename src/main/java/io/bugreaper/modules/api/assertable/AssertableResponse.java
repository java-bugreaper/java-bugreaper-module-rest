package io.bugreaper.modules.api.assertable;

import io.bugreaper.modules.api.assertable.response.ResponseOperators;
import io.bugreaper.modules.api.assertable.response.asserters.BodyCondition;
import io.bugreaper.modules.api.assertable.response.asserters.Condition;
import io.bugreaper.modules.api.assertable.response.extract.ExtractType;
import io.qameta.allure.Step;
import io.restassured.response.Response;


public class AssertableResponse {

    private final Response response;

    public AssertableResponse(Response response) {
        this.response = response;
    }

    @Step("API response should have: {condition}")
    public AssertableResponse shouldHave(Condition condition) {
        condition.check(response);
        return this;
    }

    @Step("API response body should: {bodyCondition}")
    public AssertableResponse bodyShould(BodyCondition bodyCondition) {
        bodyCondition.bodyCheck(response);
        return this;
    }

    /**
     * Extract String data from response
     * use for tests where need to grab dynamic data and reuse it
     *
     * @param extractType types: {@link ResponseOperators#grabFromBodyPath(String)},{@link ResponseOperators#grabHeader(String)})
     * @return String
     */
    public String extract(ExtractType extractType) {
        return extractType.extract(response);
    }


}
