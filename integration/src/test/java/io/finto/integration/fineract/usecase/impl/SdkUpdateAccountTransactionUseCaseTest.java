package io.finto.integration.fineract.usecase.impl;

import io.finto.fineract.sdk.api.SavingsAccountApi;
import io.finto.fineract.sdk.models.PostSavingsAccountsAccountIdRequest;
import io.finto.fineract.sdk.models.PostSavingsAccountsAccountIdResponse;
import io.finto.fineract.sdk.util.FineractClient;
import io.finto.integration.fineract.common.ResponseHandler;
import io.finto.integration.fineract.converter.FineractTransactionMapper;
import io.finto.integration.fineract.domain.AccountId;
import io.finto.integration.fineract.domain.CustomerId;
import io.finto.integration.fineract.domain.TransactionsStatus;
import org.easymock.IMocksControl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Call;

import static org.assertj.core.api.Assertions.assertThat;
import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class SdkUpdateAccountTransactionUseCaseTest {

    private IMocksControl control;
    private FineractClient fineractClient;
    private ResponseHandler responseHandler;

    private SdkUpdateAccountTransactionUseCase useCase;

    private SavingsAccountApi savingsAccountApi;

    @BeforeEach
    void setUp() {
        control = createStrictControl();
        fineractClient = control.createMock(FineractClient.class);
        responseHandler = control.createMock(ResponseHandler.class);

        useCase = SdkUpdateAccountTransactionUseCase.builder()
                .fineractClient(fineractClient)
                .transactionMapper(FineractTransactionMapper.INSTANCE)
                .responseHandler(responseHandler)
                .build();

        savingsAccountApi = control.createMock(SavingsAccountApi.class);
    }

    @Test
    void test_defaultInstance_creation() {
        assertThat(SdkUpdateAccountTransactionUseCase.defaultInstance(fineractClient)).isNotNull();
    }

    /**
     * Method under test: {@link SdkUpdateAccountTransactionUseCase#updateAccountTransactionsStatus(CustomerId, AccountId, TransactionsStatus)}
     */
    @Test
    void test_updateAccountTransactionsStatus() {
        CustomerId customerId = CustomerId.of(123L);
        AccountId accountId = AccountId.of(32L);
        TransactionsStatus status = TransactionsStatus.BLOCKED;
        PostSavingsAccountsAccountIdRequest request = new PostSavingsAccountsAccountIdRequest();
        request.setReasonForBlock("Reason For Block");
        String command = "block";
        Call<PostSavingsAccountsAccountIdResponse> call = control.createMock(Call.class);

        expect(fineractClient.getSavingsAccounts()).andReturn(savingsAccountApi);
        expect(savingsAccountApi.handleSavingsAccountsCommands(accountId.getValue(), request, command)).andReturn(call);
        expect(responseHandler.getResponseBody(call)).andReturn(null);

        control.replay();

        assertDoesNotThrow(() -> useCase.updateAccountTransactionsStatus(customerId, accountId, status));

        control.verify();
    }

}