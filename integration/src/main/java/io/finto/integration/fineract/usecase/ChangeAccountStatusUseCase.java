package io.finto.integration.fineract.usecase;

import io.finto.integration.fineract.domain.AccountId;

public interface ChangeAccountStatusUseCase {
    AccountId approveAccount(AccountId accountId);

    AccountId activateAccount(AccountId accountId);

    AccountId closeAccount(AccountId accountId);
}
