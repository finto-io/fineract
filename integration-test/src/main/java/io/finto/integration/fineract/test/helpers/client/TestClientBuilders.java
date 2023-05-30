package io.finto.integration.fineract.test.helpers.client;


public interface TestClientBuilders<T extends TestClientRepository<T>> {

    TestClientCreator<T> buildClient();

    default T createRandomClient() {
        return buildClient().withRandomParams().create();
    }

    default TestClient createAndGetRandomClient() {
        return createRandomClient().getLastClient();
    }

}
