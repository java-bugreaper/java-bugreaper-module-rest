package net.bugreaper.modules.rest.mocks.allurereporter;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static net.bugreaper.modules.mocks.allurereporter.AllureBuilder.reportSummaryTableBuilder;
import static java.nio.file.Files.readString;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AllureReporterSummaryTableTests {

    final String filePath = "src/test/resources/";

    @Test
    void testReportSummaryTableBuilder() throws IOException {

        String expectedText = "Exactly <2>";
        int actualRequestsCount = 2;
        int allPass = 1;

        String[][] baseCheckResult = new String[actualRequestsCount][];
        baseCheckResult[0] = new String[]{"1", "passed"};
        baseCheckResult[1] = new String[]{"2", "passed"};

        String[][] bodyCheckResult = new String[actualRequestsCount][];
        bodyCheckResult[0] = new String[]{"1", "passed"};
        bodyCheckResult[1] = new String[]{"2", "failed"};

        String[][] headerCheckResult = new String[actualRequestsCount][];
        headerCheckResult[0] = new String[]{"1", "skipped"};
        headerCheckResult[1] = new String[]{"2", "skipped"};

        String[][] allCheckResults = new String[actualRequestsCount][];
        allCheckResults[0] = new String[]{"1", "passed"};
        allCheckResults[1] = new String[]{"2", "failed"};

        assertEquals(readString(Path.of(filePath + "allure/summary_report.html")),
                reportSummaryTableBuilder(
                        expectedText,
                        actualRequestsCount,
                        allPass,
                        baseCheckResult,
                        bodyCheckResult,
                        headerCheckResult,
                        allCheckResults),
                "HTML summary report");


    }

    @Test
    void testReportSummaryTableBuilderNegativeSize() {

        assertEquals("Error while create attachment",
                reportSummaryTableBuilder(
                        "text",
                        2,
                        2,
                        new String[1][],
                        new String[1][],
                        new String[1][],
                        new String[1][]),
                "HTML summary report on failed");

    }

    @Test
    void testReportSummaryTableBuilderNegativeNull() {

        int actualRequestsCount = 2;

        String[][] baseCheckResult = new String[actualRequestsCount][];
        baseCheckResult[0] = new String[]{"1", "passed"};
        baseCheckResult[1] = new String[]{"2", "passed"};

        String[][] bodyCheckResult = new String[actualRequestsCount][];
        bodyCheckResult[0] = null;
        bodyCheckResult[1] = new String[]{"2", "failed"};

        assertEquals("Error while create attachment",
                reportSummaryTableBuilder(
                        "text",
                        2,
                        2,
                        baseCheckResult,
                        bodyCheckResult,
                        bodyCheckResult,
                        bodyCheckResult),
                "HTML summary report on failed");

    }

}
