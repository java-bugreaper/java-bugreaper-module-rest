package io.bugreaper.modules.rest.api;

import io.bugreaper.modules.api.Api;
import io.bugreaper.modules.rest.PreSetup;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.bugreaper.modules.api.assertable.response.ResponseOperators.statusCode;


class ApiBaseSettersTests extends PreSetup {


    @Test
    void testSetterBaseAuth() {
        mocksApi.createMock(universalMock);
        api.setBasicAuth("user1", "password2").sendGet("/api/test")
                .shouldHave(statusCode(200));

        mocksApi.verifyMock(checkAuth("user1", "password2", 1));

        mocksApi.verifyMock("""
                {
                  "httpRequest": {
                    "method": "GET",
                    "headers": {
                        "Authorization" : ["Basic dXNlcjE6cGFzc3dvcmQy"]
                    }
                  },
                  "times": {
                    "atLeast": 1,
                    "atMost": 1
                  }
                }""");
    }

    @Test
    void testSetterTokenAuth() {
        mocksApi.createMock(universalMock);
        api.setBearerAuth("TOKEN1").sendGet("/api/test")
                .shouldHave(statusCode(200));

        mocksApi.verifyMock("""
                {
                  "httpRequest": {
                    "method": "GET",
                    "headers": {
                        "Authorization" : ["Bearer TOKEN1"]
                    }
                  },
                  "times": {
                    "atLeast": 1,
                    "atMost": 1
                  }
                }""");
    }

    @Test
    void testSetterHeader() {
        mocksApi.createMock(universalMock);

        api
                .setHeader("Content-Type", "application/json; charset=utf-8")
                .sendPost("/api/post", "{}")
                .shouldHave(statusCode(200));

        mocksApi.verifyMock("""
                {
                  "httpRequest": {
                    "method": "POST",
                    "headers": {
                        "Content-Type" : ["application/json; charset=utf-8"]
                    }
                  },
                  "times": {
                    "atLeast": 1,
                    "atMost": 1
                  }
                }""");
    }

    @Test
    void testSetterHeaders() {
        mocksApi.createMock(universalMock);
        Api apiTest = api
                .withoutContentType()
                .setHeaders(Map.of
                        ("par1", "data1",
                                "par2", "data2")
                )
                .withLogging(true);

        apiTest.sendPost("/api/test",
                        """
                                {"id" : 1}""")
                .shouldHave(statusCode(200));

        mocksApi.verifyMock("""
                {
                  "httpRequest": {
                    "method": "POST",
                    "headers": {
                        "par1" : ["data1"],
                        "par2" : ["data2"]
                    }
                  },
                  "times": {
                    "atLeast": 1,
                    "atMost": 1
                  }
                }""");
    }

    @Test
    void testSetQueryParams() {
        mocksApi.createMock(universalMock);
        Api apiTest = api
                .withoutContentType()
                .setQueryParams(Map.of
                        ("par1", "data1",
                                "par2", "data2")
                )
                .withLogging(true);

        apiTest.sendPost("/api/test",
                        """
                                {"id" : 1}""")
                .shouldHave(statusCode(200));

        mocksApi.verifyMock("""
                {
                  "httpRequest": {
                    "method": "POST",
                    "queryStringParameters": {
                        "par1": [
                            "data1"
                        ],
                        "par2": [
                            "data2"
                        ]
                    }
                  },
                  "times": {
                    "atLeast": 1,
                    "atMost": 1
                  }
                }""");
    }

}
