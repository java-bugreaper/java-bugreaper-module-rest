package net.bugreaper.modules.rest.mocks.logs;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

import helpers.MemoryAppender;
import net.bugreaper.modules.rest.PreSetup;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;
import org.slf4j.LoggerFactory;


import static net.bugreaper.core.assertions.Asserts.assertBooleans;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CheckEnchantedDebugLogsTests extends PreSetup {

    private static final MemoryAppender memoryAppender = new MemoryAppender();
    private static final String LOGGER_NAME = "MockEnchantedReport";


    @BeforeEach
    void setup() {
        Logger logger = (Logger) LoggerFactory.getLogger(LOGGER_NAME);
        logger.setLevel(Level.DEBUG);
        logger.addAppender(memoryAppender);

        memoryAppender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
        memoryAppender.start();
    }


    @Test
    void ActualBodyListInfoLogTest() {
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
                                "id_wrong": 4
                                }""")
                .seeResponseCodeIs(200);

        api.sendGet("/api/get")
                .seeResponseCodeIs(200);

        api.sendPost("/api/post",
                        """
                                {
                                "id_wrong":"text"
                                }""")
                .seeResponseCodeIs(200);

        apiNoType
                .sendPost("/api/admin")
                .seeResponseCodeIs(200);

        apiJsonPlus
                .sendPost("/api/post", "{}")
                .seeResponseCodeIs(200);

        apiText
                .sendPost("/api/post", "some string")
                .seeResponseCodeIs(200);


        mocksApi.createMock("""
                {
                    "httpRequest": {
                        "method": "POST",
                        "path": "/api/post-xml",
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

        apiXml
                .sendPost("/api/post-xml",
                        """
                                <request> <key>some_xml</key> </request>""")
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

        String expectedLog = """
                [
                {
                  "id": 3,
                  "text": "some text"
                }

                -----------

                {"id_wrong": 4}

                -----------

                {"ABSENT_JSON_REPLACED": "true"}

                -----------

                {"id_wrong": "text"}

                -----------

                {"ABSENT_JSON_REPLACED": "true"}

                -----------

                {}

                -----------

                {"NOT_JSON_BODY_REPLACED": "true"}

                -----------

                {"NOT_JSON_BODY_REPLACED": "true"}
                ]""";

        assertThat(
                "Check Actual list log table",
                memoryAppender.getLoggedEvents().toString(),
                StringContains.containsString(expectedLog));

        assertBooleans(memoryAppender.contains(expectedLog, Level.DEBUG), true);
    }

}
