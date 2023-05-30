package io.finto.integration.fineract.test.helpers.client;

import io.finto.fineract.sdk.api.ClientApi;
import lombok.Builder;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Builder(toBuilder = true)
public class TestClientRepositoryImpl implements TestClientRepository<TestClientRepositoryImpl> {

    public interface TestClientIssuer {
        Integer submitClient(TestClient account);

        void updateStatus(Integer accountId, TestClient account);
    }

    @NonNull
    private final TestClientRepositoryImpl.TestClientIssuer issuer;
    private final List<TestClient> clients = new ArrayList<>();
    private final List<Integer> clientIDs = new ArrayList<>();

    private int normalizeCreationIndex(int creationIndex) {
        return creationIndex < 0 ? getClientCount() + creationIndex : creationIndex;
    }

    public static class TestClientRepositoryImplBuilder {
        public TestClientRepositoryImplBuilder withClientIssuerREST(ClientApi clientApi) {
            this.issuer = TestClientIssuerFineract.builder().clientApi(clientApi).build();
            return this;
        }

    }

    @Override
    public TestClientRepositoryImpl submitClient(TestClient client) {
        var id = issuer.submitClient(client);
        clients.add(client.markIssued());
        clientIDs.add(id);
        return this;
    }

    @Override
    public int getClientCount() {
        return clients.size();
    }

    @Override
    public List<TestClient> getClients() {
        return new ArrayList<>(clients);
    }

    @Override
    public List<Integer> getClientIDs() {
        return clientIDs;
    }

    @Override
    public TestClient getClient(int creationIndex) {
        return clients.get(normalizeCreationIndex(creationIndex));
    }

    public Integer getClientId(int creationIndex) {
        return clientIDs.get(normalizeCreationIndex(creationIndex));
    }

    public Integer getClientId(TestClient client) {
        int pos = clients.indexOf(client);
        if (pos < 0) {
            throw new NoSuchElementException();
        }
        return clientIDs.get(pos);
    }

    @Override
    public TestClientRepositoryImpl closeLastClient() {
        return setClientStatus(getLastClient(), ClientStatus.CLOSED);
    }

    @Override
    public TestClientRepositoryImpl approveLastClient() {
        return setClientStatus(getLastClient(), ClientStatus.APPROVED);
    }

    @Override
    public TestClientRepositoryImpl activateLastClient() {
        return setClientStatus(getLastClient(), ClientStatus.ACTIVATED);
    }

    public TestClientRepositoryImpl setClientStatus(TestClient savingClient, ClientStatus status) {
        var id = getClientId(savingClient);
        savingClient.setStatus(status);
        issuer.updateStatus(id, savingClient);
        return this;
    }

    public void clearAll() {
        clients.clear();
        clientIDs.clear();
    }
}
