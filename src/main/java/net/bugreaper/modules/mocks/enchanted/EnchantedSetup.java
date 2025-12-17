package net.bugreaper.modules.mocks.enchanted;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class EnchantedSetup {

    EnchantedSetup() {
        throw new IllegalStateException("Utility class");
    }

    protected static final Logger logger_mer = LoggerFactory.getLogger("MockEnchantedReport");


    protected static final List<String> BASE_FIELDS = List.of("method", "path");
    protected static final List<String> FROM_TO_FIELDS = List.of("atLeast", "atMost");

    protected static final String REQUEST_KEY = "httpRequest";
    protected static final String HEADERS_KEY = "headers";
    protected static final String RAW_BODY_KEY = "rawBody";
    protected static final String BODY_KEY = "body";
    protected static final String JSON_KEY = "json";
    protected static final String SCHEMA_KEY = "jsonSchema";

    protected static final String PASS = "passed";
    protected static final String SKIP = "skipped";

    public static final String BODY_WRONG_KEY = "NOT_JSON_BODY_REPLACED";
    public static final String BODY_ABSENT_KEY = "ABSENT_JSON_REPLACED";

    protected static final String SKIPPED_MESSAGE = "No <%s> in verify setup : this check will be skipped";
    protected static final String REPLACE_ABSENT_MESSAGE = "Actual {} will be replaced by enum object because it's not present:\n{}";
    protected static final String NO_VALUE_MESSAGE = "No value for <{}> key";

}
