package io.bugreaper.modules.api.assertable.response.asserters;

import io.restassured.response.Response;

import java.util.concurrent.TimeUnit;

import static io.bugreaper.core.mappers.StringMappers.formatMilliseconds;
import static org.hamcrest.Matchers.lessThan;

public class ResponseTimeLessCondition implements Condition {

    private final long maximumResponseTime;

    public ResponseTimeLessCondition(long maximumResponseTime) {
        this.maximumResponseTime = maximumResponseTime;
    }

    @Override
    public void check(Response response) {
        response.then().time(lessThan(maximumResponseTime), TimeUnit.MILLISECONDS);
    }

    @Override
    public String toString() {
        return "<response time> less " + formatMilliseconds(maximumResponseTime);
    }
}