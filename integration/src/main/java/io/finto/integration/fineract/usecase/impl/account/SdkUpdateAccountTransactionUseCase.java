package io.finto.integration.fineract.usecase.impl.account;

import io.finto.domain.account.AccountId;
import io.finto.domain.customer.CustomerId;
import io.finto.domain.transaction.TransactionsStatus;
import io.finto.fineract.sdk.models.PostSavingsAccountsAccountIdRequest;
import io.finto.fineract.sdk.models.PostSavingsAccountsAccountIdResponse;
import io.finto.integration.fineract.converter.FineractTransactionMapper;
import io.finto.integration.fineract.usecase.impl.SdkFineractUseCaseContext;
import io.finto.usecase.transaction.UpdateAccountTransactionUseCase;
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
