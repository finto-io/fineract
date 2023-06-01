package io.finto.integration.fineract.usecase.impl;

import io.finto.fineract.sdk.models.PostSavingsAccountsAccountIdRequest;
import io.finto.integration.fineract.domain.AccountId;
import io.finto.integration.fineract.usecase.ChangeAccountStatusUseCase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;

import java.time.LocalDate;
import java.util.Objects;

import static io.finto.fineract.sdk.Constants.DATE_FORMAT_PATTERN;
import static io.finto.fineract.sdk.Constants.DEFAULT_DATE_FORMATTER;

@AllArgsConstructor
@Builder
public class SdkChangeAccountStatusUseCase implements ChangeAccountStatusUseCase {

    private static final String COMMAND_APPROVE = "approve";
    private static final String COMMAND_ACTIVATE = "activate";
    private static final String COMMAND_CLOSE = "close";

    @NonNull
    private final SdkFineractUseCaseContext context;

    @Override
    public AccountId approveAccount(AccountId accountId) {
        PostSavingsAccountsAccountIdRequest fineractRequest = new PostSavingsAccountsAccountIdRequest();
        fineractRequest.setDateFormat(DATE_FORMAT_PATTERN);
        fineractRequest.setApprovedOnDate(LocalDate.now().format(DEFAULT_DATE_FORMATTER));
        fineractRequest.setLocale("en");
        return executeCommand(accountId, fineractRequest, COMMAND_APPROVE);
    }

    @Override
    public AccountId activateAccount(AccountId accountId) {
        PostSavingsAccountsAccountIdRequest fineractRequest = new PostSavingsAccountsAccountIdRequest();
        fineractRequest.setDateFormat(DATE_FORMAT_PATTERN);
        fineractRequest.setActivatedOnDate(LocalDate.now().format(DEFAULT_DATE_FORMATTER));
        fineractRequest.setLocale("en");
        return executeCommand(accountId, fineractRequest, COMMAND_ACTIVATE);
    }

    @Override
    public AccountId closeAccount(AccountId accountId) {
        PostSavingsAccountsAccountIdRequest fineractRequest = new PostSavingsAccountsAccountIdRequest();
        fineractRequest.setDateFormat(DATE_FORMAT_PATTERN);
        fineractRequest.setClosedOnDate(LocalDate.now().format(DEFAULT_DATE_FORMATTER));
        fineractRequest.setLocale("en");
        return executeCommand(accountId, fineractRequest, COMMAND_CLOSE);
    }

    private AccountId executeCommand(AccountId accountId, PostSavingsAccountsAccountIdRequest request, String command) {
        return AccountId.of(
                Objects.requireNonNull(context
                                .getResponseBody(
                                        context.savingsAccountApi().handleSavingsAccountsCommands(accountId.getValue(), request, command)
                                )
                                .getResourceId())
                        .longValue()
        );
    }

}
