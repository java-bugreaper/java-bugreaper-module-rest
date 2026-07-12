package net.bugreaper.modules.rest;

import net.bugreaper.modules.api.Api;
import net.bugreaper.modules.mocks.MocksApi;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

import java.util.Map;


@Isolated
class ApiGlobalAndSpecificSettersTests extends PreSetup {

    final String path = "/api/test";

    @Test
    void testQueryParamsOverwrite() {
        Api apiSet = getApi();
        MocksApi mock = getMocksApi().setEnchantedReport(true);

        mock.createMock(universalMock);

        //set global param
        apiSet.setQueryParams(Map.of("par1", "data1"));

        //send first
        apiSet.sendGet(path)
                .seeResponseCodeIs(200);

        mock.verifyMock("""
                {
                            "httpRequest": {
                                "method": "GET",
                                 "queryStringParameters": {
                                      "par1" : "data1"
                                    }
                            },
                            "times": {
                                "atLeast": 1,
                                "atMost": 1
                            }
                        }""");

        //send second
        apiSet.sendGet(path)
                .seeResponseCodeIs(200);

        mock.verifyMock("""
                {
                            "httpRequest": {
                                "method": "GET",
                                 "queryStringParameters": {
                                      "par1" : "data1"
                                    }
                            },
                            "times": {
                                "atLeast": 2,
                                "atMost": 2
                            }
                        }""");



        //send with specific
        mock.cleanMockLogs();
        apiSet.withQueryParams(Map.of("spec1", "data_sp1","spec2", "data_sp2")).sendGet(path)
                .seeResponseCodeIs(200);

        mock.verifyMock("""
                {
                            "httpRequest": {
                                "method": "GET",
                                 "queryStringParameters": {
                                      "!par1" : [".*"]
                                    }
                            },
                            "times": {
                                "atLeast": 1,
                                "atMost": 1
                            }
                        }""");

        mock.verifyMock("""
                {
                            "httpRequest": {
                                "method": "GET",
                                 "queryStringParameters": {
                                      "spec1" : "data_sp1",
                                      "spec2" : "data_sp2"
                                    }
                            },
                            "times": {
                                "atLeast": 1,
                                "atMost": 1
                            }
                        }""");

        //rollback to Global
        mock.cleanMockLogs();
        apiSet.sendGet(path)
                .seeResponseCodeIs(200);

        mock.verifyMock("""
                {
                            "httpRequest": {
                                "method": "GET",
                                 "queryStringParameters": {
                                      "par1" : "data1"
                                    }
                            },
                            "times": {
                                "atLeast": 1,
                                "atMost": 1
                            }
                        }""");
        mock.verifyMock("""
                {
                            "httpRequest": {
                                "method": "GET",
                                 "queryStringParameters": {
                                      "!spec1" : [".*"]
                                    }
                            },
                            "times": {
                                "atLeast": 1,
                                "atMost": 1
                            }
                        }""");

        //specific two params
        mock.cleanMockLogs();

        apiSet.withQueryParam("spec3", "data_sp3").withQueryParam("spec4", "data_sp4").sendGet(path)
                .seeResponseCodeIs(200);

        mock.verifyMock("""
                {
                            "httpRequest": {
                                "method": "GET",
                                 "queryStringParameters": {
                                      "spec3" : "data_sp3",
                                      "spec4" : "data_sp4"
                                    }
                            },
                            "times": {
                                "atLeast": 1,
                                "atMost": 1
                            }
                        }""");

        mock.verifyMock("""
                {
                            "httpRequest": {
                                "method": "GET",
                                 "queryStringParameters": {
                                      "!spec1" : [".*"]
                                    }
                            },
                            "times": {
                                "atLeast": 1,
                                "atMost": 1
                            }
                        }""");

        //overwrite global
        apiSet.setQueryParams(Map.of("par2", "data2"));

        mock.cleanMockLogs();
        apiSet.sendGet(path)
                .seeResponseCodeIs(200);

        mock.verifyMock("""
                {
                            "httpRequest": {
                                "method": "GET",
                                 "queryStringParameters": {
                                      "par2" : "data2"
                                    }
                            },
                            "times": {
                                "atLeast": 1,
                                "atMost": 1
                            }
                        }""");

        mock.verifyMock("""
                {
                            "httpRequest": {
                                "method": "GET",
                                 "queryStringParameters": {
                                      "!par1" : [".*"]
                                    }
                            },
                            "times": {
                                "atLeast": 1,
                                "atMost": 1
                            }
                        }""");

        //clean global
        apiSet.cleanSetQueryParams();

        mock.cleanMockLogs();
        apiSet.sendGet(path)
                .seeResponseCodeIs(200);

        mock.verifyMock("""
                {
                            "httpRequest": {
                                "method": "GET",
                                 "queryStringParameters": {
                                      "!par1" : [".*"]
                                    }
                            },
                            "times": {
                                "atLeast": 1,
                                "atMost": 1
                            }
                        }""");

        mock.verifyMock("""
                {
                            "httpRequest": {
                                "method": "GET",
                                 "queryStringParameters": {
                                      "!par2" : [".*"]
                                    }
                            },
                            "times": {
                                "atLeast": 1,
                                "atMost": 1
                            }
                        }""");
    }

