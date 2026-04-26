package net.bugreaper.modules.rest.api;

import net.bugreaper.modules.api.Api;
import net.bugreaper.modules.rest.PreSetup;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Map;


class ApiBaseSettersTests extends PreSetup {


    @Test
    void testSetterBaseAuth() {
        mocksApi.createMock(universalMock);
        api.setBasicAuth("user1", "password2").sendGet("/api/test")
                .seeResponseCodeIs(200);

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
                .seeResponseCodeIs(200);

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
                .seeResponseCodeIs(200);

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
                .setNoContentType()
                .setHeaders(Map.of
                        ("par1", "data1",
                                "par2", "data2")
                )
                .setLogging(true);

        apiTest.sendPost("/api/test",
                        """
                                {"id" : 1}""")
                .seeResponseCodeIs(200);

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
                .setNoContentType()
                .setQueryParams(Map.of
                        ("par1", "data1",
                                "par2", "data2")
                )
                .setLogging(true);

        apiTest.sendPost("/api/test",
                        """
                                {"id" : 1}""")
                .seeResponseCodeIs(200);

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

    @Test
    void testSetQuerySameParams() {
        mocksApi.createMock(universalMock);
        Api apiTest = api
                .setNoContentType()
                .withQueryParam("test", Arrays.asList("one", "two"))
                .setLogging(true);

        apiTest.sendPost("/api/test",
                        """
                                {"id" : 1}""")
                .seeResponseCodeIs(200);

        mocksApi.verifyMock("""
                {
                  "httpRequest": {
                    "method": "POST",
                    "queryStringParameters": {
                        "test": [
                            "two",
                            "one"
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
