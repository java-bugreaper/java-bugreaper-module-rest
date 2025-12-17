package net.bugreaper.modules.rest.mocks;

import net.bugreaper.modules.rest.PreSetup;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


class ApiMocksEnchantedJsonSchemaTests extends PreSetup {

    @Test
    void differentTypesOfBodyTest() {
        mocksApi.createMock(universalMock);

        api.sendPost("/api/post",
                        """
                                {
                                "id": 3,
                                "text": "some text"
                                }""")
                .seeResponseCodeIs(200);

        api.sendPost("/api/post",
                        """
                                {
                                "id_wrong": 4,
                                }""")
                .seeResponseCodeIs(200);

        api.sendPost("/api/post",
                        """
                                {
                                "id_wrong":"text",
                                }""")
                .seeResponseCodeIs(200);

        Throwable exception = assertThrows(AssertionFailedError.class, () ->
                mocksApi.verifyMock("""
                        {
                            "httpRequest": {
                                "method": "POST",
                                "path": "/api/post",
                                "body": {
                                    "jsonSchema": {
                                        "type": "object",
                                        "properties": {
                                             "id_wrong": {
                                             "type": "integer"
                                             }
                                        },
                                        "additionalProperties": false
                                    }
                                }
                            },
                            "times": {
                                "atLeast": 2,
                                "atMost": 2
                            }
                        }"""));

        assertThat(
                "Assert for expected verify body Schema check with different types",
                exception.getMessage(),
                StringContains.containsString("""
                        Count of expected mock request(s) not match.
                        Expected Exactly <2> with AR <1>
                        Check report for more info"""));
    }

    @Test
    void checkJsonSchemaWithEmptyActualBodyJson() {
        mocksApi.createMock(universalMock);

        apiJsonPlus.sendPost("/api/post", "{}")
                .seeResponseCodeIs(200);

        mocksApi.verifyMock("""
                {
                    "httpRequest": {
                        "method": "POST",
                        "path": "/api/post",
                        "body": {
                            "jsonSchema": {
                                "type": "object",
                                "properties": {
                                     "id_wrong": {
                                     "type": "integer"
                                     }
                                },
                                "additionalProperties": false
                            }
                        }
                    },
                    "times": {
                        "atLeast": 1,
                        "atMost": 1
                    }
                }""");

    }

    @Test
    void checkJsonSchemaRequiredWithEmptyActualBodyJson() {
        mocksApi.createMock(universalMock);

        apiJsonPlus.sendPost("/api/post", "{}")
                .seeResponseCodeIs(200);

        Throwable exception = assertThrows(AssertionFailedError.class, () ->
                mocksApi.verifyMock("""
                        {
                            "httpRequest": {
                                "method": "POST",
                                "path": "/api/post",
                                "body": {
                                    "jsonSchema": {
                                        "type": "object",
                                        "required": [
                                                    "id_wrong"
                                                ],
                                        "properties": {
                                             "id_wrong": {
                                             "type": "integer"
                                             }
                                        },
                                        "additionalProperties": false
                                    }
                                }
                            },
                            "times": {
                                "atLeast": 1,
                                "atMost": 1
                            }
                        }"""));

        assertThat(
                "Assert for expected verify body Schema check(required) with empty actual body",
                exception.getMessage(),
                StringContains.containsString("""
                        Schema check in actual requests FAILED"""));

    }

    @Test
    void checkJsonSchemaNotPassGetTest() {
        mocksApi.createMock(universalMock);

        api.sendGet("/api/get")
                .seeResponseCodeIs(200);
        api.sendGet("/api/get")
                .seeResponseCodeIs(200);

        Throwable exception = assertThrows(AssertionFailedError.class, () ->
                mocksApi.verifyMock("""
                        {
                            "httpRequest": {
                                "method": "GET",
                                "body": {
                                    "jsonSchema": {
                                        "type": "object",
                                        "properties": {
                                             "id_wrong": {
                                             "type": "integer"
                                             }
                                        },
                                        "additionalProperties": false
                                    }
                                }
                            },
                            "times": {
                                "atLeast": 1,
                                "atMost": 1
                            }
                        }"""));

        assertThat(
                "Assert for expected verify body Schema check(required) without actual body(GET)",
                exception.getMessage(),
                StringContains.containsString("""
                        Schema check in actual requests FAILED"""));

        assertThat(
                "Sub exception trace",
                exception.getMessage(),
                StringContains.containsString("""
                        -----------1
                        Schema assert failed (Actual body absent)
                                                
                        -----------2
                        Schema assert failed (Actual body absent)"""));

    }

