package io.finto.integration.fineract.usecase;

import io.finto.integration.fineract.domain.Account;
import io.finto.integration.fineract.domain.AccountId;

public interface FindAccountUseCase {
    Account findAccount(AccountId id);
}
