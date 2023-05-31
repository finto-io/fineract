package io.finto.integration.fineract.usecase;

import io.finto.integration.fineract.domain.Account;
import io.finto.integration.fineract.domain.CustomerId;

import java.util.List;

public interface FindAccountsUseCase {
    List<Account> findAccounts(CustomerId customerId);
}
