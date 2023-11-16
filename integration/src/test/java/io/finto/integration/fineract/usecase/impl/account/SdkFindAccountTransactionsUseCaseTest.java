package io.finto.integration.fineract.usecase.impl.account;

import io.finto.domain.account.AccountId;
import io.finto.domain.transaction.Transaction;
import io.finto.fineract.sdk.api.SavingsAccountApi;
import io.finto.fineract.sdk.models.GetSavingsAccountsAccountIdResponse;
import io.finto.fineract.sdk.models.GetSavingsAccountsAccountIdTransactionsResponse;
import io.finto.integration.fineract.converter.FineractTransactionMapper;
import io.finto.integration.fineract.usecase.impl.SdkFineractUseCaseContext;
import org.easymock.IMocksControl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Call;

import java.util.List;

import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SdkFindAccountTransactionsUseCaseTest {

    private IMocksControl control;
    private SdkFineractUseCaseContext context;
    private FineractTransactionMapper fineractTransactionMapper;
    private SdkFindAccountTransactionsUseCase sdkFindAccountTransactionsUseCase;

    @BeforeEach
    void setUp() {
        control = createStrictControl();
        context = control.createMock(SdkFineractUseCaseContext.class);
        fineractTransactionMapper = control.createMock(FineractTransactionMapper.class);
        sdkFindAccountTransactionsUseCase = SdkFindAccountTransactionsUseCase.builder()
                .context(context)
                .transactionMapper(fineractTransactionMapper)
                .build();
    }

    /**
     * Method under test: {@link SdkFindAccountTransactionsUseCase#findTransactionList(AccountId)}
     */
    @Test
    void test_findTransactionList_success() {
        AccountId accountId = AccountId.of(123L);

        SavingsAccountApi savingsAccountApiMock = control.createMock(SavingsAccountApi.class);
        expect(context.savingsAccountApi())
                .andReturn(savingsAccountApiMock);

        Call<GetSavingsAccountsAccountIdResponse> apiCallMock = control.createMock(Call.class);
        expect(savingsAccountApiMock.retrieveOneSavingsAccount(accountId.getValue(), null, null, "transactions"))
                .andReturn(apiCallMock);

        GetSavingsAccountsAccountIdTransactionsResponse transactionsResponse1 = GetSavingsAccountsAccountIdTransactionsResponse.builder()
                .build();
        GetSavingsAccountsAccountIdResponse apiResponse = GetSavingsAccountsAccountIdResponse.builder()
                .transactions(List.of(transactionsResponse1))
                .build();
        expect(context.getResponseBody(apiCallMock))
                .andReturn(apiResponse);

        Transaction transaction1 = Transaction.builder().build();
        List<Transaction> expected = List.of(transaction1);
        expect(fineractTransactionMapper.toTransaction(transactionsResponse1))
                .andReturn(transaction1);

        control.replay();

        List<Transaction> actual = sdkFindAccountTransactionsUseCase.findTransactionList(accountId);

        control.verify();

        assertEquals(expected, actual);
    }

    /**
     * Method under test: {@link SdkFindAccountTransactionsUseCase#findTransactionList(AccountId)}
     */
    @Test
    void test_findTransactionList_success_emptyList() {
        AccountId accountId = AccountId.of(123L);

        SavingsAccountApi savingsAccountApiMock = control.createMock(SavingsAccountApi.class);
        expect(context.savingsAccountApi())
                .andReturn(savingsAccountApiMock);

        Call<GetSavingsAccountsAccountIdResponse> apiCallMock = control.createMock(Call.class);
        expect(savingsAccountApiMock.retrieveOneSavingsAccount(accountId.getValue(), null, null, "transactions"))
                .andReturn(apiCallMock);

        GetSavingsAccountsAccountIdResponse apiResponse = GetSavingsAccountsAccountIdResponse.builder()
                .transactions(List.of())
                .build();
        expect(context.getResponseBody(apiCallMock))
                .andReturn(apiResponse);

        List<Transaction> expected = List.of();

        control.replay();

        List<Transaction> actual = sdkFindAccountTransactionsUseCase.findTransactionList(accountId);

        control.verify();

        assertEquals(expected, actual);
    }

}