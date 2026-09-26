package testcontainers;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import net.bugreaper.modules.api.Api;
import net.bugreaper.modules.mocks.MocksApi;
import org.testcontainers.mockserver.MockServerContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Objects;

public class SetupMockserver {

    private static final String STABLE_VERSION = "mockserver/mockserver:5.15.0";
    private static final String LATEST_VERSION = "mockserver/mockserver:8.0.0";

    private static final String DOCKER_IMAGE = resolveDockerImage();


    private static final MockServerContainer MOCKSERVER_CONTAINER = new MockServerContainer(
            DockerImageName.parse(DOCKER_IMAGE))
            .withCreateContainerCmdModifier(cmd -> Objects.requireNonNull(cmd.getHostConfig()).withPortBindings(
                    new PortBinding(Ports.Binding.bindPort(1082), new ExposedPort(1080))
            ));


    static {
        System.out.printf("""
                \u001B[32m
                ============================================
                >>> TESTS RUNNING ON ON DOCKER IMAGE: %s <<<
                ============================================
                \u001B[0m
                %n""", DOCKER_IMAGE);

        MOCKSERVER_CONTAINER.start();
    }

    private static String resolveDockerImage() {
        String dockerVersion = System.getProperty("dockerTestVersion");

        if ("latest".equalsIgnoreCase(dockerVersion)) {
            return LATEST_VERSION;
        }

        return STABLE_VERSION;
    }

    public static Api getApi() {
        return new Api(
                "http://" + MOCKSERVER_CONTAINER.getHost(),
                MOCKSERVER_CONTAINER.getMappedPort(1080));
    }

    public static MocksApi getMocksApi() {
        return new MocksApi(
                "http://" + MOCKSERVER_CONTAINER.getHost(),
                MOCKSERVER_CONTAINER.getMappedPort(1080));

    }

}
