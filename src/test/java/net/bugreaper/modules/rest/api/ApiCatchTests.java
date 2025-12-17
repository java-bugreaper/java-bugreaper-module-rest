package net.bugreaper.modules.rest.api;

import net.bugreaper.modules.api.Api;
import net.bugreaper.modules.api.assertable.AssertableResponse;
import net.bugreaper.modules.rest.PreSetup;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.Test;
import testcontainers.SetupMockserver;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertThrows;


class ApiCatchTests extends PreSetup {


    @Test
    void testTimeoutSetter() {
        mocksApi.resetMocks();
        mocksApi.createMock(withTimeout);

        Api apiTime = new SetupMockserver().getApi().withMaxResponseMsAssert(100);

        Throwable exception = assertThrows(AssertionError.class, () ->
                apiTime.sendGet("/api/test"));

        MatcherAssert.assertThat(
                "Timeout catch by setter (but wait response)",
                exception.getMessage(),
                StringContains.containsString("Expected response time was not a value less than <100L> milliseconds"));

    }


    @Test
    void testSeeResponseCodeIsSuccessfulCatch() {
        mocksApi.resetMocks();

        Api api = new SetupMockserver().getApi().withContentTypeJson();

        AssertableResponse result = api.sendGet("/api/test");

        Throwable exception = assertThrows(AssertionError.class, result::seeResponseCodeIsSuccessful);

        MatcherAssert.assertThat(
                exception.getMessage(),
                StringContains.containsString("Expected SUCCESSFUL(2xx) status code, but got: 404"));

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

}
