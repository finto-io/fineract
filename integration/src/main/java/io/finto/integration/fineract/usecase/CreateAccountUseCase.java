package io.finto.integration.fineract.usecase;

import io.finto.integration.fineract.domain.AccountId;
import io.finto.integration.fineract.domain.OpeningAccount;

public interface CreateAccountUseCase {
    AccountId initAccount(OpeningAccount request);
}
