package io.finto.integration.fineract.usecase.impl;

import io.finto.fineract.sdk.models.PostSavingsAccountsAccountIdRequest;
import io.finto.fineract.sdk.models.PostSavingsAccountsAccountIdResponse;
import io.finto.integration.fineract.converter.FineractTransactionMapper;
import io.finto.integration.fineract.domain.AccountId;
import io.finto.integration.fineract.domain.CustomerId;
import io.finto.integration.fineract.domain.TransactionsStatus;
import io.finto.integration.fineract.usecase.UpdateAccountTransactionUseCase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import retrofit2.Call;

@AllArgsConstructor
@Builder
public class SdkUpdateAccountTransactionUseCase implements UpdateAccountTransactionUseCase {

    @NonNull
    private final SdkFineractUseCaseContext context;
    @NonNull
    private final FineractTransactionMapper transactionMapper;

    public static class SdkUpdateAccountTransactionUseCaseBuilder {
        private FineractTransactionMapper transactionMapper = FineractTransactionMapper.INSTANCE;
    }

    @Override
    public void updateAccountTransactionsStatus(CustomerId customerId,
                                                AccountId accountId,
                                                TransactionsStatus status) {
        String command = transactionMapper.mapStatusToCommand(status);

        PostSavingsAccountsAccountIdRequest request = new PostSavingsAccountsAccountIdRequest();
        request.setReasonForBlock("Reason For Block");

        Call<PostSavingsAccountsAccountIdResponse> call = context.savingsAccountApi().handleSavingsAccountsCommands(
                accountId.getValue(),
                request,
                command);
        context.getResponseBody(call);
    }

}