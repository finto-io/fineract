package io.finto.integration.fineract.test.helpers.client;

import io.finto.fineract.sdk.util.FineractClient;
import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
public class ClientHelper implements TestClientRepositoryDelegate<ClientHelper>,
        TestClientBuilders<ClientHelper> {

    @NonNull
    private final TestClientRepository<?> repository;

    public ClientHelper(@NonNull FineractClient fineract) {
        this(TestClientRepositoryImpl.builder().withClientIssuerREST(fineract.getClients(), fineract.getClientIdentifiers() ).build());
    }

    @Override
    public ClientHelper submitClient(TestClient client) {
        repository.submitClient(client);
        return this;
    }

    @Override
    public ClientHelper closeLastClient() {
        repository.closeLastClient();
        return this;
    }

    @Override
    public ClientHelper reactivateLastClient() {
        repository.reactivateLastClient();
        return this;
    }

    @Override
    public ClientHelper activateLastClient() {
        repository.activateLastClient();
        return this;
    }

    @Override
    public ClientHelper setClientStatus(TestClient testClient, ClientStatus status) {
        repository.setClientStatus(testClient, status);
        return this;
    }

    @Override
    public TestClientRepository<?> getClientRepository() {
        return repository;
    }

    @Override
    public TestClientCreator<ClientHelper> buildClient() {
        return new TestClientCreator<>(this);
    }


}
