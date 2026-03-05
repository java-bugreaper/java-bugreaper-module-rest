package net.bugreaper.modules.rest;

import net.bugreaper.modules.api.Api;
import net.bugreaper.modules.mocks.MocksApi;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import testcontainers.SetupMockserver;

import java.util.Map;

import static net.bugreaper.core.mappers.StringMappers.stringMapper;
import static net.bugreaper.modules.mocks.MocksApi.baseAuthGenerate;

public abstract class PreSetup {

    protected MocksApi mocksApi = new SetupMockserver().getMocksApi();
    protected Api api = new SetupMockserver().getApi();
    protected Api apiSet = new SetupMockserver().getApi();
    protected Api apiLogging = new SetupMockserver().getApi().withLogging(true);
    protected Api apiNoLogs = new SetupMockserver().getApi().withLogging(false);

    protected Api apiJsonPlus = new SetupMockserver().getApi().setHeader("Content-Type", "application/json; charset=utf-8");
    protected Api apiXml = new SetupMockserver().getApi().withContentTypeXml();
    protected Api apiText = new SetupMockserver().getApi().withContentType(ContentType.TEXT);
    protected Api apiNoType = new SetupMockserver().getApi().withoutContentType();

    protected MocksApi mocksApiAwait = new SetupMockserver().getMocksApi().setAwaitMs(400);

    protected final String universalMock = """
            {
              "httpRequest": {
              },
              "httpResponse": {
                "statusCode": 200,
                "body": {
                  "result": "ok"
                }
              },
              "priority": -1
            }""";

    protected final String arrayMock = """
            {
              "httpRequest": {
              },
              "httpResponse": {
                "statusCode": 200,
                "body": [
                    {"id": 1},
                    {"id": 2},
                    {"id": 3}
                  ]
              },
              "priority": -1
            }""";

    protected final String noBody = """
            {
              "httpRequest": {
              },
              "httpResponse": {
                "statusCode": 200
              },
              "priority": -1
            }""";

    protected final String withTimeout = """
            {
              "httpRequest": {
              },
              "httpResponse": {
                "statusCode": 200,
                "body": {
                  "result": "ok"
                },
                 "delay": {
                   "timeUnit": "MILLISECONDS",
                   "value": 700
                }
              },
              "priority": -1
            }""";


    protected void xmlExpectationDefault() {

        mocksApi.createMock("""
                {
                    "httpRequest": {
                        "method": "POST",
                        "path": "/api/post-xml"
                    },
                    "httpResponse": {
                        "statusCode": 200,
                        "headers": {
                            "content-type": [
                                "application/xml"
                            ]
                        },
                        "body": "<response><id>285</id><status>ok</status></response>"
                    }
                }""");
    }

    protected String checkAuth(String user, String pass, int receivedCount) {
        return (stringMapper("""
                        {
                            "httpRequest": {
                                "method": "GET",
                                 "headers": {
                                      "Authorization" : "${auth}"
                                    }
                            },
                            "times": {
                                "atLeast": ${receivedCount},
                                "atMost": ${receivedCount}
                            }
                        }""",
                Map.of(
                        "auth", baseAuthGenerate(user, pass),
                        "receivedCount", receivedCount
                )));
    }

    protected String testMock1(String name, int age, boolean isRegistered) {

        return stringMapper("""
                        {
                            "httpRequest" : {
                              "method" : "GET",
                              "path" : "/api/get"
                            },
                            "httpResponse" : {
                              "statusCode" : 200,
                              "body" : {
                                          "name": "${name}",
                                          "age": ${age},
                                          "name2": "${name} again",
                                          "isRegistered": ${isRegistered},
                               }
                            }
                        }
                        """,
                Map.of(
                        "name", name,
                        "age", age,
                        "isRegistered", isRegistered
                ));
    }

    protected String testMock2(Map<String, Object> params) {

        return stringMapper("""
                        {
                            "httpRequest" : {
                              "method" : "GET",
                              "path" : "/api/get"
                            },
                            "httpResponse" : {
                              "statusCode" : 200,
                              "body" : {
                                          "name": "${name}",
                                          "age": ${age},
                                          "name2": "${name} again"
                                        }
                            }
                        }""",
                params
        );
    }

    protected void createMockTestPostXml(String data, int statusCode) {

        mocksApi.createMock(stringMapper("""
                        {
                            "httpRequest": {
                                "method": "POST",
                                "path": "/api/post-xml",
                                "body": {
                                    "type": "XML",
                                    "xml": "<response> <key>${data}</key> </response>"
                                }
                            },
                            "httpResponse": {
                                "statusCode": ${statusCode},
                                "headers": {
                                    "content-type": [
                                        "application/xml"
                                    ]
                                },
                                "body": "<response><id>285</id><status>ok</status></response>"
                            }
                        }""",
                Map.of(
                        "data", data,
                        "statusCode", statusCode
                )));
    }

    public void verifyMockTestPostXml(String data, int receivedCount) {

        mocksApi.verifyMockWithAwait(stringMapper("""
                        {
                            "httpRequest": {
                                "method": "POST",
                                "path": "/api/post-xml",
                                "body": {
                                    "type": "REGEX",
                                    "regex": ".*<response.*.${data}.*"
                                },
                            },
                            "times": {
                                "atLeast": ${receivedCount},
                                "atMost": ${receivedCount}
                            }
                        }""",
                Map.of(
                        "data", data,
                        "receivedCount", receivedCount
                )));
    }

    public void verifyMockMethod(String method, int receivedCount) {

        mocksApi.verifyMock(stringMapper("""
                        {
                            "httpRequest": {
                                "method": "${method}",
                                "path": "/api/test"
                            },
                            "times": {
                                "atLeast": ${receivedCount},
                                "atMost": ${receivedCount}
                            }
                        }""",
                Map.of(
                        "method", method,
                        "receivedCount", receivedCount
                )));
    }

    public void verifyMockMethodBody(String method, int id, int receivedCount) {

        mocksApi.verifyMock(stringMapper("""
                        {
                            "httpRequest": {
                                "method": "${method}",
                                "path": "/api/test",
                                "body": {
                                    "id": ${id}
                                }
                            },
                            "times": {
                                "atLeast": ${receivedCount},
                                "atMost": ${receivedCount}
                            }
                        }""",
                Map.of(
                        "method", method,
                        "id", id,
                        "receivedCount", receivedCount
                )));
    }

    @BeforeEach
    void cleanMock() {
        mocksApi.resetMocks();
    }
}
