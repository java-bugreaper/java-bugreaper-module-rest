package testcontainers;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import net.bugreaper.modules.api.Api;
import net.bugreaper.modules.mocks.MocksApi;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Objects;

public class SetupMockserver {

    static MockServerContainer mockServerContainer = new MockServerContainer(
            DockerImageName.parse("mockserver/mockserver:7.2.0")
    );

    public SetupMockserver() {
        mockServerContainer
                .withCreateContainerCmdModifier(cmd -> {
                    Objects.requireNonNull(cmd.getHostConfig()).withPortBindings(
                            new PortBinding(Ports.Binding.bindPort(1082), new ExposedPort(1080))
                    );
                })
                .start();
    }

    public Api getApi() {
        return new Api(
                "http://" + mockServerContainer.getHost(),
                mockServerContainer.getMappedPort(1080));
    }

    public MocksApi getMocksApi() {
        return new MocksApi(
                "http://" + mockServerContainer.getHost(),
                mockServerContainer.getMappedPort(1080));

    }

}
