package io.finto.integration.fineract.usecase.impl.loan.transaction;

import io.finto.domain.bnpl.transaction.Transaction;
import io.finto.domain.id.fineract.LoanId;
import io.finto.domain.id.fineract.TransactionId;
import io.finto.fineract.sdk.api.LoanTransactionsApi;
import io.finto.fineract.sdk.models.GetLoansLoanIdTransactionsTransactionIdResponse;
import io.finto.integration.fineract.converter.FineractLoanTransactionMapper;
import io.finto.integration.fineract.usecase.impl.SdkFineractUseCaseContext;
import org.easymock.IMocksControl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Call;

import static org.easymock.EasyMock.createMockBuilder;
import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SdkFindLoanTransactionUseCaseTest {
    private IMocksControl control;
    private SdkFineractUseCaseContext context;
    private FineractLoanTransactionMapper loanTransactionMapper;
    private SdkFindLoanTransactionUseCase useCase;

    @BeforeEach
    void setUp() {
        control = createStrictControl();
        context = control.createMock(SdkFineractUseCaseContext.class);
        loanTransactionMapper = control.createMock(FineractLoanTransactionMapper.class);
        useCase = createMockBuilder(SdkFindLoanTransactionUseCase.class)
                .withConstructor(context, loanTransactionMapper)
                .createMock(control);
    }

    /**
     * Method under test: {@link SdkFindLoanTransactionUseCase#findLoanTransaction(LoanId, TransactionId)}
     */
    @Test
    void test_findLoanTransaction_success() {
        LoanId loanId = LoanId.of(13L);
        TransactionId transactionId = TransactionId.of(17L);

        LoanTransactionsApi fineractApiMock = control.createMock(LoanTransactionsApi.class);
        expect(context.loanTransactionApi())
                .andReturn(fineractApiMock);

        Call<GetLoansLoanIdTransactionsTransactionIdResponse> fineractApiCallMock = control.createMock(Call.class);
        expect(fineractApiMock.retrieveTransaction(loanId.getValue(), transactionId.getValue(), null))
                .andReturn(fineractApiCallMock);

        GetLoansLoanIdTransactionsTransactionIdResponse fineractApiCallResponseMock = control.createMock(GetLoansLoanIdTransactionsTransactionIdResponse.class);
        expect(context.getResponseBody(fineractApiCallMock))
                .andReturn(fineractApiCallResponseMock);

        var expected = Transaction.builder()
                .build();
        expect(loanTransactionMapper.toDomainBnplTransaction(fineractApiCallResponseMock))
                .andReturn(expected);

        control.replay();
        var actual = useCase.findLoanTransaction(loanId, transactionId);
        control.verify();

        assertEquals(expected, actual);
    }


}