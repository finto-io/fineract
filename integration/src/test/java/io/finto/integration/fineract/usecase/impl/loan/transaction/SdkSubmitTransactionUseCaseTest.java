package io.finto.integration.fineract.usecase.impl.loan.transaction;

import io.finto.domain.bnpl.enums.LoanTransactionType;
import io.finto.domain.bnpl.loan.LoanShortInfo;
import io.finto.domain.bnpl.transaction.Transaction;
import io.finto.domain.bnpl.transaction.TransactionSubmit;
import io.finto.domain.id.CustomerInternalId;
import io.finto.domain.id.fineract.LoanId;
import io.finto.exceptions.core.generic.BadRequestException;
import io.finto.fineract.sdk.api.LoanTransactionsApi;
import io.finto.fineract.sdk.api.PaymentTypeApi;
import io.finto.fineract.sdk.models.GetLoansLoanIdTransactionsTransactionIdResponse;
import io.finto.fineract.sdk.models.GetPaymentTypesPaymentTypeIdResponse;
import io.finto.fineract.sdk.models.PostLoansLoanIdTransactionsRequest;
import io.finto.fineract.sdk.models.PostLoansLoanIdTransactionsResponse;
import io.finto.integration.fineract.converter.FineractLoanTransactionMapper;
import io.finto.integration.fineract.usecase.impl.SdkFineractUseCaseContext;
import io.finto.integration.fineract.validators.loan.template.TemplateClientValidator;
import io.finto.integration.fineract.validators.loan.template.TemplateDateValidator;
import io.finto.integration.fineract.validators.loan.template.TemplateStatusValidator;
import io.finto.integration.fineract.validators.loan.template.TemplateSubmitRequestValidator;
import io.finto.usecase.loan.FindLoanUseCase;
import org.easymock.IMocksControl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Call;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;

class SdkSubmitTransactionUseCaseTest {
    private IMocksControl control;
    private SdkFineractUseCaseContext context;
    private FineractLoanTransactionMapper loanTransactionMapper;
    private TemplateClientValidator templateClientValidator;
    private TemplateStatusValidator templateStatusValidator;
    private TemplateDateValidator templateDateValidator;
    private TemplateSubmitRequestValidator templateSubmitRequestValidator;
    private SdkSubmitTransactionUseCase useCase;
    private FindLoanUseCase findLoanUseCase;

    @BeforeEach
    void setUp() {
        control = createStrictControl();
        context = control.createMock(SdkFineractUseCaseContext.class);
        loanTransactionMapper = control.createMock(FineractLoanTransactionMapper.class);
        templateClientValidator = control.createMock(TemplateClientValidator.class);
        templateStatusValidator = control.createMock(TemplateStatusValidator.class);
        templateDateValidator = control.createMock(TemplateDateValidator.class);
        templateSubmitRequestValidator = control.createMock(TemplateSubmitRequestValidator.class);
        findLoanUseCase = control.createMock(FindLoanUseCase.class);

        useCase = SdkSubmitTransactionUseCase.builder()
                .context(context)
                .loanTransactionMapper(loanTransactionMapper)
                .templateClientValidator(templateClientValidator)
                .templateStatusValidator(templateStatusValidator)
                .templateDateValidator(templateDateValidator)
                .templateSubmitRequestValidator(templateSubmitRequestValidator)
                .findLoanUseCase(findLoanUseCase)
                .build();
    }

