package io.finto.integration.fineract.usecase;

import io.finto.integration.fineract.domain.Account;
import io.finto.integration.fineract.domain.AccountAdditionalFields;
import io.finto.integration.fineract.domain.AccountId;

public interface EnrichAccountInfoUseCase {
    Account saveAdditionalFields(AccountId accountId, AccountAdditionalFields additionalFields);
}
