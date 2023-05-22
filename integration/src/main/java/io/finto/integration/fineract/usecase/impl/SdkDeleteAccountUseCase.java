package io.finto.integration.fineract.usecase.impl;

import io.finto.integration.fineract.domain.AccountId;
import io.finto.integration.fineract.usecase.DeleteAccountUseCase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;

@AllArgsConstructor
@Builder
public class SdkDeleteAccountUseCase implements DeleteAccountUseCase {

    @NonNull
    private final SdkFineractUseCaseContext context;

    @Override
    public void deleteAccount(AccountId id) {
        context.getResponseBody(context.savingsAccountApi().deleteSavingsAccount(id.getValue()));
    }

}
