package io.finto.integration.fineract.test.helpers.client;

import java.util.List;

public interface TestClientRepository<T extends TestClientRepository<T>> {
    
    T submitClient(TestClient testClient);

    T closeLastClient();

    T reactivateLastClient();

    T activateLastClient();

    T setClientStatus(TestClient testClient, ClientStatus status);

    int getClientCount();
    
    List<TestClient> getClients();

    List<Integer> getClientIDs();

    TestClient getClient(int creationIndex);
    
    Integer getClientId(int creationIndex);

    Integer getClientId(TestClient client);
    
    default TestClient getFirstClient() {
        return getClient(0);
    }
    
    default TestClient getLastClient() {
        return getClient(getClientCount() - 1);
    }

    default Integer getLastClientId() {
        return getClientId(getClientCount() - 1);
    }

    void clearAll();
}
