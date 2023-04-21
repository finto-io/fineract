package io.finto.integration.fineract.test.containers;

import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.File;
import java.time.Duration;
import java.util.Objects;
import java.util.function.Consumer;

public class FineractContainerBuilder {

    public static FineractContainerBuilder getInstance(){
        return new FineractContainerBuilder();
    }

    public DockerComposeContainer<?> startCommonContainer(){
        var classLoader = this.getClass().getClassLoader();
        var composeFile = new File(Objects.requireNonNull(classLoader.getResource("scripts/docker-compose.yml")).getFile());
        var container = new DockerComposeContainer(composeFile)
                .withLocalCompose(true)
                .withExposedService("fineractpostgresql", 5432, Wait.forHealthcheck().withStartupTimeout(Duration.ofSeconds(600L)))
                .withExposedService("fineract-server", 8443, Wait.forHealthcheck().withStartupTimeout(Duration.ofSeconds(600L)));
        container.start();
        return container;
    }

}
