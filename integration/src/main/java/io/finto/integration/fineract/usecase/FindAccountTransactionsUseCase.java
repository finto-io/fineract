package io.finto.integration.fineract.usecase;

import io.finto.integration.fineract.domain.AccountId;
import io.finto.integration.fineract.domain.Transaction;

import java.util.List;

public interface FindAccountTransactionsUseCase {
    List<Transaction> findTransactionList(AccountId id);
}
