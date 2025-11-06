package io.bugreaper.modules.rest.mocks.mappers;

import org.junit.jupiter.api.Test;

import java.util.List;

import static io.bugreaper.core.filereaders.FileReader.readTextFromFile;
import static io.bugreaper.modules.mocks.enchanted.EnchantedAllureLogic.differenceContent;
import static io.bugreaper.modules.mocks.enchanted.GetActual.getActualBodiesList;
import static org.junit.jupiter.api.Assertions.assertEquals;


class MockEnchantedDifferReportTests {

    @Test
    void contentActualBodyContainsAbsent() {

        String expectedObject = """ 
                {
                   "id": 1
                }""";

        String actualContent = """
                {
                  "ABSENT_JSON_REPLACED": "true"
                }""";


        String result = differenceContent("body", expectedObject, actualContent, false);

        assertEquals(
                readTextFromFile("diffContentExpected/contentActualBodyAbsent"),
                result,
                "Content with: contains check, actual body absent");

    }

    @Test
    void contentActualBodyContainsWrong() {

        String expectedObject = """ 
                {
                   "id": 1
                }""";

        String actualContent = """
                {
                  "NOT_JSON_BODY_REPLACED": "true"
                }""";


        String result = differenceContent("body", expectedObject, actualContent, false);

        assertEquals(
                readTextFromFile("diffContentExpected/contentContainsActualBodyNotJson"),
                result,
                "Content with: contains check, actual body absent");

    }

    @Test
    void contentActualBodyEqualsAbsent() {

        String expectedObject = """ 
                {
                   "id": 1
                }""";


        String actualContent = """
                {
                  "ABSENT_JSON_REPLACED": "true"
                }""";


        String result = differenceContent("body", expectedObject, actualContent, true);

        assertEquals(
                readTextFromFile("diffContentExpected/contentActualBodyAbsent"),
                result,
                "Content with: contains check, actual body absent");

    }

    @Test
    void contentContainsActualBodyNotJson_skipped() {

        String expectedObject =
                """ 
                            {
                               "id": 1
                            }
                        """;

        String allMockRequestsString = """ 
                [
                {
                     "body": {
                        "type" : "STRING",
                        "string" : "some_string"
                     }
                }
                ]
                """;
        List<String> actualBodiesList = getActualBodiesList(allMockRequestsString, 1);

        String result = differenceContent("body", expectedObject, actualBodiesList.get(0), false);

        assertEquals(
                readTextFromFile("diffContentExpected/contentContainsActualBodyNotJson"),
                result,
                "Content with: contains check, actual body not JSON(transform)");

    }

    @Test
    void contentContainsJsonDiffs() {

        String expectedObject = """ 
                {
                     "id": 1,
                     "test": "text"
                }
                """;

        String actualContent = """ 
                {
                  "id": 2
                }
                """;


        String result = differenceContent("body", expectedObject, actualContent, false);

        assertEquals(
                readTextFromFile("diffContentExpected/contentContainsJsonDiffs"),
                result,
                "Content with: contains check");

    }

    @Test
    void contentContainsJsonDiffsPass() {

        String expectedObject = """ 
                {
                     "id": 1
                }
                """;

        String actualContent = """ 
                {
                  "id": 1,
                  "test": "text"
                }
                """;


        String result = differenceContent("body", expectedObject, actualContent, false);

        assertEquals(
                readTextFromFile("diffContentExpected/contentContainsJsonDiffsPass"),
                result,
                "Content with: contains check pass");

    }
}
