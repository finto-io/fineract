package io.finto.integration.fineract.usecase.impl;

import io.finto.fineract.sdk.util.FineractClient;
import io.finto.integration.fineract.common.FineractResponseHandler;
import io.finto.integration.fineract.common.ResponseHandler;
import io.finto.integration.fineract.domain.AccountId;
import io.finto.integration.fineract.usecase.DeleteAccountUseCase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;

@AllArgsConstructor
@Builder
public class SdkDeleteAccountUseCase implements DeleteAccountUseCase {

    @NonNull
    private final FineractClient fineractClient;
    @NonNull
    private final ResponseHandler responseHandler;

    public static SdkDeleteAccountUseCase defaultInstance(FineractClient fineractClient) {
        return SdkDeleteAccountUseCase.builder()
                .fineractClient(fineractClient)
                .responseHandler(FineractResponseHandler.getDefaultInstance())
                .build();
    }

    @Override
    public void deleteAccount(AccountId id) {
        responseHandler.getResponseBody(fineractClient.getSavingsAccounts().deleteSavingsAccount(id.getValue()));
    }

}
