package net.bugreaper.modules.rest.api;

import net.bugreaper.modules.rest.PreSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class ApiBaseMethodsTests extends PreSetup {

    final String endpoint = "/api/test";


    @BeforeEach
    void addMockUniversal() {
        mocksApi.createMock(universalMock);
    }

    @Test
    void testGet() {
        api.sendGet(endpoint)
                .seeResponseCodeIsSuccessful();

        verifyMockMethod("GET", 1);
    }

    @Test
    void testPostNoBody() {
        api.setLogging(true).sendPost(endpoint)
                .seeResponseCodeIs(200);

        verifyMockMethod("POST", 1);
    }

    @Test
    void testPostBody() {
        api.sendPost(endpoint,
                        """
                                {"id": 1}""")
                .seeResponseCodeIs(200);

        verifyMockMethodBody("POST", 1, 1);
    }

    @Test
    void testDeleteNoBody() {
        api.sendDelete(endpoint)
                .seeResponseCodeIsSuccessful();

        verifyMockMethod("DELETE", 1);
    }

    @Test
    void testDeleteBody() {
        api.sendDelete(endpoint,
                        """
                                {"id": 1}""")
                .seeResponseCodeIs(200);

        verifyMockMethodBody("DELETE", 1, 1);
    }

    @Test
    void testPutNoBody() {
        api.sendPut(endpoint)
                .seeResponseCodeIs(200);

        verifyMockMethod("PUT", 1);
    }

    @Test
    void testPutBody() {
        api.sendPut(endpoint,
                        """
                                {"id": 1}""")
                .seeResponseCodeIsSuccessful();

        verifyMockMethodBody("PUT", 1, 1);
    }

    @Test
    void testPatchBody() {
        api.sendPatch(endpoint,
                        """
                                {"id": 1}""")
                .seeResponseCodeIs(200);

        verifyMockMethodBody("PATCH", 1, 1);
    }

    @Test
    void testOptions() {
        api.sendOptions(endpoint)
                .seeResponseCodeIs(200);

        verifyMockMethod("OPTIONS", 1);
    }

    @Test
    void testHead() {
        api.sendHead(endpoint)
                .seeResponseCodeIs(200);

        verifyMockMethod("HEAD", 1);
    }
}