    @Test
    void testHeadersOverwrite() {
        Api apiSet = getApi();
        MocksApi mock = getMocksApi().setEnchantedReport(true);

        mock.createMock(universalMock);

        //set global headers
        apiSet.setHeaders(Map.of("par1", "data1"));

        //send first
        apiSet.sendGet(path)
                .seeResponseCodeIs(200);

        mock.verifyMock("""
                {
                            "httpRequest": {
                                "method": "GET",
                                 "headers": {
                                      "par1" : "data1"
                                    }
                            },
                            "times": {
                                "atLeast": 1,
                                "atMost": 1
                            }
                        }""");

        //send second
        apiSet.sendGet(path)
                .seeResponseCodeIs(200);

        mock.verifyMock("""
                {
                            "httpRequest": {
                                "method": "GET",
                                 "headers": {
                                      "par1" : "data1"
                                    }
                            },
                            "times": {
                                "atLeast": 2,
                                "atMost": 2
                            }
                        }""");



        //send with specific
        mock.cleanMockLogs();
        apiSet.withHeaders(Map.of("spec1", "data_sp1","spec2", true)).sendGet(path)
                .seeResponseCodeIs(200);

        mock.verifyMock("""
                {
                            "httpRequest": {
                                "method": "GET",
                                 "headers": {
                                      "!par1" : [".*"]
                                    }
                            },
                            "times": {
                                "atLeast": 1,
                                "atMost": 1
                            }
                        }""");

        mock.verifyMock("""
                {
                            "httpRequest": {
                                "method": "GET",
                                 "headers": {
                                      "spec1" : "data_sp1",
                                      "spec2" : "true"
                                    }
                            },
                            "times": {
                                "atLeast": 1,
                                "atMost": 1
                            }
                        }""");

        //rollback to Global
        mock.cleanMockLogs();
        apiSet.sendGet(path)
                .seeResponseCodeIs(200);

        mock.verifyMock("""
                {
                            "httpRequest": {
                                "method": "GET",
                                 "headers": {
                                      "par1" : "data1"
                                    }
                            },
                            "times": {
                                "atLeast": 1,
                                "atMost": 1
                            }
                        }""");
        mock.verifyMock("""
                {
                            "httpRequest": {
                                "method": "GET",
                                 "headers": {
                                      "!spec1" : [".*"]
                                    }
                            },
                            "times": {
                                "atLeast": 1,
                                "atMost": 1
                            }
                        }""");

        //specific two headers
        mock.cleanMockLogs();

        apiSet.withHeader("spec3", "data_sp3").withHeader("spec4", 4).sendGet(path)
                .seeResponseCodeIs(200);

        mock.verifyMock("""
                {
                            "httpRequest": {
                                "method": "GET",
                                 "headers": {
                                      "spec3" : "data_sp3",
                                      "spec4" : "4"
                                    }
                            },
                            "times": {
                                "atLeast": 1,
                                "atMost": 1
                            }
                        }""");

        mock.verifyMock("""
                {
                            "httpRequest": {
                                "method": "GET",
                                 "headers": {
                                      "!spec1" : [".*"]
                                    }
                            },
                            "times": {
                                "atLeast": 1,
                                "atMost": 1
                            }
                        }""");

        //overwrite global
        apiSet.setHeaders(Map.of("par2", "data2"));

        mock.cleanMockLogs();
        apiSet.sendGet(path)
                .seeResponseCodeIs(200);

        mock.verifyMock("""
                {
                            "httpRequest": {
                                "method": "GET",
                                 "headers": {
                                      "par2" : "data2"
                                    }
                            },
                            "times": {
                                "atLeast": 1,
                                "atMost": 1
                            }
                        }""");

        mock.verifyMock("""
                {
                            "httpRequest": {
                                "method": "GET",
                                 "headers": {
                                      "!par1" : [".*"]
                                    }
                            },
                            "times": {
                                "atLeast": 1,
                                "atMost": 1
                            }
                        }""");

        //Add! global one
        apiSet.setHeader("par3", "data3");

        mock.cleanMockLogs();
        apiSet.sendGet(path)
                .seeResponseCodeIs(200);

        mock.verifyMock("""
                {
                            "httpRequest": {
                                "method": "GET",
                                 "headers": {
                                      "par2" : "data2",
                                      "par3" : "data3"
                                    }
                            },
                            "times": {
                                "atLeast": 1,
                                "atMost": 1
                            }
                        }""");
        //clean global
        apiSet.cleanSetHeaders();

        mock.cleanMockLogs();
        apiSet.sendGet(path)
                .seeResponseCodeIs(200);

        mock.verifyMock("""
                {
                            "httpRequest": {
                                "method": "GET",
                                 "headers": {
                                      "!par1" : [".*"]
                                    }
                            },
                            "times": {
                                "atLeast": 1,
                                "atMost": 1
                            }
                        }""");

        mock.verifyMock("""
                {
                            "httpRequest": {
                                "method": "GET",
                                 "headers": {
                                      "!par2" : [".*"]
                                    }
                            },
                            "times": {
                                "atLeast": 1,
                                "atMost": 1
                            }
                        }""");


    }

}
