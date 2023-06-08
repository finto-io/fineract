package io.finto.integration.fineract.usecase.impl;

import io.finto.domain.account.AccountId;
import io.finto.domain.transaction.Transaction;
import io.finto.fineract.sdk.models.GetSavingsAccountsAccountIdResponse;
import io.finto.fineract.sdk.models.GetSavingsAccountsAccountIdTransactionsResponse;
import io.finto.integration.fineract.converter.FineractTransactionMapper;
import io.finto.usecase.transaction.FindAccountTransactionsUseCase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import retrofit2.Call;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Builder
public class SdkFindAccountTransactionsUseCase implements FindAccountTransactionsUseCase {

    @NonNull
    private final SdkFineractUseCaseContext context;
    @NonNull
    private final FineractTransactionMapper transactionMapper;

    public static class SdkFindAccountTransactionsUseCaseBuilder {
        private FineractTransactionMapper transactionMapper = FineractTransactionMapper.INSTANCE;
    }

    @Override
    public List<Transaction> findTransactionList(AccountId id) {
        Call<GetSavingsAccountsAccountIdResponse> call = context.savingsAccountApi().retrieveOneSavingsAccount(id.getValue(), null, null, "transactions");
        GetSavingsAccountsAccountIdResponse response = context.getResponseBody(call);
        List<GetSavingsAccountsAccountIdTransactionsResponse> transactions = response.getTransactions();
        if (transactions == null) {
            return Collections.emptyList();
        }
        return transactions.stream()
                .map(transactionMapper::toTransaction)
                .collect(Collectors.toList());
    }
}
