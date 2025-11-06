package io.bugreaper.modules.api.assertable.response.asserters;

import io.restassured.response.Response;
import org.hamcrest.Matcher;

public class BodyFieldCondition implements Condition {

    private final String path;
    private final Matcher<?> matcher;

    public BodyFieldCondition(String path, Matcher<?> matcher) {
        this.path = path;
        this.matcher = matcher;
    }

    @Override
    public void check(Response response) {
        response.then().assertThat().body(path, matcher);
    }

    @Override
    public String toString() {
        return "body field [" + path + "] " + matcher;
    }
}