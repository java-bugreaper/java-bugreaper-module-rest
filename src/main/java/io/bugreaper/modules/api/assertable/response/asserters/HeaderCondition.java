package io.bugreaper.modules.api.assertable.response.asserters;

import io.restassured.response.Response;
import org.hamcrest.Matcher;

public class HeaderCondition implements Condition {

    private final String headerName;
    private final Matcher<?> matcher;

    public HeaderCondition(String headerName, Matcher<?> matcher) {
        this.headerName = headerName;
        this.matcher = matcher;
    }

    @Override
    public void check(Response response) {
        response.then().assertThat().header(headerName, matcher);
    }

    @Override
    public String toString() {
        return "header [" + headerName + "] " + matcher;
    }
}