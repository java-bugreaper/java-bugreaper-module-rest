package net.bugreaper.modules.rest.api;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.fasterxml.jackson.databind.JsonNode;
import net.bugreaper.core.utils.AllureAssert;
import net.bugreaper.core.utils.AllureResultLoader;
import net.bugreaper.modules.rest.PreSetup;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.stringContainsInOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LoggingTests extends PreSetup {

    private static final String LOGGER_NAME = "bugreaper-module-api";

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @Test
    void testLoggingOnDebugFlagOff() {
        mocksApi.createMock(universalMock);
        Logger logger = (Logger) LoggerFactory.getLogger(LOGGER_NAME);
        logger.setLevel(Level.DEBUG);


        System.setOut(new PrintStream(outContent));

        apiNoLogs.setLogging(false).sendGet("api/test").seeResponseCodeIs(200);

        MatcherAssert.assertThat(
                "Request printed",
                outContent.toString(),
                stringContainsInOrder(
                        "Request method:	GET",
                        "Request URI:", "/api/test"));

        MatcherAssert.assertThat(
                "Response printed",
                outContent.toString(),
                stringContainsInOrder(
                        "HTTP/1.1 200 OK",
                        "\"result\": \"ok\""));

        System.setOut(originalOut);
    }

    @Test
    void testLoggingOnInfoFlagOn() {
        mocksApi.createMock(universalMock);
        Logger logger = (Logger) LoggerFactory.getLogger(LOGGER_NAME);
        logger.setLevel(Level.INFO);

        System.setOut(new PrintStream(outContent));

        apiLogging.sendGet("api/test").seeResponseCodeIs(200);

        MatcherAssert.assertThat(
                "Request printed",
                outContent.toString(),
                stringContainsInOrder(
                        "Request method:	GET",
                        "Request URI:", "/api/test"));

        MatcherAssert.assertThat(
                "Response printed",
                outContent.toString(),
                stringContainsInOrder(
                        "HTTP/1.1 200 OK",
                        "\"result\": \"ok\""));

        System.setOut(originalOut);
    }

    @Test
    @Order(1)
    void testLoggingOnDefaultInfoOff() {
        mocksApi.createMock(universalMock);
        Logger logger = (Logger) LoggerFactory.getLogger(LOGGER_NAME);
        logger.setLevel(Level.INFO);

        System.setOut(new PrintStream(outContent));

        api.sendGet("api/test").seeResponseCodeIs(200);

        MatcherAssert.assertThat(
                "Request not printed",
                outContent.toString(),
                not(stringContainsInOrder(
                        "Request method:")));

        MatcherAssert.assertThat(
                "Response not printed",
                outContent.toString(),
                not(stringContainsInOrder(
                        "HTTP/1.1 200 OK")));

        System.setOut(originalOut);
    }

    @Test
    @Order(2)
    void allureForListCheck() {
        JsonNode result = AllureResultLoader.loadByTestName("testLoggingOnDefaultInfoOff");

        AllureAssert.assertThat(result)
                .hasStep("(API) Send GET api/test")
                .hasAttachment("Request")
                .hasAttachment("HTTP/1.1 200 OK")

                .hasStep("Status code is: 200");
    }


}
