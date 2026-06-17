package net.bugreaper.modules.rest.mocks.logs;

import ch.qos.logback.classic.Level;

import com.fasterxml.jackson.databind.JsonNode;
import net.bugreaper.core.utils.AllureAssert;
import net.bugreaper.core.utils.AllureResultLoader;
import net.bugreaper.core.utils.LogWatcher;
import net.bugreaper.modules.rest.PreSetup;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.*;
import org.opentest4j.AssertionFailedError;


import static net.bugreaper.core.filereaders.FileReader.readTextFromFile;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CheckEnchantedInfoLogsTests extends PreSetup {


    private LogWatcher logWatcher;
    @BeforeEach
    void setup() {
        logWatcher = new LogWatcher("MockEnchantedReport", Level.INFO);
    }

    @AfterEach
    void teardown() {
        logWatcher.detach();
    }


    @Test
    @Order(1)
    void SummaryTableInfoLogTest() {
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
                                "id_wrong":"text",
                                }""")
                .seeResponseCodeIs(200);

        api.sendPost("/api/post",
                        """
                                {
                                "id_wrong": 4,
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
                                             "type": "integer",
                                             }
                                        },
                                        "additionalProperties": false,
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
                [INFO]\s
                Mock verify assertions:
                №     method/path  body         headers      queryParams  all asserts
                ---   ----------   ----------   ----------   ----------   ----------
                1     passed       failed       skipped      skipped      \u001B[31mfailed\u001B[0m
                2     passed       failed       skipped      skipped      \u001B[31mfailed\u001B[0m
                3     passed       passed       skipped      skipped      \u001B[32mpassed\u001B[0m
                ]""";


        assertThat(
                "Check INFO log table",
                logWatcher.getLoggedEvents(Level.INFO).toString(),
                StringContains.containsString(expectedTable));


        assertThat(
                logWatcher.getLoggedEvents(Level.INFO).toString(),
                StringContains.containsString("[INFO] No <headers> in verify setup : this check will be skipped"));

        assertThat(
                logWatcher.getLoggedEvents(Level.INFO).toString(),
                StringContains.containsString("[INFO] No <query params> in verify setup : this check will be skipped"));
    }

    @Test
    @Order(2)
    void allureForListCheck() {
        JsonNode result = AllureResultLoader.loadByTestName("SummaryTableInfoLogTest");

        AllureAssert.assertThat(result)
                .hasStep("(MOCK)[VERIFY] Verify mock")
                .hasSubStep("[MOCK-REPORT]: Enchanted mock verify report")
                .hasSubStep("Check assert for method and/or path")

                .hasAttachment("Expected CONTAINS method and/or path")
                .hasAttachment("Expected CONTAINS method and/or path", """
                        {
                          "path": "/api/post",
                          "method": "POST"
                        }""")

                .hasAttachment("Actual list: method and/or path")

                .hasAttachment("CONTAINS method and/or path №1 passed:","""
                        
                        Actual method and/or path
                        {
                          "path": "/api/post",
                          "method": "POST"
                        }
                        ========================
                        """)
                .hasAttachment("CONTAINS method and/or path №2 passed:")
                .hasAttachment("CONTAINS method and/or path №3 passed:")



                .hasStep("(MOCK)[VERIFY] Verify mock")
                .hasSubStep("[MOCK-REPORT]: Enchanted mock verify report")
                .hasSubStep("Check assert for jsonSchema")
                .hasAttachment("jsonSchema validate №1 failed:","""
                        
                        Actual body
                        {
                          "id": 3,
                          "text": "some text"
                        }
                        ========================
                        $.id: is not defined in the schema and the schema does not allow additional properties
                        $.text: is not defined in the schema and the schema does not allow additional properties
                        """)
                .hasAttachment("jsonSchema validate №2 failed:","""
                        
                        Actual body
                        {"id_wrong": "text"}
                        ========================
                        $.id_wrong: string found, integer expected
                        """)

                .hasAttachment("jsonSchema validate №3 passed:","""
                        
                        Actual body
                        {"id_wrong": 4}
                        ========================
                        Schema check passed""")

                .hasStep("(MOCK)[VERIFY] Verify mock")
                .hasSubStep("[MOCK-REPORT]: Enchanted mock verify report")
                .hasSubStep("No <headers> in verify setup : this check will be skipped")

                .hasStep("(MOCK)[VERIFY] Verify mock")
                .hasSubStep("[MOCK-REPORT]: Enchanted mock verify report")
                .hasSubStep("No <query params> in verify setup : this check will be skipped")

                .hasStep("(MOCK)[VERIFY] Verify mock")
                .hasSubStep("[MOCK-REPORT]: Enchanted mock verify report")
                .hasAttachment("Summary Table", readTextFromFile("allure/SummaryTableInfoLogTest.html"));
    }

}
