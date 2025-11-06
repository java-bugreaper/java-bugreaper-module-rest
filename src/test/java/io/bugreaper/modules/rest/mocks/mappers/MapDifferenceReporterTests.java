package io.bugreaper.modules.rest.mocks.mappers;

import io.bugreaper.modules.mocks.mappers.MapDifferenceReporter;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static io.bugreaper.core.filereaders.FileReader.readTextFromFile;
import static io.bugreaper.modules.mocks.mappers.MapDifferenceReporter.jsonDifLogic;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class MapDifferenceReporterTests {

    @Test
    void staticClass() throws NoSuchMethodException {
        Constructor<MapDifferenceReporter> constructor = MapDifferenceReporter.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        InvocationTargetException thrown = assertThrows(InvocationTargetException.class, constructor::newInstance);

        Throwable cause = thrown.getCause();
        assert (cause instanceof IllegalStateException);
        assert ("Utility class".equals(cause.getMessage()));
    }

    @Test
    void checkNotStrictPass() {
        var er = """
                {
                  "id": 1
                }""";
        String result = jsonDifLogic(er, er, false);

        assertEquals(
                readTextFromFile("expectedReports/checkNotStrictPass"),
                result,
                "Report for not strict PASS");
    }

    @Test
    void checkNotStrictPassExtensive() {
        var er = """
                {
                  "id": 1
                }""";
        var ar = """
                {
                  "id": 1,
                  "data": "dummy"
                }""";
        String result = jsonDifLogic(er, ar, false);

        assertEquals(
                readTextFromFile("expectedReports/checkNotStrictPass"),
                result,
                "Report for not strict extensive PASS");
    }

    @Test
    void checkNoHeadersExample() {
        var er = """
                {
                  "headers": {
                      "first": [
                         "num_1"
                      ],
                      "second": [
                         "num_2"
                      ]
                  }
                }""";
        var ar = "{}";

        String result = jsonDifLogic(er, ar, false);

        assertEquals(
                readTextFromFile("expectedReports/checkNoHeadersExample"),
                result,
                "Report for missing headers");
    }

    @Test
    void checkNotStrictArrayPass() {
        var er = """
                {
                  "id": 1,
                  "array": [1, 2, 3]
                }""";
        String result = jsonDifLogic(er, er, false);

        assertEquals(
                readTextFromFile("expectedReports/checkNotStrictPass"),
                result,
                "Report for not strict with array PASS");
    }

    @Test
    void checkNotStrictArrayExtensivePass() {
        var er = """
                {
                  "id": 1,
                  "array": [1, 2, 3]
                }""";

        var ar = """
                {
                  "id": 1,
                  "array": [1, 2, 3, 4, "dummy"]
                }""";
        String result = jsonDifLogic(er, ar, false);

        assertEquals(
                readTextFromFile("expectedReports/checkNotStrictPass"),
                result,
                "Report for not strict with array extensive PASS");
    }

    @Test
    void checkNotStrictDataDiffs() {
        var er = """
                {
                  "id": 1,
                  "data": {
                    "test1": "num1",
                    "test2": "num2"
                  }
                }""";

        var ar = """
                {
                  "id": 2,
                  "data": {
                    "test1": "num1",
                    "test2": "num3"
                  }
                }""";


        String result = jsonDifLogic(er, ar, false);

        assertEquals(
                readTextFromFile("expectedReports/checkNotStrictDataDiffs"),
                result,
                "Report for not strict data diffs");
    }

    @Test
    void checkNotStrictDataMissing() {
        var er = """
                {
                  "id": 1,
                  "data": {
                    "test1": "num1",
                    "test2": "num2"
                  }
                }""";

        var ar = """
                {
                  "id": 1,
                  "data": {
                    "test1": "num1"
                  }
                }""";


        String result = jsonDifLogic(er, ar, false);

        assertEquals(
                readTextFromFile("expectedReports/checkNotStrictDataMissing"),
                result,
                "Report for not strict data missing");
    }

    @Test
    void checkNotStrictArrayMiss() {
        var er = """
                {
                  "id": 1,
                  "array": [1, 2, 3]
                }""";
        var ar = """
                {
                  "id": 1,
                  "array": [1, 2]
                }""";

        String result = jsonDifLogic(er, ar, false);

        assertEquals(
                readTextFromFile("expectedReports/checkNotStrictArrayMiss"),
                result,
                "Report for not strict Array data miss");
    }

    @Test
    void checkNotStrictArrayDiffs() {
        var er = """
                {
                  "id": 1,
                  "array": [1, 3]
                }""";
        var ar = """
                {
                  "id": 1,
                  "array": [1, 2]
                }""";

        String result = jsonDifLogic(er, ar, false);

        assertEquals(
                readTextFromFile("expectedReports/checkNotStrictArrayDiffs"),
                result,
                "Report for not strict Array data diffs");
    }

    @Test
    void checkStrictExtensive() {
        var er = """
                {
                  "id": 1
                }""";

        var ar = """
                {
                  "id": 2,
                  "data": {
                    "test1": "num1"
                  }
                }""";


        String result = jsonDifLogic(er, ar, true);

        assertEquals(
                readTextFromFile("expectedReports/checkStrictExtensive"),
                result,
                "Report for strict extensive data diffs");
    }

    @Test
    void checkBrokenJsonException() {
        var er = """
                {
                  "id": 1
                """;

        Throwable exception = assertThrows(IllegalArgumentException.class, () ->
                jsonDifLogic(er, er, false));


        MatcherAssert.assertThat(
                "Exception for wronj Json",
                exception.getMessage(),
                StringContains.containsString("Not valid Json for mapping"));

    }

}
