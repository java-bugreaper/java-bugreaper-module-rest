package net.bugreaper.modules.mocks.mappers;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;

import java.util.Map;

import static net.bugreaper.core.mappers.JsonObjectMappers.convertJsonToMap;

public final class MapDifferenceReporter {

    private MapDifferenceReporter() {
        throw new IllegalStateException("Utility class");
    }

    public static String jsonDifLogic(String expectedJson, String actualJson, boolean isStrict) {

        Map<String, Object> leftMap = convertJsonToMap(expectedJson);
        Map<String, Object> rightMap = convertJsonToMap(actualJson);

        Map<String, Object> leftFlatMap = MapDiffMapper.flatten(leftMap);
        Map<String, Object> rightFlatMap = MapDiffMapper.flatten(rightMap);

        MapDifference<String, Object> difference = Maps.difference(leftFlatMap, rightFlatMap);

        StringBuilder diffResult = new StringBuilder();

        Map<String, Object> entriesOnlyOnLeft = difference.entriesOnlyOnLeft();


        if(!entriesOnlyOnLeft.isEmpty()) {
            diffResult.append("\nMissing data in Actual Result:\n");

            for (var entry : entriesOnlyOnLeft.entrySet()) {
                diffResult.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
        }

        //check what is difference in existing data
        Map<String, MapDifference.ValueDifference<Object>> entriesDiffering = difference.entriesDiffering();

        if(!entriesDiffering.isEmpty()) {
            diffResult.append("\nNot expected values in Actual Result:\n");
            for (var entry : entriesDiffering.entrySet()) {
                diffResult.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
        }

        //check what is not expected exist in actual result (only for STRICT verify)
        if (isStrict) {
            diffResult.append("\nExtensive data in Actual Result (for strict match):\n");
            Map<String, Object> entriesOnlyOnRight = difference.entriesOnlyOnRight();
            for (var entry : entriesOnlyOnRight.entrySet()) {
                diffResult.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
        }

        return diffResult.toString();
    }

}
