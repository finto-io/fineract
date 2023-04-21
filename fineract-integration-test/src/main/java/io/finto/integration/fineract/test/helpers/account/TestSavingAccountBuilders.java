package io.finto.integration.fineract.test.helpers.account;


public interface TestSavingAccountBuilders<T extends TestSavingAccountRepository<T>> {

    TestSavingAccountCreator<T> buildSavingAccount();

    default T createRandomSavingAccount(Integer clientId, Integer productId) {
        return buildSavingAccount().withRandomParams().create(clientId, productId);
    }

    default T createRandomSavingAccount() {
        return buildSavingAccount().withRandomParams().create(1, 1);
    }


    default TestSavingAccount createAndGetRandomSavingAccount(Integer clientId, Integer productId) {
        return createRandomSavingAccount(clientId, productId).getLastSavingAccount();
    }

}
