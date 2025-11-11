package io.bugreaper.modules.rest.mocks;

import io.bugreaper.modules.rest.PreSetup;
import org.junit.jupiter.api.Test;


@SuppressWarnings({"squid:S2699", "squid:S5976"})
class ApiMocksEnchantedLogicPassedTests extends PreSetup {


    @Test
    void testVerifyMockPassed() {
        mocksApi.createMock(universalMock);

        api.sendPost("/api/post",
                        """
                                {
                                  "id": 1
                                }""")
                .seeResponseCodeIs(200);

        mocksApi.verifyMock("""
                {
                  "httpRequest": {
                    "method": "POST",
                    "path": "/api/post",
                    "body" : {
                          "id": 1
                        }
                  },
                  "times": {
                    "atLeast": 1,
                    "atMost": 1
                  }
                }""");
    }

    @Test
    void testVerifySomeBodyMockPassed() {
        mocksApi.createMock(universalMock);

        api.sendPost("/api/post",
                        """
                                {
                                  "id": 1
                                }""")
                .seeResponseCodeIs(200);

        mocksApi.verifyMock("""
                {
                  "httpRequest": {
                    "method": "POST",
                    "headers": {
                        "Connection" : ["Keep-Alive"]
                    },
                    "body" : {}
                  },
                  "times": {
                    "atLeast": 1,
                    "atMost": 1
                  }
                }""");
    }

    @Test
    void testVerifyMockArrayPassed() {
        mocksApi.createMock(universalMock);

        api.sendPost("/api/post",
                        """
                                {
                                  "id": 1,
                                  "array": [1, 2, 3]
                                }""")
                .seeResponseCodeIs(200);

        mocksApi.verifyMock("""
                {
                  "httpRequest": {
                    "method": "POST",
                    "path": "/api/post",
                    "body" : {
                          "id": 1,
                          "array": [1]
                        }
                  },
                  "times": {
                    "atLeast": 1,
                    "atMost": 1
                  }
                }""");
    }

    @Test
    void testVerifyMockStrictPassed() {
        mocksApi.createMock(universalMock);

        api.sendPost("/api/post",
                        """
                                {
                                  "id": 1
                                }""")
                .seeResponseCodeIs(200);

        mocksApi.verifyMock("""
                {
                  "httpRequest": {
                    "method": "POST",
                    "path": "/api/post",
                    "body" : {
                         "type": "JSON",
                         "json": {
                            "id": 1
                         },
                         "matchType": "STRICT"
                    }
                  },
                  "times": {
                    "atLeast": 1,
                    "atMost": 1
                  }
                }""");
    }

    @Test
    void testVerifyMockStrictArrayPassed() {
        mocksApi.createMock(universalMock);

        api.sendPost("/api/post",
                        """
                                {
                                  "id": 1,
                                  "array": [1, 2, 3]
                                }""")
                .seeResponseCodeIs(200);

        mocksApi.verifyMock("""
                {
                  "httpRequest": {
                    "method": "POST",
                    "path": "/api/post",
                    "body" : {
                         "type": "JSON",
                         "json": {
                            "id": 1,
                          "array": [1, 2, 3]
                         },
                         "matchType": "STRICT"
                    }
                  },
                  "times": {
                    "atLeast": 1,
                    "atMost": 1
                  }
                }""");
    }
}
