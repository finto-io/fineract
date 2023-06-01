package io.finto.integration.fineract.usecase.impl;

import io.finto.fineract.sdk.api.DataTablesApi;
import io.finto.fineract.sdk.api.SavingsAccountApi;
import io.finto.fineract.sdk.models.DeleteDataTablesDatatableAppTableIdResponse;
import io.finto.fineract.sdk.models.DeleteSavingsAccountsAccountIdResponse;
import io.finto.integration.fineract.domain.AccountId;
import org.easymock.IMocksControl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Call;

import static io.finto.fineract.sdk.CustomDatatableNames.ACCOUNT_ADDITIONAL_FIELDS;
import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class SdkDeleteAccountUseCaseTest {

    private IMocksControl control;
    private SdkFineractUseCaseContext context;
    private AccountId accountId = AccountId.of(10L);

    private SavingsAccountApi savingsAccountApi;
    private DataTablesApi dataTablesApi;

    private SdkDeleteAccountUseCase useCase;

    @BeforeEach
    void setUp() {
        control = createStrictControl();
        context = control.createMock(SdkFineractUseCaseContext.class);
        useCase = SdkDeleteAccountUseCase.builder().context(context).build();

        savingsAccountApi = control.createMock(SavingsAccountApi.class);
        dataTablesApi = control.createMock(DataTablesApi.class);
    }

    /**
     * Method under test: {@link SdkDeleteAccountUseCase#deleteAccount(AccountId)}
     */
    @Test
    void test_deleteAccount_success() {
        Call<DeleteDataTablesDatatableAppTableIdResponse> cleanAdditionalCall = control.createMock(Call.class);
        Call<DeleteSavingsAccountsAccountIdResponse> removingCall = control.createMock(Call.class);

        expect(context.dataTablesApi()).andReturn(dataTablesApi);
        expect(dataTablesApi.deleteDatatableEntries(ACCOUNT_ADDITIONAL_FIELDS, accountId.getValue())).andReturn(cleanAdditionalCall);
        expect(context.getResponseBody(cleanAdditionalCall)).andReturn(null);

        expect(context.savingsAccountApi()).andReturn(savingsAccountApi);
        expect(savingsAccountApi.deleteSavingsAccount(accountId.getValue())).andReturn(removingCall);
        expect(context.getResponseBody(removingCall)).andReturn(null);

        control.replay();

        assertDoesNotThrow(() -> useCase.deleteAccount(accountId));

        control.verify();
    }

}