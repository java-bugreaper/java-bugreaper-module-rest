package io.bugreaper.modules.rest;

import io.bugreaper.modules.api.Api;
import io.bugreaper.modules.mocks.MocksApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;


class ConfigTests extends PreSetup {

    final String endpoint = "/api/test";



    private static final String CI = System.getenv("CI");
    private static final String PROPERTY = "bugreaperEnv";
    private String expectedHost;


    @BeforeEach
    void addMockUniversal() {
        if(Objects.equals(CI, "true")){
            this.expectedHost = "http://docker";
        }else {
            this.expectedHost = "http://localhost";
        }
        mocksApi.createMock(universalMock);
    }

    @Test
    void testConfigWithAllFields() {
        if(Objects.equals(CI, "true")){
            System.setProperty(PROPERTY, "docker");
        }else {
            System.clearProperty(PROPERTY);
        }
        Api apiConf = new Api();

        apiConf.sendGet(endpoint)
                .seeResponseCodeIsSuccessful();

        verifyMockMethod("GET", 1);

        assertEquals(String.format("""
                        Api:
                            url=%s
                            port=1082
                            username=user_1
                            password=pass123
                            useBasicAuth=false
                            authToken=my-token
                            contentType=application/json
                            enableLogging=true
                            maxResponseMsAssert=2000
                        """, expectedHost),
                apiConf.getConfigSummary());
    }

    @Test
    void testConfigWithRequiredFieldsOnly() {
        if(Objects.equals(CI, "true")){
            System.setProperty(PROPERTY, "docker-noopt");
        }else {
            System.setProperty(PROPERTY, "noopt");
        }

        Api apiConf = new Api();

        apiConf.sendGet(endpoint)
                .seeResponseCodeIsSuccessful();

        verifyMockMethod("GET", 1);

        assertEquals(String.format("""
                        Api:
                            url=%s
                            port=1082
                            username=null
                            password=null
                            useBasicAuth=false
                            authToken=null
                            contentType=application/json
                            enableLogging=false
                            maxResponseMsAssert=5000
                        """, expectedHost),
                apiConf.getConfigSummary());
    }

    @Test
    void testConfigMocksWithAllFields() {
        if(Objects.equals(CI, "true")){
            System.setProperty(PROPERTY, "docker");
        }else {
            System.clearProperty(PROPERTY);
        }

        MocksApi mocksConf = new MocksApi();

        api.sendGet(endpoint)
                .seeResponseCodeIsSuccessful();

        mocksConf.verifyMock("""
                        {
                            "httpRequest": {
                                "method": "GET",
                                "path": "/api/test"
                            }
                        }""");

        assertEquals(String.format("""
                        MocksApi:
                            url=%s
                            port=1082
                            await=440
                            enableLogging=true
                            enchantedReport=false
                        """, expectedHost),
                mocksConf.getConfigSummary());
    }

    @Test
    void testConfigMocksWithRequiredFieldsOnly() {
        if(Objects.equals(CI, "true")){
            System.setProperty(PROPERTY, "docker-noopt");
        }else {
            System.setProperty(PROPERTY, "noopt");
        }

        MocksApi mocksConf = new MocksApi();

        api.sendGet(endpoint)
                .seeResponseCodeIsSuccessful();

        mocksConf.verifyMock("""
                        {
                            "httpRequest": {
                                "method": "GET",
                                "path": "/api/test"
                            }
                        }""");

        assertEquals(String.format("""
                        MocksApi:
                            url=%s
                            port=1082
                            await=2000
                            enableLogging=false
                            enchantedReport=true
                        """, expectedHost),
                mocksConf.getConfigSummary());
    }

}
