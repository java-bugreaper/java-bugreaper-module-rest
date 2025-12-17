package net.bugreaper.modules.mocks.mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public final class MapDifferenceReporter {

    private MapDifferenceReporter() {
        throw new IllegalStateException("Utility class");
    }

    public static String jsonDifLogic(String expectedJson, String actualJson, boolean isStrict) {

        Map<String, Object> leftMap = jsonStringToMap(expectedJson);
        Map<String, Object> rightMap = jsonStringToMap(actualJson);

        Map<String, Object> leftFlatMap = MapDiffMapper.flatten(leftMap);
        Map<String, Object> rightFlatMap = MapDiffMapper.flatten(rightMap);

        MapDifference<String, Object> difference = Maps.difference(leftFlatMap, rightFlatMap);

        StringBuilder diffResult = new StringBuilder();

        diffResult.append("\nMissing data in Actual Result:\n");

        Map<String, Object> entriesOnlyOnLeft = difference.entriesOnlyOnLeft();

        for (var entry : entriesOnlyOnLeft.entrySet()) {
            diffResult.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }

        //check what is difference in existing data
        diffResult.append("\nNot expected values in Actual Result:\n");

        Map<String, MapDifference.ValueDifference<Object>> entriesDiffering = difference.entriesDiffering();

        for (var entry : entriesDiffering.entrySet()) {
            diffResult.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }


        //check what is not expected exist in actual result (only for STRICT verify
        if (isStrict) {
            diffResult.append("\nExtensive data in Actual Result (for strict match):\n");
            Map<String, Object> entriesOnlyOnRight = difference.entriesOnlyOnRight();
            for (var entry : entriesOnlyOnRight.entrySet()) {
                diffResult.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
        }

        return diffResult.toString();
    }

    private static HashMap<String, Object> jsonStringToMap(String json) {
        TypeReference<HashMap<String, Object>> type =
                new TypeReference<>() {
                };

        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(MessageFormat.format("Not valid Json for mapping\n{0}", json), e);
        }
    }

}
