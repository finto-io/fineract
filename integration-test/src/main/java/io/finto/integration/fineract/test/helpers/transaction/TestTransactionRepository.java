package io.finto.integration.fineract.test.helpers.transaction;

import java.util.List;

public interface TestTransactionRepository<T extends TestTransactionRepository<T>> {
    
    T submitTransaction(TestTransaction transaction);
    
    int getTransactionCount();
    
    List<TestTransaction> getTransactions();

    List<Integer> getTransactionIDs();

    TestTransaction getTransaction(int creationIndex);
    
    Integer getTransactionId(int creationIndex);

    Integer getTransactionId(TestTransaction Transaction);
    
    default TestTransaction getFirstTransaction() {
        return getTransaction(0);
    }
    
    default TestTransaction getLastTransaction() {
        return getTransaction(getTransactionCount() - 1);
    }

    default Integer getLastTransactionId() {
        return getTransactionId(getTransactionCount() - 1);
    }

    void clearAll();
}
