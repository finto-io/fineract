package io.finto.integration.fineract.usecase;

import io.finto.integration.fineract.domain.AccountId;
import io.finto.integration.fineract.domain.CustomerId;
import io.finto.integration.fineract.domain.TransactionsStatus;

public interface UpdateAccountTransactionUseCase {
    void updateAccountTransactionsStatus(CustomerId customerId, AccountId accountId, TransactionsStatus status);
}
