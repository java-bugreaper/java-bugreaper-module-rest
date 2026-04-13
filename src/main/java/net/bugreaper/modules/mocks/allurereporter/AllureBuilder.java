package net.bugreaper.modules.mocks.allurereporter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static net.bugreaper.core.allurereporter.AllureReporter.createHtmlAllureAttachment;
import static net.bugreaper.core.mappers.StringMappers.stringMapper;
import static net.bugreaper.core.mappers.StringMappers.stringMapperV2;


public class AllureBuilder {

    private AllureBuilder() {
        throw new IllegalStateException("Utility class");
    }

    private static final String ROW_END = "</tr>";
    private static final String HEADER_END = "</th>";
    private static final String CELL_END = "</td>";
    private static final Logger logger = LoggerFactory.getLogger(AllureBuilder.class);

    @SuppressWarnings("java:S107")
    public static String reportSummaryTableBuilder(
            String expectedText,
            int actualRequestsCount,
            int allPassedCount,
            String[][] baseCheckResult,
            String[][] bodyCheckResult,
            String[][] headerCheckResult,
            String[][] queryParamsCheckResult,
            String[][] allCheckResults) {

        try {

            StringBuilder columns = new StringBuilder();
            StringBuilder dataRows = new StringBuilder();

            //create header with numbers & base checks

            String summaryCell = stringMapper("""
                            Expected verify: ${expectedText}<br>
                            All requests: <${actualRequestsCount}><br>
                            Passed verify: <${allPassedCount}>""",
                    Map.of(
                            "expectedText", expectedText,
                            "actualRequestsCount", actualRequestsCount,
                            "allPassedCount", allPassedCount
                    ));


            columns.append("<th style=\"font-weight: normal;\">\n").append(summaryCell).append(HEADER_END);

            for (int i = 0; i < actualRequestsCount; i++) {
                columns.append("<th>n").append(baseCheckResult[i][0]).append(HEADER_END);
            }

            //create method/path checks
            dataRows.append(assertRow("method/path", baseCheckResult));

            //create body checks
            dataRows.append(assertRow("body", bodyCheckResult));

            //create headers checks
            dataRows.append(assertRow("headers", headerCheckResult));

            //create headers checks
            dataRows.append(assertRow("query params", queryParamsCheckResult));

            //create full assert summary
            dataRows.append(assertRow("Full assert", allCheckResults));

            // Allure attachment
            var attachment = reportSummaryTable(columns, dataRows);
            createHtmlAllureAttachment("Summary Table", attachment);

            return attachment;
        } catch (Exception e) {
            final String ALLURE_ERROR_MESSAGE = "Error while create attachment";
            logger.error(ALLURE_ERROR_MESSAGE, e);

            return ALLURE_ERROR_MESSAGE;
        }
    }

    private static StringBuilder assertRow(String rowName, String[][] rowArrayData) {

        String passCell = "<td style=\"background-color: #c2f0e7;\">";
        String failedCell = "<td style=\"background-color: #f0c2cb;\">";

        StringBuilder dataRows = new StringBuilder();
        dataRows.append("<tr>");

        dataRows.append("<td id=\"stick-column\">").append(rowName).append(CELL_END);

        for (String[] cellData : rowArrayData) {

            //save from empty array
            if (cellData == null) {
                throw new NullPointerException("Array element is null in row: " + rowName);
            }
            switch (cellData[1]) {
                case "passed":
                    dataRows.append(passCell);
                    break;
                case "failed":
                    dataRows.append(failedCell);
                    break;
                default:
                    dataRows.append("<td>");
            }

            dataRows.append(cellData[1]).append(CELL_END);

        }
        dataRows.append(ROW_END + "\n");
        return dataRows;
    }



    private static String reportSummaryTable(StringBuilder columns,
                                             StringBuilder dataRows) {

        return
                stringMapperV2("""
                                <html title="test title">
                                <head>
                                <style>
                                                    table {
                                                        width: 100%;
                                                        margin-bottom: 1rem;
                                                        color: #111314;
                                                        border-collapse: collapse;
                                                        font-size: 12;
                                                    }
                                                    table td, table th {
                                                        vertical-align: center;
                                                        text-align:center;
                                                        border: 1px solid #dee2e6;
                                                    }
                                                    table th{
                                                        position: sticky;
                                                        top: 0;
                                                        background-color:#FBFCFC!important;
                                                    }
                                                    #stick-column {
                                                        position: sticky;
                                                        left: 0;
                                                        font-weight: bold;
                                                    }
                                </style>
                                </head>
                                <body>
                                <table>
                                <tr>
                                $${columns}</tr>
                                $${dataRows}</table>
                                </body>
                                </html>""",
                        Map.of(
                                "columns", columns,
                                "dataRows", dataRows
                        ));
    }

}
