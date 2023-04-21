package io.finto.integration.fineract.test.helpers.account;

import io.finto.fineract.sdk.api.SavingsAccountApi;
import io.finto.fineract.sdk.util.FineractClient;
import lombok.Builder;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Builder(toBuilder = true)
public class TestSavingAccountRepositoryImpl implements TestSavingAccountRepository<TestSavingAccountRepositoryImpl> {

    public interface TestSavingAccountIssuer {
        Integer submitSavingAccount(TestSavingAccount account);
        void updateStatus(Integer accountId, TestSavingAccount account);
    }

    @NonNull private final TestSavingAccountRepositoryImpl.TestSavingAccountIssuer issuer;
    private final List<TestSavingAccount> savingAccounts = new ArrayList<>();
    private final List<Integer> accountIDs = new ArrayList<>();

    private int normalizeCreationIndex(int creationIndex) {
        return creationIndex < 0 ? getSavingAccountCount() + creationIndex : creationIndex;
    }

    public static class TestSavingAccountRepositoryImplBuilder {
        
        public TestSavingAccountRepositoryImplBuilder withSavingAccountIssuerREST(SavingsAccountApi client) {
            this.issuer = TestSavingAccountIssuerFineract.builder().client(client).build();
            return this;
        }
        
    }

    @Override
    public TestSavingAccountRepositoryImpl submitSavingAccount(TestSavingAccount savingAccount) {
        var id = issuer.submitSavingAccount(savingAccount);
        savingAccounts.add(savingAccount.markIssued());
        accountIDs.add(id);
        return this;
    }

    @Override
    public int getSavingAccountCount() {
        return savingAccounts.size();
    }

    @Override
    public List<TestSavingAccount> getSavingAccounts() {
        return new ArrayList<>(savingAccounts);
    }

    @Override
    public List<Integer> getSavingAccountIDs() {
        return accountIDs;
    }

    @Override
    public TestSavingAccount getSavingAccount(int creationIndex) {
        return savingAccounts.get(normalizeCreationIndex(creationIndex));
    }

    public Integer getSavingAccountId(int creationIndex) {
        return accountIDs.get(normalizeCreationIndex(creationIndex));
    }

    public Integer getSavingAccountId(TestSavingAccount savingAccount) {
        int pos = savingAccounts.indexOf(savingAccount);
        if ( pos < 0 ) {
            throw new NoSuchElementException();
        }
        return accountIDs.get(pos);
    }

    @Override
    public TestSavingAccountRepositoryImpl closeLastAccount() {
        return setAccountStatus(getLastSavingAccount(), AccountStatus.CLOSED);
    }

    @Override
    public TestSavingAccountRepositoryImpl approveLastAccount() {
        return setAccountStatus(getLastSavingAccount(), AccountStatus.APPROVED);
    }

    @Override
    public TestSavingAccountRepositoryImpl activateLastAccount() {
        return setAccountStatus(getLastSavingAccount(), AccountStatus.ACTIVATED);
    }

    public TestSavingAccountRepositoryImpl setAccountStatus(TestSavingAccount savingAccount, AccountStatus status){
        var id = getSavingAccountId(savingAccount);
        savingAccount.setStatus(status);
        issuer.updateStatus(id, savingAccount);
        return this;
    }

    public void clearAll(){
        savingAccounts.clear();
        accountIDs.clear();
    }
}
