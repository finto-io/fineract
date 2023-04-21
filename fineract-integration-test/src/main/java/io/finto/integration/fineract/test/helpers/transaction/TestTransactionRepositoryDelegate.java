package io.finto.integration.fineract.test.helpers.transaction;

import java.util.List;

public interface TestTransactionRepositoryDelegate<T extends TestTransactionRepository<T>>
    extends TestTransactionRepository<T>
{
    
    TestTransactionRepository<?> getTransactionRepository();
    
    @Override
    default int getTransactionCount() {
        return this.getTransactionRepository().getTransactionCount();
    }
    
    @Override
    default List<TestTransaction> getTransactions() {
        return this.getTransactionRepository().getTransactions();
    }

    @Override
    default List<Integer> getTransactionIDs() {
        return this.getTransactionRepository().getTransactionIDs();
    }


    @Override
    default TestTransaction getTransaction(int creationIndex) {
        return this.getTransactionRepository().getTransaction(creationIndex);
    }
    
    
    @Override
    default Integer getTransactionId(int creationIndex) {
        return this.getTransactionRepository().getTransactionId(creationIndex);
    }

    @Override
    default Integer getTransactionId(TestTransaction transaction) {
        return this.getTransactionRepository().getTransactionId(transaction);
    }

    @Override
    default void clearAll(){
        this.getTransactionRepository().clearAll();
    }

}
