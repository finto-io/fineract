package io.finto.integration.fineract.test.helpers.transaction;


public interface TestTransactionBuilders<T extends TestTransactionRepository<T>> {

    TestTransactionCreator<T> buildTransaction();

    default T createRandomTransaction(Long savingAccountId) {
        return buildTransaction().withRandomParams().create(savingAccountId);
    }

    default TestTransaction createAndGetRandomTransaction(Long savingAccountId) {
        return createRandomTransaction(savingAccountId).getLastTransaction();
    }

}
