package net.bugreaper.modules.mocks.asserts;

import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static net.bugreaper.modules.mocks.asserts.JsonSchemaAsserts.assertSchemaMethod;

public final class ListAsserts {

    private ListAsserts() {
        throw new IllegalStateException("Utility class");
    }

    private static final Logger logger = LoggerFactory.getLogger(ListAsserts.class);
    private static final String LIST_LOG_MESSAGE = "Start assert for list:\n{}";
    private static final String ASSERT_LOG_MESSAGE = "Assert log:\n{}";


    public static void schemaCheckInList(String expectedSchema, List<String> actualBodiesList) {

        StringBuilder trace = listJsonSchemaBuilder(expectedSchema, actualBodiesList);

        if (trace != null) {
            throw new AssertionError(
                    String.format("There is no elements in the list with valid JSON Schema:%n%s%n%s", trace, expectedSchema));
        }

    }

    private static StringBuilder listJsonSchemaBuilder(String expectedSchema, List<String> actualList) {

        StringBuilder trace = traceInit(actualList);

        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4);

        int num = 0;
        for (String actual : actualList) {
            try {
                assertSchemaMethod(expectedSchema, actual, factory, false);
                return null;
            } catch (AssertionError | IllegalArgumentException ex) {
                num = num + 1;
                trace = traceNumBuilder(trace, num, ex.getMessage());
            }
        }

        return trace;
    }

    private static StringBuilder traceInit(List<String> actualList) {
        logger.debug(LIST_LOG_MESSAGE, actualList);
        return new StringBuilder();
    }

    private static StringBuilder traceNumBuilder(StringBuilder trace, int num, String exceptionString) {
        logger.debug(ASSERT_LOG_MESSAGE, exceptionString);
        return trace.append("\n-----------").append(num).append("\n").append(exceptionString).append("\n");
    }
}
