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

    private static final MockServerContainer MOCKSERVER_CONTAINER = new MockServerContainer(
            DockerImageName.parse("mockserver/mockserver:7.2.0"))
            .withCreateContainerCmdModifier(cmd -> Objects.requireNonNull(cmd.getHostConfig()).withPortBindings(
                    new PortBinding(Ports.Binding.bindPort(1082), new ExposedPort(1080))
            ));


    static {
        MOCKSERVER_CONTAINER.start();
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
