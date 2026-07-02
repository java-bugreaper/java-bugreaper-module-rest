package net.bugreaper.modules.rest.mocks.logs;

import ch.qos.logback.classic.Level;

import com.fasterxml.jackson.databind.JsonNode;
import net.bugreaper.core.utils.AllureAssert;
import net.bugreaper.core.utils.AllureResultLoader;
import net.bugreaper.core.utils.LogWatcher;
import net.bugreaper.modules.rest.PreSetup;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Isolated;


import static net.bugreaper.core.filereaders.FileReader.readTextFromFile;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@Isolated
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CheckEnchantedDebugLogsTests extends PreSetup {

    private LogWatcher logWatcher;
    @BeforeEach
    void setup() {
        logWatcher = new LogWatcher("MockEnchantedReport", Level.DEBUG);
    }

    @AfterEach
    void teardown() {
        logWatcher.detach();
    }

    @Test
    @Order(1)
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


        Throwable exception = assertThrows(AssertionError.class, () ->
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
                logWatcher.getLoggedEvents(Level.DEBUG).toString(),
                StringContains.containsString(expectedLog));


        //INFO part

        assertThat(
                logWatcher.getLoggedEvents(Level.INFO).toString(),
                StringContains.containsString("[INFO] Actual body will be replaced because it's not JSON type: STRING"));

        assertThat(
                logWatcher.getLoggedEvents(Level.INFO).toString(),
                StringContains.containsString("[INFO] Actual body will be replaced because it's not JSON type: XML"));
    }

    @Test
    @Order(2)
    void allureForListCheck() {
        JsonNode result = AllureResultLoader.loadByTestName("ActualBodyListInfoLogTest");

        AllureAssert.assertThat(result)
                .hasStep("(MOCK)[VERIFY] Verify mock")
                .hasSubStep("[MOCK-REPORT]: Enchanted mock verify report")

                .hasSubStep("Check assert for method and/or path")
                .hasAttachment("CONTAINS method and/or path №3 failed:","""
                        
                        Actual method and/or path
                        {
                          "path": "/api/get",
                          "method": "GET"
                        }
                        ========================
                                                
                        Not expected values in Actual Result:
                        /path: (/api/post, /api/get)
                        /method: (POST, GET)
                        """)
                .hasAttachment("CONTAINS method and/or path №6 passed:")
                .hasAttachment("CONTAINS method and/or path №8 failed:")



                .hasStep("(MOCK)[VERIFY] Verify mock")
                .hasSubStep("[MOCK-REPORT]: Enchanted mock verify report")
                .hasSubStep("Check assert for jsonSchema")
                .hasAttachment("Expected schema","""
                        {
                          "additionalProperties": false,
                          "type": "object",
                          "properties": {"id_wrong": {"type": "integer"}}
                        }""")

                .hasAttachment("Actual list: body")
                .hasAttachment("jsonSchema validate №3 failed:","""
                        
                        Actual body
                        {"ABSENT_JSON_REPLACED": "true"}
                        ========================
                        Schema assert failed (Actual body absent)""")

                .hasAttachment("jsonSchema validate №6 passed:","""
                        
                        Actual body
                        {}
                        ========================
                        Schema check passed""")

                .hasAttachment("jsonSchema validate №8 failed:","""
                        
                        Actual body
                        {"NOT_JSON_BODY_REPLACED": "true"}
                        ========================
                        Schema assert failed (Actual body not JSON)""")

                .hasStep("(MOCK)[VERIFY] Verify mock")
                .hasSubStep("[MOCK-REPORT]: Enchanted mock verify report")
                .hasAttachment("Summary Table", readTextFromFile("allure/ActualBodyListInfoLogTest.html"));
    }

}
