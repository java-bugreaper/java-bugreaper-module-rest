package io.bugreaper.modules.rest.api;

import io.bugreaper.modules.rest.PreSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.bugreaper.modules.api.assertable.response.ResponseOperators.statusCode;
import static io.bugreaper.modules.api.assertable.response.ResponseOperators.statusCodeSuccessful;


class ApiBaseMethodsTests extends PreSetup {

    final String endpoint = "/api/test";


    @BeforeEach
    void addMockUniversal() {
        mocksApi.createMock(universalMock);
    }

    @Test
    void testGet() {
        api.sendGet(endpoint)
                .shouldHave(statusCodeSuccessful());

        verifyMockMethod("GET", 1);
    }

    @Test
    void testPostNoBody() {
        api.sendPost(endpoint)
                .shouldHave(statusCode(200));

        verifyMockMethod("POST", 1);
    }

    @Test
    void testPostBody() {
        api.sendPost(endpoint,
                        """
                                {"id": 1}""")
                .shouldHave(statusCode(200));

        verifyMockMethodBody("POST", 1, 1);
    }

    @Test
    void testDeleteNoBody() {
        api.sendDelete(endpoint)
                .shouldHave(statusCode(200));

        verifyMockMethod("DELETE", 1);
    }

    @Test
    void testDeleteBody() {
        api.sendDelete(endpoint,
                        """
                                {"id": 1}""")
                .shouldHave(statusCode(200));

        verifyMockMethodBody("DELETE", 1, 1);
    }

    @Test
    void testPutNoBody() {
        api.sendPut(endpoint)
                .shouldHave(statusCode(200));

        verifyMockMethod("PUT", 1);
    }

    @Test
    void testPutBody() {
        api.sendPut(endpoint,
                        """
                                {"id": 1}""")
                .shouldHave(statusCode(200));

        verifyMockMethodBody("PUT", 1, 1);
    }

    @Test
    void testPatchBody() {
        api.sendPatch(endpoint,
                        """
                                {"id": 1}""")
                .shouldHave(statusCode(200));

        verifyMockMethodBody("PATCH", 1, 1);
    }

    @Test
    void testOptions() {
        api.sendOptions(endpoint)
                .shouldHave(statusCode(200));

        verifyMockMethod("OPTIONS", 1);
    }

    @Test
    void testHead() {
        api.sendHead(endpoint)
                .shouldHave(statusCode(200));

        verifyMockMethod("HEAD", 1);
    }
}
