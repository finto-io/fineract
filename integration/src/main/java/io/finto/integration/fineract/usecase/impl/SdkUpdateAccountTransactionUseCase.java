package io.finto.integration.fineract.usecase.impl;

import io.finto.fineract.sdk.models.PostSavingsAccountsAccountIdRequest;
import io.finto.fineract.sdk.models.PostSavingsAccountsAccountIdResponse;
import io.finto.fineract.sdk.util.FineractClient;
import io.finto.integration.fineract.common.FineractResponseHandler;
import io.finto.integration.fineract.common.ResponseHandler;
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
    private final FineractTransactionMapper transactionMapper;
    @NonNull
    private final FineractClient fineractClient;
    @NonNull
    private final ResponseHandler responseHandler;

    public static UpdateAccountTransactionUseCase defaultInstance(FineractClient fineractClient) {
        return SdkUpdateAccountTransactionUseCase.builder()
                .fineractClient(fineractClient)
                .transactionMapper(FineractTransactionMapper.INSTANCE)
                .responseHandler(FineractResponseHandler.getDefaultInstance())
                .build();
    }

    @Override
    public void updateAccountTransactionsStatus(CustomerId customerId,
                                                AccountId accountId,
                                                TransactionsStatus status) {
        String command = transactionMapper.mapStatusToCommand(status);

        PostSavingsAccountsAccountIdRequest request = new PostSavingsAccountsAccountIdRequest();
        request.setReasonForBlock("Reason For Block");

        Call<PostSavingsAccountsAccountIdResponse> call = fineractClient.getSavingsAccounts().handleSavingsAccountsCommands(
                accountId.getValue(),
                request,
                command);
        responseHandler.getResponseBody(call);
    }

}
