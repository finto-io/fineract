package io.finto.integration.fineract.usecase.impl;

import io.finto.fineract.sdk.api.SavingsAccountApi;
import io.finto.fineract.sdk.models.DeleteSavingsAccountsAccountIdResponse;
import io.finto.fineract.sdk.util.FineractClient;
import io.finto.integration.fineract.common.ResponseHandler;
import io.finto.integration.fineract.domain.AccountId;
import org.easymock.IMocksControl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Call;

import static org.assertj.core.api.Assertions.assertThat;
import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class SdkDeleteAccountUseCaseTest {

    private IMocksControl control;
    private FineractClient fineractClient;
    private ResponseHandler responseHandler;
    private AccountId accountId = AccountId.of(10L);

    private SavingsAccountApi savingsAccountApi;

    private SdkDeleteAccountUseCase useCase;

    @BeforeEach
    void setUp() {
        control = createStrictControl();
        fineractClient = control.createMock(FineractClient.class);
        responseHandler = control.createMock(ResponseHandler.class);
        useCase = SdkDeleteAccountUseCase.builder()
                .fineractClient(fineractClient)
                .responseHandler(responseHandler)
                .build();

        savingsAccountApi = control.createMock(SavingsAccountApi.class);
    }

    @Test
    void test_defaultInstance_creation() {
        assertThat(SdkDeleteAccountUseCase.defaultInstance(fineractClient)).isNotNull();
    }

    /**
     * Method under test: {@link SdkDeleteAccountUseCase#deleteAccount(AccountId)}
     */
    @Test
    void test_deleteAccount_success() {
        Call<DeleteSavingsAccountsAccountIdResponse> removingCall = control.createMock(Call.class);

        expect(fineractClient.getSavingsAccounts()).andReturn(savingsAccountApi);
        expect(savingsAccountApi.deleteSavingsAccount(accountId.getValue())).andReturn(removingCall);
        expect(responseHandler.getResponseBody(removingCall)).andReturn(null);

        control.replay();

        assertDoesNotThrow(() -> useCase.deleteAccount(accountId));

        control.verify();
    }

}