    @Test
    void checkJsonSchemaNotPassStringAndXmlTest() {
        mocksApi.createMock(universalMock);

        xmlExpectationDefault();

        apiText.sendPost("/api/post", "some string")
                .seeResponseCodeIs(200);
        apiXml.sendPost("/api/post-xml",
                        """
                                "<request> <key>id55</key> </request>"
                                """)
                .seeResponseCodeIs(200);

        Throwable exception = assertThrows(AssertionFailedError.class, () ->
                mocksApi.verifyMock("""
                        {
                            "httpRequest": {
                                "method": "POST",
                                "body": {
                                    "jsonSchema": {
                                        "type": "object",
                                        "properties": {
                                             "id_wrong": {
                                             "type": "integer"
                                             }
                                        },
                                        "additionalProperties": false
                                    }
                                }
                            },
                            "times": {
                                "atLeast": 1,
                                "atMost": 1
                            }
                        }"""));

        assertThat(
                "Assert for expected verify body Schema check(required) without actual bodies(String & XML)",
                exception.getMessage(),
                StringContains.containsString("""
                        Schema check in actual requests FAILED"""));

        assertThat(
                "Sub exception trace",
                exception.getMessage(),
                StringContains.containsString("""
                        -----------1
                        Schema assert failed (Actual body not JSON)
                                                
                        -----------2
                        Schema assert failed (Actual body not JSON)"""));

    }

    @Test
    void differentTypesOfBody2Test() {
        mocksApi.createMock(universalMock);

        xmlExpectationDefault();


        apiText.sendPost("/api/post", "some string")
                .seeResponseCodeIs(200);

        api.sendPost("/api/post",
                        """
                                {
                                "id_wrong": 5,
                                }""")
                .seeResponseCodeIs(200);

        api.sendGet("/api/get")
                .seeResponseCodeIs(200);

        api.sendPost("/api/post",
                        """
                                {
                                "id_wrong": "text",
                                }""")
                .seeResponseCodeIs(200);

        apiJsonPlus.sendPost("/api/post", "{}")
                .seeResponseCodeIs(200); //problem

        apiXml.sendPost("/api/post-xml",
                        """
                                "<request> <key>id55</key> </request>"
                                """)
                .seeResponseCodeIs(200);

        apiText.sendPost("/api/post", "103")
                .seeResponseCodeIs(200);

        Throwable exception = assertThrows(AssertionFailedError.class, () ->
                mocksApi.verifyMock("""
                        {
                            "httpRequest": {
                                "method": "POST",
                                "path": "/api/post",
                                "body": {
                                    "jsonSchema": {
                                        "type": "object",
                                        "properties": {
                                             "id_wrong": {
                                             "type": "integer"
                                             }
                                        },
                                        "additionalProperties": false
                                    }
                                }
                            },
                            "times": {
                                "atLeast": 3,
                                "atMost": 3
                            }
                        }"""));

        assertThat(
                "Assert for expected verify body Schema check with different types",
                exception.getMessage(),
                StringContains.containsString("""
                        Count of expected mock request(s) not match.
                        Expected Exactly <3> with AR <2>
                        Check report for more info"""));

    }

    @Test
    void verifyValidation_bodySchemaBase() {
        mocksApi.createMock(universalMock);

        api.sendPost("/api/post",
                        """
                                {
                                "id": 3,
                                "text": "some text"
                                }""")
                .seeResponseCodeIs(200);


        Throwable exception = assertThrows(AssertionFailedError.class, () ->
                mocksApi.verifyMock("""
                        {
                            "httpRequest": {
                                "method": "POST",
                                "path": "/api/post",
                                "body": {
                                    "jsonSchema": {
                                        "type": "object",
                                        "properties": {
                                             "id_wrong": {
                                             "type": "integer"
                                             }
                                        },
                                        "additionalProperties": false
                                    }
                                }
                            },
                            "times": {
                                "atLeast": 1,
                                "atMost": 1
                            }
                        }"""));

        assertThat(
                "Exception for expected verify body Schema check",
                exception.getMessage(),
                StringContains.containsString("Schema check in actual requests FAILED"));

    }

    @Test
    void verifyValidation_bodySchemaOtherCase() {
        mocksApi.createMock(universalMock);

        api.sendPost("/api/post",
                        """
                                {"id": 3}""")
                .seeResponseCodeIs(200);

        Throwable exception = assertThrows(AssertionFailedError.class, () ->
                mocksApi.verifyMock("""
                        {
                            "httpRequest": {
                                "method": "POST",
                                "path": "/api/post",
                                "body": {
                                    "JSONschema": {
                                        "type": "object",
                                        "properties": {
                                             "id_wrong": {}
                                        },
                                        "additionalProperties": false
                                    }
                                }
                            },
                            "times": {
                                "atLeast": 1,
                                "atMost": 1
                            }
                        }"""));

        assertThat(
                "Exception for expected verify body Schema check (not case-sensitive)",
                exception.getMessage(),
                StringContains.containsString("Schema check in actual requests FAILED"));
    }
}
