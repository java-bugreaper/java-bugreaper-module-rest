package net.bugreaper.modules.mocks.asserts;

import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.util.List;

import static net.bugreaper.core.assertions.JsonAsserts.assertJsonMethod;
import static net.bugreaper.modules.mocks.asserts.JsonAsserts.assertSchemaMethod;

public final class ListAssertsEvery {

    private ListAssertsEvery() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * @param expectedAct actual object
     * @param actualList  list with actual jsons
     * @param compareMode type of assert (STRICT, LENIENT, NON_EXTENSIBLE, STRICT_ORDER)
     * @return String[][] = [][№, passed/failed/skipped, actualObject]
     */
    public static String[][] everyJsonAsserBuilder(String expectedAct, List<String> actualList, JSONCompareMode compareMode) {

        final int REQUESTS_COUNT = actualList.size();

        String[][] result = new String[REQUESTS_COUNT][];

        for (int i = 0; i < REQUESTS_COUNT; i++) {
            String[] add;

            try {
                assertJsonMethod(expectedAct, actualList.get(i), compareMode);
                add = new String[]{String.valueOf(i + 1), "passed", actualList.get(i)};
            } catch (AssertionError ex) {
                add = new String[]{String.valueOf(i + 1), "failed", actualList.get(i)};
            }
            result[i] = add;
        }
        return result;
    }

    /**
     * @param expectedSchema schema for validation
     * @param actualList     list with actual jsons
     * @return String[][] = [][№, passed/failed, difference]
     */
    public static String[][] everyJsonSchemaAsserBuilder(String expectedSchema, List<String> actualList) {

        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4);

        final int REQUESTS_COUNT = actualList.size();

        String[][] result = new String[REQUESTS_COUNT][];

        for (int i = 0; i < REQUESTS_COUNT; i++) {
            String[] add;

            String oneAssertResult = assertSchemaMethod(expectedSchema, actualList.get(i), factory, true);

            if (oneAssertResult != null) {
                add = new String[]{String.valueOf(i + 1), "failed", oneAssertResult};
            } else {
                add = new String[]{String.valueOf(i + 1), "passed", "Schema check passed"};
            }

            result[i] = add;
        }
        return result;
    }


}
