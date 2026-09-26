package net.bugreaper.modules.rest;

import net.bugreaper.modules.api.Api;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ConfigOptionsTests {

    @BeforeAll
    static void setupEnvCustom(){
        System.setProperty("bugreaperEnv", "custom");
    }

    @Test
    void testConfigOnlyOptions() {
        Api apiConf = new Api();
        String result = apiConf.getHttpClientParams();

        MatcherAssert.assertThat(
                "provided option override",
                result,
                StringContains.containsString("http.socket.timeout=6000"));

        MatcherAssert.assertThat(
                "provided option",
                result,
                StringContains.containsString("http.connection.max-header-count=7000"));

        MatcherAssert.assertThat(
                "helper default",
                result,
                StringContains.containsString("http.connection.timeout=5000"));
    }

    @Test
    void testConfigNoOptionsNoSetter() {
        Api apiConf = new Api("-2");
        String result = apiConf.getHttpClientParams();

        MatcherAssert.assertThat(
                "helper default",
                result,
                StringContains.containsString("http.socket.timeout=5000"));

        MatcherAssert.assertThat(
                "helper default",
                result,
                StringContains.containsString("http.connection.timeout=5000"));
    }

    @Test
    void testConfigOnlySetter() {
        Api apiConf = new Api("-3");
        String result = apiConf.getHttpClientParams();

        MatcherAssert.assertThat(
                "provided setter override",
                result,
                StringContains.containsString("http.socket.timeout=2000"));

        MatcherAssert.assertThat(
                "provided setter override ",
                result,
                StringContains.containsString("http.connection.timeout=2000"));
    }

    @Test
    void testConfigSetterAndOptions() {
        Api apiConf = new Api("-4");
        String result = apiConf.getHttpClientParams();

        MatcherAssert.assertThat(
                "helper setter override option and default",
                result,
                StringContains.containsString("http.socket.timeout=1000"));

        MatcherAssert.assertThat(
                "helper setter",
                result,
                StringContains.containsString("http.connection.timeout=1000"));

        MatcherAssert.assertThat(
                "provided option",
                result,
                StringContains.containsString("http.connection.max-header-count=5000"));
    }
}
