package net.bugreaper.modules.rest;


import net.bugreaper.modules.api.logger.Log;
import net.bugreaper.modules.mocks.allurereporter.AllureBuilder;
import net.bugreaper.modules.mocks.asserts.JsonSchemaAsserts;
import net.bugreaper.modules.mocks.asserts.ListAsserts;
import net.bugreaper.modules.mocks.asserts.ListAssertsEvery;
import net.bugreaper.modules.mocks.enchanted.MockEnchantedDiffer;
import net.bugreaper.modules.mocks.enchanted.SummaryReportForVerify;
import net.bugreaper.modules.mocks.mappers.JsonMappersRest;
import net.bugreaper.modules.mocks.mappers.MapDiffMapper;
import net.bugreaper.modules.mocks.mappers.MapDifferenceReporter;
import org.junit.jupiter.api.Test;


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

class UtilityClassesTests {


    @Test
    void testUtilityLog() {
        assertUtilityClass(Log.class);
    }

    @Test
    void testUtilityAllureBuilder() {
        assertUtilityClass(AllureBuilder.class);
    }

    @Test
    void testUtilityJsonSchemaAsserts() {
        assertUtilityClass(JsonSchemaAsserts.class);
    }

    @Test
    void testUtilityListAsserts() {
        assertUtilityClass(ListAsserts.class);
    }

    @Test
    void testUtilityListAssertsEvery() {
        assertUtilityClass(ListAssertsEvery.class);
    }

    @Test
    void testUtilityMockEnchantedDiffer() {
        assertUtilityClass(MockEnchantedDiffer.class);
    }

    @Test
    void testUtilityMapDifferenceReporter() {
        assertUtilityClass(MapDifferenceReporter.class);
    }

    @Test
    void testUtilitySummaryReportForVerify() {
        assertUtilityClass(SummaryReportForVerify.class);
    }

    @Test
    void testUtilityJsonMappersRest() {
        assertUtilityClass(JsonMappersRest.class);
    }

    @Test
    void testUtilityMapDiffMapper() {
        assertUtilityClass(MapDiffMapper.class);
    }

    private static void assertUtilityClass(
            Class<?> utilityClass) {

        Constructor<?> constructor;
        try {
            constructor = utilityClass.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        constructor.setAccessible(true);

        InvocationTargetException thrown = assertThrows(
                InvocationTargetException.class,
                constructor::newInstance
        );

        Throwable cause = thrown.getCause();

        assertInstanceOf(IllegalStateException.class, cause);
        assertEquals("Utility class", cause.getMessage());
    }

}
