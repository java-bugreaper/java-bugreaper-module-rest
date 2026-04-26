/*
 *  Copyright 2025 Oleksii Betin
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package net.bugreaper.modules.mocks.enchanted;

import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.Objects;

import static net.bugreaper.modules.mocks.allurereporter.AllureBuilder.reportSummaryTableBuilder;
import static net.bugreaper.modules.mocks.enchanted.GetExpected.assertRequestsCountText;
import static org.junit.jupiter.api.Assertions.fail;


@SuppressWarnings("squid:S5960")
public class SummaryReportForVerify extends EnchantedSetup {


    private SummaryReportForVerify() {
        throw new IllegalStateException("Utility class");
    }


    public static void summaryReport(int actualRequestsCount,
                                     String[][] baseCheckResult,
                                     String[][] bodyCheckResult,
                                     String[][] headerCheckResult,
                                     String[][] queryParamsCheckResult,
                                     JSONObject expectedMockSetup) {


        String[][] allCheckResults = createAllChecksResult(actualRequestsCount, baseCheckResult, bodyCheckResult, headerCheckResult, queryParamsCheckResult);
        int passNum = getAllPassedCount(actualRequestsCount, allCheckResults);

        String expectedText = assertRequestsCountText(expectedMockSetup);

        // assertion table to allure report
        reportSummaryTableBuilder(
                expectedText,
                actualRequestsCount,
                passNum,
                baseCheckResult,
                bodyCheckResult,
                headerCheckResult,
                queryParamsCheckResult,
                allCheckResults);

        // assertion table to log
        if (logger_mer.isInfoEnabled()) {
            logFormatter(baseCheckResult,
                    bodyCheckResult,
                    headerCheckResult,
                    queryParamsCheckResult,
                    allCheckResults,
                    actualRequestsCount);
        }

        fail(MessageFormat.format("""
                Count of expected mock request(s) not match.
                Expected {0} with AR <{1}>
                Check report for more info""", expectedText, passNum));

    }

    private static String[][] createAllChecksResult(
            int cnt,
            String[][] baseCheckResult,
            String[][] bodyCheckResult,
            String[][] headerCheckResult,
            String[][] queryParamsCheckResult) {

        String[][] allResult = new String[cnt][];

        //to do separate to method for count
        for (int i = 0; i < cnt; i++) {
            String[] add;
            if (
                    (Objects.equals(baseCheckResult[i][1], PASS) || Objects.equals(baseCheckResult[i][1], SKIP))
                            &&
                            (Objects.equals(bodyCheckResult[i][1], PASS) || Objects.equals(bodyCheckResult[i][1], SKIP))
                            &&
                            (Objects.equals(headerCheckResult[i][1], PASS) || Objects.equals(headerCheckResult[i][1], SKIP))
                            &&
                            (Objects.equals(queryParamsCheckResult[i][1], PASS) || Objects.equals(queryParamsCheckResult[i][1], SKIP))
            ) {
                add = new String[]{String.valueOf(i + 1), PASS};
            } else {
                add = new String[]{String.valueOf(i + 1), "failed"};
            }
            allResult[i] = add;
        }
        return allResult;
    }

    private static int getAllPassedCount(
            int cnt,
            String[][] allResult) {

        int passCount = 0;
        //to do separate to method for count
        for (int i = 0; i < cnt; i++) {
            if (Objects.equals(allResult[i][1], "passed")) {
                passCount = passCount + 1;
            }
        }
        return passCount;
    }


    private static void logFormatter(String[][] baseCheckResult,
                                     String[][] bodyCheckResult,
                                     String[][] headerCheckResult,
                                     String[][] queryParamsCheckResult,
                                     String[][] allCheckResults,
                                     int actualRequestsCount) {

        try {

            String format = "%-5s %-12s %-12s %-12s %-12s %-10s%n";
            String space = "----------";

            String head = String.format(format, "№", "method/path", BODY_KEY, HEADERS_KEY, "queryParams", "all asserts");

            String separator = String.format(format, "---", space, space, space, space, space);

            StringBuilder tableLogBody = new StringBuilder();
            for (int i = 0; i < actualRequestsCount; i++) {

                tableLogBody.append(String.format(format,
                        bodyCheckResult[i][0],
                        baseCheckResult[i][1], bodyCheckResult[i][1],
                        headerCheckResult[i][1], queryParamsCheckResult[i][1],
                        getFinalAssertColored(allCheckResults[i][1])));
            }

            logger_mer.info("\nMock verify assertions:\n{}{}{}", head, separator, tableLogBody);

        } catch (Exception e) {
            logger_mer.error("ERROR while build Mock verify assertions log", e);
        }
    }


    private static String getFinalAssertColored(String assertResult) {
        final String ANSI_RESET = "\u001B[0m";
        final String ANSI_RED = "\u001B[31m";
        final String ANSI_GREEN = "\u001B[32m";

        String color;
        if (Objects.equals(assertResult, PASS)) {
            color = ANSI_GREEN;
        } else {
            color = ANSI_RED;
        }

        return color + assertResult + ANSI_RESET;
    }
}
