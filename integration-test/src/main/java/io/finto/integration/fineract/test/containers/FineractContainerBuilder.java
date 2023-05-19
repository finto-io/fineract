package io.finto.integration.fineract.test.containers;

import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.Objects;

public class FineractContainerBuilder {

    public static final String FINERACTPOSTGRESQL_DEFAULT_SERVICE_NAME = "fineractpostgresql";
    public static final String FINERACT_DEFAULT_SERVICE_NAME = "fineract-server";
    public static final String FINERACT_DEFAULT_BASE_PATH = "fineract-provider/api/v1/";
    public static final String FINERACT_DEFAULT_TENANT = "default";
    public static final String FINERACT_DEFAULT_USERNAME = "mifos";
    public static final String FINERACT_DEFAULT_PASSWORD = "password";


    public static FineractContainerBuilder getInstance(){
        return new FineractContainerBuilder();
    }

    public DockerComposeContainer<?> startCommonContainer(){
        var classLoader = this.getClass().getClassLoader();
        var file = new File("./target/scripts/docker-compose.yml");

        try {
            var inputStream = classLoader.getResourceAsStream("docker-compose.yml");
            FileUtils.copyInputStreamToFile(Objects.requireNonNull(inputStream), file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        var container = new DockerComposeContainer<>(file)
                .withExposedService(FINERACTPOSTGRESQL_DEFAULT_SERVICE_NAME, 5432, Wait.forHealthcheck().withStartupTimeout(Duration.ofSeconds(600L)))
                .withExposedService(FINERACT_DEFAULT_SERVICE_NAME, 8443, Wait.forHealthcheck().withStartupTimeout(Duration.ofSeconds(600L)));
        container.start();

        var server = container.getContainerByServiceName(FINERACT_DEFAULT_SERVICE_NAME);
        if (server.isEmpty()) {
            throw new RuntimeException(String.format("Service with name %s is not found", FINERACT_DEFAULT_SERVICE_NAME));
        }
        var host = server.get().getHost();
        var port = server.get().getFirstMappedPort();
        var fineractPath = String.format("https://%s:%s", host, port);

        System.setProperty("FINERACT_HOST", fineractPath);
        System.setProperty("FINERACT_TENANT", FINERACT_DEFAULT_TENANT);
        System.setProperty("FINERACT_USERNAME", FINERACT_DEFAULT_USERNAME);
        System.setProperty("FINERACT_PASSWORD", FINERACT_DEFAULT_PASSWORD);
        System.setProperty("FINERACT_BASE_PATH", String.format("%s/%s", fineractPath, FINERACT_DEFAULT_BASE_PATH));
        return container;
    }





}
