package io.finto.integration.fineract.test.helpers.transaction;

import io.finto.fineract.sdk.api.SavingsAccountTransactionsApi;
import io.finto.fineract.sdk.util.FineractClient;
import lombok.Builder;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Builder(toBuilder = true)
public class TestTransactionRepositoryImpl implements TestTransactionRepository<TestTransactionRepositoryImpl> {

    public interface TestTransactionIssuer {
        Integer submitTransaction(TestTransaction transaction);
    }

    @NonNull private final TestTransactionRepositoryImpl.TestTransactionIssuer issuer;
    private final List<TestTransaction> savingAccounts = new ArrayList<>();
    private final List<Integer> transactionIDs = new ArrayList<>();

    private int normalizeCreationIndex(int creationIndex) {
        return creationIndex < 0 ? getTransactionCount() + creationIndex : creationIndex;
    }

    public static class TestTransactionRepositoryImplBuilder {
        
        public TestTransactionRepositoryImplBuilder withTransactionIssuerREST(SavingsAccountTransactionsApi client) {
            this.issuer = TestTransactionIssuerFineract.builder().client(client).build();
            return this;
        }
        
    }

    @Override
    public TestTransactionRepositoryImpl submitTransaction(TestTransaction savingAccount) {
        var id = issuer.submitTransaction(savingAccount);
        savingAccounts.add(savingAccount.markIssued());
        transactionIDs.add(id);
        return this;
    }

    @Override
    public int getTransactionCount() {
        return savingAccounts.size();
    }

    @Override
    public List<TestTransaction> getTransactions() {
        return new ArrayList<>(savingAccounts);
    }

    @Override
    public List<Integer> getTransactionIDs() {
        return transactionIDs;
    }

    @Override
    public TestTransaction getTransaction(int creationIndex) {
        return savingAccounts.get(normalizeCreationIndex(creationIndex));
    }

    public Integer getTransactionId(int creationIndex) {
        return transactionIDs.get(normalizeCreationIndex(creationIndex));
    }

    public Integer getTransactionId(TestTransaction savingAccount) {
        int pos = savingAccounts.indexOf(savingAccount);
        if ( pos < 0 ) {
            throw new NoSuchElementException();
        }
        return transactionIDs.get(pos);
    }

    public void clearAll(){
        savingAccounts.clear();
        transactionIDs.clear();
    }
}
