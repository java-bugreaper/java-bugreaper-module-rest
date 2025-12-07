package io.bugreaper.modules.rest;

import io.bugreaper.core.config.YamlUtils;
import io.bugreaper.modules.api.Api;
import io.bugreaper.modules.mocks.MocksApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class ConfigTests extends PreSetup {

    final String endpoint = "/api/test";



    private static final String CI = System.getenv("CI");
    private static final String PROPERTY = "bugreaperEnv";
    private String expectedHost;


    @BeforeEach
    void addMockUniversal() {
        YamlUtils.clearCache();
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
    void testConfigCustom5WithAllFields() {
        System.setProperty(PROPERTY, "custom");
        Api apiConf = new Api( "-5");

        assertEquals("""
                        Api:
                            url=http://my-host
                            port=8085
                            username=user_5
                            password=pass_5
                            useBasicAuth=false
                            authToken=token_5
                            contentType=application/json
                            enableLogging=true
                            maxResponseMsAssert=777
                        """,
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
                            maxResponseMsAssert=0
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

    @Test
    void testConfigCustom5ValidationNull() {
        System.setProperty(PROPERTY, "custom");

        Throwable exception = assertThrows(IllegalArgumentException.class, () ->
                new Api( null));

        assertEquals("suffix can`t be empty or null",
                exception.getMessage());

    }

    @Test
    void testConfigCustom5ValidationEmpty() {
        System.setProperty(PROPERTY, "custom");

        Throwable exception = assertThrows(IllegalArgumentException.class, () ->
                new Api( ""));

        assertEquals("suffix can`t be empty or null",
                exception.getMessage());

    }

}
