package io.bugreaper.modules.rest.mocks.logs;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

import helpers.MemoryAppender;
import io.bugreaper.modules.rest.PreSetup;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;
import org.slf4j.LoggerFactory;


import static io.bugreaper.core.assertions.Asserts.assertBooleans;
import static io.bugreaper.modules.api.assertable.response.ResponseOperators.statusCode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CheckEnchantedInfoLogsTests extends PreSetup {

    private static final MemoryAppender memoryAppender = new MemoryAppender();
    private static final String LOGGER_NAME = "MockEnchantedReport";


    @BeforeEach
    void setup() {
        Logger logger = (Logger) LoggerFactory.getLogger(LOGGER_NAME);
        logger.setLevel(Level.INFO);
        logger.addAppender(memoryAppender);

        memoryAppender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
        memoryAppender.start();
    }

    @Test
    void SummaryTableInfoLogTest() {
        mocksApi.createMock(universalMock);

        api.sendPost("/api/post",
                        """
                                {
                                "id": 3,
                                "text": "some text"
                                }""")
                .shouldHave(statusCode(200));

        api.sendPost("/api/post",
                        """
                                {
                                "id_wrong":"text",
                                }""")
                .shouldHave(statusCode(200));

        api.sendPost("/api/post",
                        """
                                {
                                "id_wrong": 4,
                                }""")
                .shouldHave(statusCode(200));


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

        String expectedTable = """
                Mock verify assertions:
                №     method/path  body         headers      all asserts
                ---   ----------   ----------   ----------   ----------
                1     passed       failed       skipped      \u001B[31mfailed\u001B[0m
                2     passed       failed       skipped      \u001B[31mfailed\u001B[0m
                3     passed       passed       skipped      \u001B[32mpassed\u001B[0m""";


        assertThat(
                "Check INFO log table",
                memoryAppender.getLoggedEvents().toString(),
                StringContains.containsString(expectedTable));

        assertBooleans(memoryAppender.contains(expectedTable, Level.INFO), true);
    }

}
