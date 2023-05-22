package io.finto.integration.fineract.usecase.impl;

import io.finto.fineract.sdk.api.SavingsAccountApi;
import io.finto.fineract.sdk.models.DeleteSavingsAccountsAccountIdResponse;
import io.finto.integration.fineract.domain.AccountId;
import org.easymock.IMocksControl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Call;

import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class SdkDeleteAccountUseCaseTest {

    private IMocksControl control;
    private SdkFineractUseCaseContext context;
    private AccountId accountId = AccountId.of(10L);

    private SavingsAccountApi savingsAccountApi;

    private SdkDeleteAccountUseCase useCase;

    @BeforeEach
    void setUp() {
        control = createStrictControl();
        context = control.createMock(SdkFineractUseCaseContext.class);
        useCase = SdkDeleteAccountUseCase.builder().context(context).build();

        savingsAccountApi = control.createMock(SavingsAccountApi.class);
    }

    /**
     * Method under test: {@link SdkDeleteAccountUseCase#deleteAccount(AccountId)}
     */
    @Test
    void test_deleteAccount_success() {
        Call<DeleteSavingsAccountsAccountIdResponse> removingCall = control.createMock(Call.class);

        expect(context.savingsAccountApi()).andReturn(savingsAccountApi);
        expect(savingsAccountApi.deleteSavingsAccount(accountId.getValue())).andReturn(removingCall);
        expect(context.getResponseBody(removingCall)).andReturn(null);

        control.replay();

        assertDoesNotThrow(() -> useCase.deleteAccount(accountId));

        control.verify();
    }

}