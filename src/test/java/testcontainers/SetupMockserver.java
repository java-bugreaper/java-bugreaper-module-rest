package testcontainers;

import io.bugreaper.modules.api.Api;
import io.bugreaper.modules.mocks.MocksApi;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.utility.DockerImageName;

public class SetupMockserver {

    static MockServerContainer mockServerContainer = new MockServerContainer(
            DockerImageName.parse("mockserver/mockserver:5.13.2")
    );

    public SetupMockserver() {
        mockServerContainer
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
