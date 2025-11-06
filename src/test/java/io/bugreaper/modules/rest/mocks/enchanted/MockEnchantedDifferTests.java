package io.bugreaper.modules.rest.mocks.enchanted;


import io.bugreaper.modules.mocks.enchanted.MockEnchantedDiffer;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;


import static io.bugreaper.core.assertions.JsonAsserts.assertJson;
import static io.bugreaper.modules.mocks.enchanted.GetActual.getActualBodiesList;
import static io.bugreaper.modules.mocks.enchanted.GetActual.getActualHeadersList;
import static io.bugreaper.modules.mocks.enchanted.MockEnchantedDiffer.noRequestsCheck;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SuppressWarnings("java:S5976")
class MockEnchantedDifferTests {

    @Test
    void staticClass() throws NoSuchMethodException {
        Constructor<MockEnchantedDiffer> constructor = MockEnchantedDiffer.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        InvocationTargetException thrown = assertThrows(InvocationTargetException.class, constructor::newInstance);

        Throwable cause = thrown.getCause();
        assert (cause instanceof IllegalStateException);
        assert ("Utility class".equals(cause.getMessage()));
    }


    @Test
    void returnActualHeadersList() {

        String mocksRequests = """
                [
                    {
                         "body": {
                             "id": 1
                         },
                         "headers": {
                             "first": ["num_1"]
                         }
                    },
                    {
                         "body": {
                             "id": 2
                         },
                         "headers": {
                             "second": ["num_2"]
                         }
                    }
                ]""";

        List<String> headersList = getActualHeadersList(mocksRequests, 2);

        assertJson("""
                        {
                            "headers": {
                                "first": [
                                    "num_1"
                                ]
                            }
                        }""",
                headersList.get(0));

        assertJson("""
                        {
                            "headers": {
                                "second": [
                                    "num_2"
                                ]
                            }
                        }""",
                headersList.get(1));
    }

    @Test
    void returnActualHeadersEmptyList() {

        String mocksRequests = """
                [
                    {
                         "body": {
                             "id": 1
                         }
                    }
                ]""";

        List<String> headersList = getActualHeadersList(mocksRequests, 1);

        assertJson("""
                        {
                          "ABSENT_JSON_REPLACED": "true"
                        }""",
                headersList.get(0));
    }

    @Test
    void noRequestsCheckTest() {

        String mocksRequests = "[ ]";

        Throwable exception = assertThrows(AssertionFailedError.class, () ->
                noRequestsCheck(mocksRequests));

        MatcherAssert.assertThat(
                "Exception for no requests to mock-server",
                exception.getMessage(),
                StringContains.containsString("No requests to mock-server in test"));
    }


    @Test
    void returnActualWrongBodyReturnEnum() {

        String mocksRequests =
                """ 
                        [
                        {
                             "body": {
                                "type" : "STRING",
                                "string" : "some_string"
                             }
                        }
                        ]
                        """;

        List<String> bodiesList = getActualBodiesList(mocksRequests, 1);

        assertJson("""
                        {
                          "NOT_JSON_BODY_REPLACED": "true"
                        }""",
                bodiesList.get(0));
    }

    @Test
    void returnActualAbsentBody() {

        String mocksRequests =
                """ 
                        [
                            {
                                 "method": "POST"
                            }
                        ]
                        """;

        List<String> bodiesList = getActualBodiesList(mocksRequests, 1);

        System.out.println(bodiesList.get(0));

        assertJson("""
                        {
                          "ABSENT_JSON_REPLACED": "true"
                        }""",
                bodiesList.get(0));
    }

    @Test
    void returnActualBodyRaw() {

        String mocksRequests =
                """ 
                        [
                        {
                             "body": {
                                "id" : 777
                             }
                        }
                        ]
                        """;

        List<String> bodiesList = getActualBodiesList(mocksRequests, 1);

        assertJson("""
                        {
                          "id" : 777
                        }""",
                bodiesList.get(0));
    }

    @Test
    void returnActualBodyJsonType() {

        String mocksRequests =
                """ 
                        [
                        {
                            "body": {
                                "contentType": "application/json",
                                "type": "JSON",
                                "json": {
                                    "id": 8
                                },
                                "rawBytes": "ewogICJpZCI6IDgKfQ=="
                            }
                        }
                        ]
                        """;

        List<String> bodiesList = getActualBodiesList(mocksRequests, 1);

        assertJson("""
                        {
                          "id": 8
                        }""",
                bodiesList.get(0));
    }


}
