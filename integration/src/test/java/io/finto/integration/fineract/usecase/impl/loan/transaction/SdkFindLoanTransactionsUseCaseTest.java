package io.finto.integration.fineract.usecase.impl.loan.transaction;

import io.finto.domain.bnpl.transaction.Transaction;
import io.finto.domain.id.fineract.LoanId;
import io.finto.domain.id.fineract.TransactionId;
import io.finto.fineract.sdk.api.LoansApi;
import io.finto.fineract.sdk.models.GetLoansLoanIdResponse;
import io.finto.fineract.sdk.models.GetLoansLoanIdTransactions;
import io.finto.integration.fineract.converter.FineractLoanProductMapper;
import io.finto.integration.fineract.usecase.impl.SdkFineractUseCaseContext;
import org.easymock.IMocksControl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Call;

import java.util.List;

import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SdkFindLoanTransactionsUseCaseTest {
    private IMocksControl control;
    private SdkFineractUseCaseContext context;
    private FineractLoanProductMapper loanProductMapper;
    private SdkFindLoanTransactionsUseCase useCase;

    @BeforeEach
    void setUp() {
        control = createStrictControl();
        context = control.createMock(SdkFineractUseCaseContext.class);
        loanProductMapper = control.createMock(FineractLoanProductMapper.class);
        useCase = SdkFindLoanTransactionsUseCase.builder()
                .context(context)
                .loanProductMapper(loanProductMapper)
                .build();
    }

    /**
     * Method under test: {@link SdkFindLoanTransactionUseCase#findLoanTransaction(LoanId, TransactionId)}
     */
    @Test
    void test_findLoanTransactions_success() {
        LoanId loanId = LoanId.of(13L);

        LoansApi loansApiMock = control.createMock(LoansApi.class);
        expect(context.loanApi())
                .andReturn(loansApiMock);
        Call<GetLoansLoanIdResponse> fineractApiCallMock = control.createMock(Call.class);
        expect(loansApiMock.retrieveLoan(loanId.getValue(), false, "transactions", null, null))
                .andReturn(fineractApiCallMock);
        GetLoansLoanIdResponse getLoanResponseMock = control.createMock(GetLoansLoanIdResponse.class);
        expect(context.getResponseBody(fineractApiCallMock))
                .andReturn(getLoanResponseMock);
        List<GetLoansLoanIdTransactions> loanTransactions = control.createMock(List.class);
        expect(getLoanResponseMock.getTransactions())
                .andReturn(loanTransactions);

        Transaction transaction1 = Transaction.builder().build();
        List<Transaction> expected = List.of(transaction1);
        expect(loanProductMapper.toTransactions(loanTransactions))
                .andReturn(expected);

        control.replay();
        var actual = useCase.findLoanTransactions(loanId);
        control.verify();

        assertEquals(expected, actual);
    }


}