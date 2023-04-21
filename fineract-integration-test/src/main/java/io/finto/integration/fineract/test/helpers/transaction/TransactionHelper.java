package io.finto.integration.fineract.test.helpers.transaction;

import io.finto.fineract.sdk.api.SavingsAccountTransactionsApi;
import io.finto.fineract.sdk.util.FineractClient;
import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
public class TransactionHelper implements TestTransactionRepositoryDelegate<TransactionHelper>,
        TestTransactionBuilders<TransactionHelper> {

    @NonNull
    private final TestTransactionRepository<?> repository;

    public TransactionHelper(@NonNull FineractClient fineract) {
        this(TestTransactionRepositoryImpl.builder().withTransactionIssuerREST(fineract.getSavingsTransactions()).build());
    }

    @Override
    public TransactionHelper submitTransaction(TestTransaction transaction) {
        repository.submitTransaction(transaction);
        return this;
    }

    @Override
    public TestTransactionRepository<?> getTransactionRepository() {
        return repository;
    }

    @Override
    public TestTransactionCreator<TransactionHelper> buildTransaction() {
        return new TestTransactionCreator<>(this);
    }


}
