package net.bugreaper.modules.rest.api;

import net.bugreaper.modules.api.Api;
import net.bugreaper.modules.api.assertable.AssertableResponse;
import net.bugreaper.modules.rest.PreSetup;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import testcontainers.SetupMockserver;

import java.net.SocketTimeoutException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@Isolated
class ApiCatchTests extends PreSetup {


    @Test
    void testTimeoutAssertSetter() {
        mocksApi.resetMocks();
        mocksApi.createMock(withTimeout);

        Api apiTime = new SetupMockserver().getApi().setMaxResponseMsAssert(100);
        Throwable exception = assertThrows(AssertionError.class, () ->
                apiTime.sendGet("/api/test"));

        MatcherAssert.assertThat(
                "Timeout catch by setter (but wait response)",
                exception.getMessage(),
                StringContains.containsString("Expected response time was not a value less than <100L> milliseconds"));

    }

    @Test
    void testTimeoutSetter() {
        mocksApi.resetMocks();
        mocksApi.createMock(withTimeout);

        Api apiTime = new SetupMockserver().getApi().setTimeoutMs(100);

        Throwable exception = assertThrows(SocketTimeoutException.class, () ->
                apiTime.sendGet("/api/test"));

        MatcherAssert.assertThat(
                "Timeout catch by setter ",
                exception.getMessage(),
                StringContains.containsString("Read timed out"));

    }


    @Test
    void testSeeResponseCodeIsSuccessfulCatch() {
        mocksApi.resetMocks();
        mocksApi.createMock("""
            {
              "httpRequest": {
              },
              "httpResponse": {
                "statusCode": 404
              }
            }""");
        Api api = new SetupMockserver().getApi().setContentTypeJson();

        AssertableResponse result = api.sendGet("/api/test");

        Throwable exception = assertThrows(AssertionError.class, result::seeResponseCodeIsSuccessful);

            assertEquals(
                    "Expected SUCCESSFUL(2xx) status code, but got: 404",
                    exception.getMessage());
    }

    @Test
    void testCatchXmlSchemaValidation() {
        var data = "some_data";

        createMockTestPostXml(data, 200);

        AssertableResponse result =  apiXml.sendPost("/api/post-xml",
                "<response> <key>some_data</key> </response>")
                .seeResponseCodeIsSuccessful();

        Path path = Path.of("testdata/schemas/post_2_failed_xml.xsd");

        Throwable exception = assertThrows(AssertionError.class, () ->
                result.seeResponseMatchesXmlSchema(path));

        MatcherAssert.assertThat(
                "Catch XML schema assert",
                exception.getMessage(),
                StringContains.containsString("""
                        Response schema not match expected XML
                        cvc-complex-type.2.4.a: Invalid content was found starting with element 'id'. One of '{id2, status}' is expected."""));
    }

    @Test
    void testSeeResponseContainsJsonSubset() {
        mocksApi.createMockCustom(
                "from file",
                "testdata/mocks/get_order.json");

        AssertableResponse result = api.sendGet("/api/get")
                .seeResponseCodeIs(200);

        Throwable exception = assertThrows(AssertionError.class, () ->
                result.seeResponseContainsJsonSubset("""
                        {
                          "array": [
                            {
                              "id": 904
                            }
                          ]
                        }"""));

        MatcherAssert.assertThat(
                exception.getMessage(),
                StringContains.containsString("""
                        JSON subset assertion failed:
                         - array.[]: missing element {"id":904}
                        """));
    }

    @Test
    void testSeeResponseContainsJsonExtended() {
        mocksApi.createMockCustom(
                "from file",
                "testdata/mocks/get_1.json");

        AssertableResponse result = api.sendGet("/api/get")
                .seeResponseCodeIs(200);

        Throwable exception = assertThrows(AssertionError.class, () ->
                result.seeResponseContainsExtendedJson("""
                        {
                          "status:>": 11,
                          "statusName:regex": ".*ening"
                        }"""));

        MatcherAssert.assertThat(
                exception.getMessage(),
                StringContains.containsString("""
                        JSON comparison failed:
                        • status: expected >[11] but was [11]
                        • statusName: expected regex [.*ening] but was [Something]"""));
    }

}
