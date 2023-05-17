package io.finto.integration.fineract.usecase;

import io.finto.integration.fineract.domain.AccountId;

public interface DeleteAccountUseCase {
    void deleteAccount(AccountId id);
}