    /**
     * Method under test: {@link SdkSubmitTransactionUseCase#submitTransaction(CustomerInternalId, LoanId, TransactionSubmit)}
     */
    @Test
    void test_submitTransaction_success_foreclosure() {
        CustomerInternalId customerInternalId = control.createMock(CustomerInternalId.class);
        LoanId loanId = control.createMock(LoanId.class);
        TransactionSubmit request = control.createMock(TransactionSubmit.class);
        LoanShortInfo loanShortInfo = control.createMock(LoanShortInfo.class);
        PostLoansLoanIdTransactionsRequest fineractRequest = control.createMock(PostLoansLoanIdTransactionsRequest.class);
        LoanTransactionsApi loanTransactionsApi = control.createMock(LoanTransactionsApi.class);
        Call<PostLoansLoanIdTransactionsResponse> responsePostLoanTransaction = control.createMock(Call.class);
        PostLoansLoanIdTransactionsResponse submittedTransaction = control.createMock(PostLoansLoanIdTransactionsResponse.class);
        Call<GetLoansLoanIdTransactionsTransactionIdResponse> responseGetLoanTransaction = control.createMock(Call.class);
        GetLoansLoanIdTransactionsTransactionIdResponse getTransaction = control.createMock(GetLoansLoanIdTransactionsTransactionIdResponse.class);
        Transaction transaction = control.createMock(Transaction.class);

        templateSubmitRequestValidator.validate(request);
        expect(loanId.getValue()).andReturn(1L);
        expect(findLoanUseCase.getLoanShortInfo(loanId, "clientId", "status", "timeline")).andReturn(loanShortInfo);
        templateClientValidator.validate(customerInternalId, loanShortInfo);
        templateStatusValidator.validate(loanShortInfo);
        templateDateValidator.validate(request, loanShortInfo);
        expect(request.getType()).andReturn(LoanTransactionType.FORECLOSURE);
        expect(loanTransactionMapper.loanTransactionSubmissionForeclosure(request)).andReturn(fineractRequest);
        expect(context.loanTransactionApi()).andReturn(loanTransactionsApi);
        expect(loanTransactionMapper.toCommand(LoanTransactionType.FORECLOSURE)).andReturn("foreclosure");
        expect(loanTransactionsApi.executeLoanTransaction(1L, fineractRequest, "foreclosure"))
                .andReturn(responsePostLoanTransaction);
        expect(context.getResponseBody(responsePostLoanTransaction)).andReturn(submittedTransaction);
        expect(submittedTransaction.getResourceId()).andReturn(3L);
        expect(loanTransactionsApi.retrieveTransaction(1L, 3L, null))
                .andReturn(responseGetLoanTransaction);
        expect(context.getResponseBody(responseGetLoanTransaction)).andReturn(getTransaction);
        expect(loanTransactionMapper.toDomainBnplTransaction(getTransaction)).andReturn(transaction);
        control.replay();

        Transaction actual = useCase.submitTransaction(customerInternalId, loanId, request);

        control.verify();

        assertThat(actual).isSameAs(transaction);
    }

    /**
     * Method under test: {@link SdkSubmitTransactionUseCase#submitTransaction(CustomerInternalId, LoanId, TransactionSubmit)}
     */
    @Test
    void test_submitTransaction_success_others() {
        CustomerInternalId customerInternalId = control.createMock(CustomerInternalId.class);
        LoanId loanId = control.createMock(LoanId.class);
        TransactionSubmit request = control.createMock(TransactionSubmit.class);
        LoanShortInfo loanShortInfo = control.createMock(LoanShortInfo.class);
        PostLoansLoanIdTransactionsRequest fineractRequest = control.createMock(PostLoansLoanIdTransactionsRequest.class);
        LoanTransactionsApi loanTransactionsApi = control.createMock(LoanTransactionsApi.class);
        Call<PostLoansLoanIdTransactionsResponse> responsePostLoanTransaction = control.createMock(Call.class);
        PostLoansLoanIdTransactionsResponse submittedTransaction = control.createMock(PostLoansLoanIdTransactionsResponse.class);
        Call<GetLoansLoanIdTransactionsTransactionIdResponse> responseGetLoanTransaction = control.createMock(Call.class);
        GetLoansLoanIdTransactionsTransactionIdResponse getTransaction = control.createMock(GetLoansLoanIdTransactionsTransactionIdResponse.class);
        Transaction transaction = control.createMock(Transaction.class);
        PaymentTypeApi paymentTypeApi = control.createMock(PaymentTypeApi.class);
        Call<GetPaymentTypesPaymentTypeIdResponse> responsePaymentType = control.createMock(Call.class);
        GetPaymentTypesPaymentTypeIdResponse paymentType = control.createMock(GetPaymentTypesPaymentTypeIdResponse.class);

        templateSubmitRequestValidator.validate(request);
        expect(loanId.getValue()).andReturn(1L);
        expect(findLoanUseCase.getLoanShortInfo(loanId, "clientId", "status", "timeline")).andReturn(loanShortInfo);
        templateClientValidator.validate(customerInternalId, loanShortInfo);
        templateStatusValidator.validate(loanShortInfo);
        templateDateValidator.validate(request, loanShortInfo);
        expect(request.getType()).andReturn(LoanTransactionType.REPAYMENT);
        expect(context.paymentTypeApi()).andReturn(paymentTypeApi);
        expect(request.getPaymentTypeId()).andReturn(4L);
        expect(paymentTypeApi.retrieveOnePaymentType(4L))
                .andReturn(responsePaymentType);
        expect(context.getResponseBody(responsePaymentType)).andReturn(paymentType);
        expect(loanTransactionMapper.loanTransactionSubmissionOther(request)).andReturn(fineractRequest);
        expect(context.loanTransactionApi()).andReturn(loanTransactionsApi);
        expect(loanTransactionMapper.toCommand(LoanTransactionType.REPAYMENT)).andReturn("repayment");
        expect(loanTransactionsApi.executeLoanTransaction(1L, fineractRequest, "repayment"))
                .andReturn(responsePostLoanTransaction);
        expect(context.getResponseBody(responsePostLoanTransaction)).andReturn(submittedTransaction);
        expect(submittedTransaction.getResourceId()).andReturn(3L);
        expect(loanTransactionsApi.retrieveTransaction(1L, 3L, null))
                .andReturn(responseGetLoanTransaction);
        expect(context.getResponseBody(responseGetLoanTransaction)).andReturn(getTransaction);
        expect(loanTransactionMapper.toDomainBnplTransaction(getTransaction)).andReturn(transaction);
        control.replay();

        Transaction actual = useCase.submitTransaction(customerInternalId, loanId, request);

        control.verify();

        assertThat(actual).isSameAs(transaction);
    }

