package io.bugreaper.modules.mocks.asserts;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.ValidationMessage;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import static io.bugreaper.modules.mocks.enchanted.EnchantedSetup.BODY_ABSENT_KEY;
import static io.bugreaper.modules.mocks.enchanted.EnchantedSetup.BODY_WRONG_KEY;

public class JsonAsserts {

    private static final Logger logger = LoggerFactory.getLogger(JsonAsserts.class);

    private JsonAsserts() {
        throw new IllegalStateException("Utility class");
    }

    public static void assertJsonNotEqual(String expectedAct, String actualJson) {
        assertJsonNotEqualMethod(expectedAct, actualJson, false);
    }

    private static void assertJsonNotEqualMethod(String expectedAct, String actualJson, Boolean strict) {
        try {
            JSONAssert.assertNotEquals(expectedAct, actualJson, strict);
        } catch (JSONException e) {
            throw new IllegalArgumentException(e);
        }
    }

    //if no assert error will be null
    public static String assertSchemaMethod(String expectedSchema, String actualJson, JsonSchemaFactory factory, boolean returnReport) {

        JsonSchema jsonSchema = factory.getSchema(expectedSchema);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode;

        try {
            jsonNode = objectMapper.readTree(actualJson);
        } catch (JsonProcessingException e) {
            String notJsonMessage = "Schema assert failed (Actual body not JSON)";
            if (returnReport) {
                return notJsonMessage;
            }
            throw new AssertionError(notJsonMessage);
        }

        //part for MockEnchantedDiffer
        if (jsonNode.has(BODY_WRONG_KEY)) {
            String wrongBodyMessage = "Schema assert failed (Actual body not JSON)";
            if (returnReport) {
                return wrongBodyMessage;
            }
            throw new AssertionError(wrongBodyMessage);
        } else if (jsonNode.has(BODY_ABSENT_KEY)) {
            String absentBodyMessage = "Schema assert failed (Actual body absent)";
            if (returnReport) {
                return absentBodyMessage;
            }
            throw new AssertionError(absentBodyMessage);
        }


        Set<ValidationMessage> errors = jsonSchema.validate(jsonNode);

        if (errors.isEmpty()) {
            logger.debug("Schema passed");
            return null;
        } else {
            StringBuilder message = new StringBuilder();

            for (ValidationMessage oneAssert : errors) {
                message.append(oneAssert).append("\n");
            }

            if (returnReport) {
                return message.toString();
            }
            throw new AssertionError(message);

        }
    }
}
