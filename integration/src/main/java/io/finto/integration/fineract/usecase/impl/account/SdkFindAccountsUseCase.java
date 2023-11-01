package io.finto.integration.fineract.usecase.impl.account;

import io.finto.domain.account.Account;
import io.finto.domain.account.AccountId;
import io.finto.domain.customer.CustomerId;
import io.finto.fineract.sdk.models.GetClientsSavingsAccounts;
import io.finto.integration.fineract.usecase.impl.SdkFineractUseCaseContext;
import io.finto.usecase.account.FindAccountUseCase;
import io.finto.usecase.account.FindAccountsUseCase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@AllArgsConstructor
@Builder
public class SdkFindAccountsUseCase implements FindAccountsUseCase {

    @NonNull
    private final SdkFineractUseCaseContext context;
    @NonNull
    private final FindAccountUseCase findAccountUseCase;

    @Override
    public List<Account> findAccounts(CustomerId customerId) {
        Set<GetClientsSavingsAccounts> clientAccounts = Objects.requireNonNull(
                context.getResponseBody(
                        context.clientApi().retrieveAssociatedAccounts(customerId.getValue(), null)
                )
        ).getSavingsAccounts();
        if (clientAccounts == null) {
            return List.of();
        }
        List<Account> accounts = new ArrayList<>();
        clientAccounts.forEach(account ->
                accounts.add(findAccountUseCase.findAccount(AccountId.of(account.getId())))
        );
        return accounts;
    }
}
