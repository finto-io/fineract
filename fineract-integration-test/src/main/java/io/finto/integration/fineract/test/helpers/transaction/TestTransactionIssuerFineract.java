package io.finto.integration.fineract.test.helpers.transaction;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.finto.fineract.sdk.api.SavingsAccountTransactionsApi;
import io.finto.fineract.sdk.util.Calls;
import io.finto.fineract.sdk.util.FineractClient;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.util.Objects;

@Getter
@Builder(toBuilder = true)
public class TestTransactionIssuerFineract implements TestTransactionRepositoryImpl.TestTransactionIssuer {
    @NonNull private final SavingsAccountTransactionsApi client;
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Integer submitTransaction(TestTransaction transaction) {
        var response = Calls.ok(client.transactionSavingsAccountTransactions(
                transaction.getSavingAccountId(),
                transaction.toTransactionRequest(),
                transaction.getCommand()
        ));
        return Objects.requireNonNull(response.getSavingsId());
    }

}
