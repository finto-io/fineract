package io.finto.integration.fineract.test.helpers.client;

import java.util.List;

public interface TestClientRepositoryDelegate<T extends TestClientRepository<T>>
    extends TestClientRepository<T>
{
    
    TestClientRepository<?> getClientRepository();
    
    @Override
    default int getClientCount() {
        return this.getClientRepository().getClientCount();
    }
    
    @Override
    default List<TestClient> getClients() {
        return this.getClientRepository().getClients();
    }

    @Override
    default List<Integer> getClientIDs() {
        return this.getClientRepository().getClientIDs();
    }


    @Override
    default TestClient getClient(int creationIndex) {
        return this.getClientRepository().getClient(creationIndex);
    }
    
    
    @Override
    default Integer getClientId(int creationIndex) {
        return this.getClientRepository().getClientId(creationIndex);
    }

    @Override
    default Integer getClientId(TestClient client) {
        return this.getClientRepository().getClientId(client);
    }

    @Override
    default void clearAll(){
        this.getClientRepository().clearAll();
    }

}
