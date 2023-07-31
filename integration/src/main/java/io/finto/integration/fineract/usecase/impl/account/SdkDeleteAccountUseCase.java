package io.finto.integration.fineract.usecase.impl.account;

import io.finto.domain.account.AccountId;
import io.finto.integration.fineract.usecase.impl.SdkFineractUseCaseContext;
import io.finto.usecase.account.DeleteAccountUseCase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;

import static io.finto.fineract.sdk.CustomDatatableNames.ACCOUNT_ADDITIONAL_FIELDS;

@AllArgsConstructor
@Builder
public class SdkDeleteAccountUseCase implements DeleteAccountUseCase {

    @NonNull
    private final SdkFineractUseCaseContext context;

    @Override
    public void deleteAccount(AccountId id) {
        context.getResponseBody(context.dataTablesApi().deleteDatatableEntries(ACCOUNT_ADDITIONAL_FIELDS, id.getValue()));
        context.getResponseBody(context.savingsAccountApi().deleteSavingsAccount(id.getValue()));
    }

}