    /**
     * Method under test: {@link SdkSubmitTransactionUseCase#submitTransaction(CustomerInternalId, LoanId, TransactionSubmit)}
     */
    @Test
    void test_submitTransaction_failDate() {
        CustomerInternalId customerInternalId = control.createMock(CustomerInternalId.class);
        LoanId loanId = control.createMock(LoanId.class);
        TransactionSubmit request = control.createMock(TransactionSubmit.class);
        LoanShortInfo loanShortInfo = control.createMock(LoanShortInfo.class);

        templateSubmitRequestValidator.validate(request);
        expect(loanId.getValue()).andReturn(1L);
        expect(findLoanUseCase.getLoanShortInfo(loanId, "clientId", "status", "timeline")).andReturn(loanShortInfo);
        templateClientValidator.validate(customerInternalId, loanShortInfo);
        templateStatusValidator.validate(loanShortInfo);
        templateDateValidator.validate(request, loanShortInfo);
        expectLastCall().andThrow(new BadRequestException());
        control.replay();

        assertThatThrownBy(() -> useCase.submitTransaction(customerInternalId, loanId, request))
                .isInstanceOf(BadRequestException.class);

        control.verify();
    }

    /**
     * Method under test: {@link SdkSubmitTransactionUseCase#submitTransaction(CustomerInternalId, LoanId, TransactionSubmit)}
     */
    @Test
    void test_submitTransaction_failStatus() {
        CustomerInternalId customerInternalId = control.createMock(CustomerInternalId.class);
        LoanId loanId = control.createMock(LoanId.class);
        TransactionSubmit request = control.createMock(TransactionSubmit.class);
        LoanShortInfo loanShortInfo = control.createMock(LoanShortInfo.class);

        templateSubmitRequestValidator.validate(request);
        expect(loanId.getValue()).andReturn(1L);
        expect(findLoanUseCase.getLoanShortInfo(loanId, "clientId", "status", "timeline")).andReturn(loanShortInfo);
        templateClientValidator.validate(customerInternalId, loanShortInfo);
        templateStatusValidator.validate(loanShortInfo);
        expectLastCall().andThrow(new BadRequestException());
        control.replay();

        assertThatThrownBy(() -> useCase.submitTransaction(customerInternalId, loanId, request))
                .isInstanceOf(BadRequestException.class);

        control.verify();
    }

    /**
     * Method under test: {@link SdkSubmitTransactionUseCase#submitTransaction(CustomerInternalId, LoanId, TransactionSubmit)}
     */
    @Test
    void test_submitTransaction_failCustomer() {
        CustomerInternalId customerInternalId = control.createMock(CustomerInternalId.class);
        LoanId loanId = control.createMock(LoanId.class);
        TransactionSubmit request = control.createMock(TransactionSubmit.class);
        LoanShortInfo loanShortInfo = control.createMock(LoanShortInfo.class);

        templateSubmitRequestValidator.validate(request);
        expect(loanId.getValue()).andReturn(1L);
        expect(findLoanUseCase.getLoanShortInfo(loanId, "clientId", "status", "timeline")).andReturn(loanShortInfo);
        templateClientValidator.validate(customerInternalId, loanShortInfo);
        expectLastCall().andThrow(new BadRequestException());
        control.replay();

        assertThatThrownBy(() -> useCase.submitTransaction(customerInternalId, loanId, request))
                .isInstanceOf(BadRequestException.class);

        control.verify();
    }

}