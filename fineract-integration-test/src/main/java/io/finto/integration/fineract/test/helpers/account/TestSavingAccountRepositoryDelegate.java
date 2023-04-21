package io.finto.integration.fineract.test.helpers.account;

import java.util.List;

public interface TestSavingAccountRepositoryDelegate<T extends TestSavingAccountRepository<T>>
    extends TestSavingAccountRepository<T>
{
    
    TestSavingAccountRepository<?> getSavingAccountRepository();
    
    @Override
    default int getSavingAccountCount() {
        return this.getSavingAccountRepository().getSavingAccountCount();
    }
    
    @Override
    default List<TestSavingAccount> getSavingAccounts() {
        return this.getSavingAccountRepository().getSavingAccounts();
    }

    @Override
    default List<Integer> getSavingAccountIDs() {
        return this.getSavingAccountRepository().getSavingAccountIDs();
    }


    @Override
    default TestSavingAccount getSavingAccount(int creationIndex) {
        return this.getSavingAccountRepository().getSavingAccount(creationIndex);
    }
    
    
    @Override
    default Integer getSavingAccountId(int creationIndex) {
        return this.getSavingAccountRepository().getSavingAccountId(creationIndex);
    }

    @Override
    default Integer getSavingAccountId(TestSavingAccount account) {
        return this.getSavingAccountRepository().getSavingAccountId(account);
    }

    @Override
    default void clearAll(){
        this.getSavingAccountRepository().clearAll();
    }

}
