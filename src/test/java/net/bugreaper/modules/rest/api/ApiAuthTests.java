package net.bugreaper.modules.rest.api;

import net.bugreaper.modules.api.Api;
import net.bugreaper.modules.rest.PreSetup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;


@Isolated
class ApiAuthTests extends PreSetup {

    final String endpoint = "/api/test";

    protected Api apiTest = getApi();


    @Test
    void testAuthSequence() {
        mocksApi.createMock(universalMock);

        apiTest.setBasicAuth("user", "pass").sendGet(endpoint)
                .seeResponseCodeIsSuccessful();

        mocksApi.verifyMock("""
                {
                  "httpRequest": {
                    "headers": {
                        "Authorization" : ["Basic dXNlcjpwYXNz"]
                    }
                  }
                }""");
        mocksApi.cleanMockLogs();

        apiTest.setBearerAuth("token-1").sendGet(endpoint)
                .seeResponseCodeIsSuccessful();

        mocksApi.verifyMock("""
                {
                  "httpRequest": {
                    "headers": {
                        "Authorization" : ["Bearer token-1"]
                    }
                  }
                }""");
        mocksApi.cleanMockLogs();

        apiTest.setBasicAuth("user", "pass").sendGet(endpoint)
                .seeResponseCodeIsSuccessful();

        mocksApi.verifyMock("""
                {
                  "httpRequest": {
                    "headers": {
                        "Authorization" : ["Basic dXNlcjpwYXNz"]
                    }
                  }
                }""");
        mocksApi.cleanMockLogs();

        apiTest.setNoAuth().sendGet(endpoint)
                .seeResponseCodeIsSuccessful();

        mocksApi.verifyMock("""
                {
                  "httpRequest": {
                    "headers": {
                        "!Authorization" : [".*"]
                    }
                  }
                }""");

    }



}
