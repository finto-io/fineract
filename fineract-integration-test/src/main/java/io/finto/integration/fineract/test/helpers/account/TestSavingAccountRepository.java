package io.finto.integration.fineract.test.helpers.account;

import java.util.List;
import java.util.UUID;

public interface TestSavingAccountRepository<T extends TestSavingAccountRepository<T>> {
    
    T submitSavingAccount(TestSavingAccount savingAccount);

    T closeLastAccount();

    T approveLastAccount();

    T activateLastAccount();

    T setAccountStatus(TestSavingAccount savingAccount, AccountStatus status);

    int getSavingAccountCount();
    
    List<TestSavingAccount> getSavingAccounts();

    List<Integer> getSavingAccountIDs();

    TestSavingAccount getSavingAccount(int creationIndex);
    
    Integer getSavingAccountId(int creationIndex);

    Integer getSavingAccountId(TestSavingAccount SavingAccount);
    
    default TestSavingAccount getFirstSavingAccount() {
        return getSavingAccount(0);
    }
    
    default TestSavingAccount getLastSavingAccount() {
        return getSavingAccount(getSavingAccountCount() - 1);
    }

    default Integer getLastSavingAccountId() {
        return getSavingAccountId(getSavingAccountCount() - 1);
    }

    void clearAll();
}
