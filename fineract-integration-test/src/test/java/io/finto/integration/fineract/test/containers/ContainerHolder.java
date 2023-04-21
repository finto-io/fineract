package io.finto.integration.fineract.test.containers;

import org.junit.jupiter.api.extension.Extension;
import org.testcontainers.containers.DockerComposeContainer;

public class ContainerHolder implements Extension {

    private static final DockerComposeContainer<?> fineract = FineractContainerBuilder.getInstance()
            .startCommonContainer();

    public static DockerComposeContainer<?> getFineract(){
        return fineract;
    }

}
