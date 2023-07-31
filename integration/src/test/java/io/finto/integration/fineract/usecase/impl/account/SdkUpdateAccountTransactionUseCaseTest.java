package io.finto.integration.fineract.usecase.impl.account;

import io.finto.domain.account.AccountId;
import io.finto.domain.customer.CustomerId;
import io.finto.domain.transaction.TransactionsStatus;
import io.finto.fineract.sdk.api.SavingsAccountApi;
import io.finto.fineract.sdk.models.PostSavingsAccountsAccountIdRequest;
import io.finto.fineract.sdk.models.PostSavingsAccountsAccountIdResponse;
import io.finto.integration.fineract.converter.FineractTransactionMapper;
import io.finto.integration.fineract.usecase.impl.SdkFineractUseCaseContext;
import io.finto.integration.fineract.usecase.impl.account.SdkUpdateAccountTransactionUseCase;
import org.easymock.IMocksControl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Call;

import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class SdkUpdateAccountTransactionUseCaseTest {

    private IMocksControl control;
    private SdkFineractUseCaseContext context;

    private SdkUpdateAccountTransactionUseCase useCase;

    private SavingsAccountApi savingsAccountApi;

    @BeforeEach
    void setUp() {
        control = createStrictControl();
        context = control.createMock(SdkFineractUseCaseContext.class);

        useCase = SdkUpdateAccountTransactionUseCase.builder()
                .context(context)
                .transactionMapper(FineractTransactionMapper.INSTANCE)
                .build();

        savingsAccountApi = control.createMock(SavingsAccountApi.class);
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

        expect(context.savingsAccountApi()).andReturn(savingsAccountApi);
        expect(savingsAccountApi.handleSavingsAccountsCommands(accountId.getValue(), request, command)).andReturn(call);
        expect(context.getResponseBody(call)).andReturn(null);

        control.replay();

        assertDoesNotThrow(() -> useCase.updateAccountTransactionsStatus(customerId, accountId, status));

        control.verify();
    }

}