package io.finto.integration.fineract.usecase.impl.loan.transaction;

import io.finto.domain.bnpl.enums.LoanTransactionType;
import io.finto.domain.bnpl.loan.LoanShortInfo;
import io.finto.domain.bnpl.transaction.TransactionTemplate;
import io.finto.domain.id.CustomerInternalId;
import io.finto.domain.id.fineract.LoanId;
import io.finto.fineract.sdk.api.LoanTransactionsApi;
import io.finto.fineract.sdk.api.LoansApi;
import io.finto.fineract.sdk.models.GetLoansLoanIdResponse;
import io.finto.fineract.sdk.models.GetLoansLoanIdTransactionsTemplateResponse;
import io.finto.integration.fineract.converter.FineractLoanTransactionMapper;
import io.finto.integration.fineract.usecase.impl.SdkFineractUseCaseContext;
import io.finto.integration.fineract.validators.loan.template.TemplateClientValidator;
import io.finto.integration.fineract.validators.loan.template.TemplateStatusValidator;
import io.finto.usecase.loan.FindLoanUseCase;
import org.easymock.IMocksControl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Call;

import static org.assertj.core.api.Assertions.assertThat;
import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;

class SdkFindLoanTransactionTemplateUseCaseTest {
    private IMocksControl control;
    private SdkFineractUseCaseContext context;
    private FineractLoanTransactionMapper loanTransactionMapper;
    private TemplateClientValidator templateClientValidator;
    private TemplateStatusValidator templateStatusValidator;
    private SdkFindLoanTransactionTemplateUseCase useCase;
    private FindLoanUseCase findLoanUseCase;

    @BeforeEach
    void setUp() {
        control = createStrictControl();
        context = control.createMock(SdkFineractUseCaseContext.class);
        loanTransactionMapper = control.createMock(FineractLoanTransactionMapper.class);
        templateClientValidator = control.createMock(TemplateClientValidator.class);
        templateStatusValidator = control.createMock(TemplateStatusValidator.class);
        findLoanUseCase = control.createMock(FindLoanUseCase.class);
        useCase = SdkFindLoanTransactionTemplateUseCase.builder()
                .context(context)
                .loanTransactionMapper(loanTransactionMapper)
                .templateClientValidator(templateClientValidator)
                .templateStatusValidator(templateStatusValidator)
                .findLoanUseCase(findLoanUseCase)
                .build();
    }

    /**
     * Method under test: {@link SdkFindLoanTransactionTemplateUseCase#findLoanTransactionTemplate(CustomerInternalId, LoanId, LoanTransactionType)}
     */
    @Test
    void test_findLoanTransaction_success() {
        CustomerInternalId customerInternalId = control.createMock(CustomerInternalId.class);
        LoanId loanId = control.createMock(LoanId.class);
        LoansApi loansApi = control.createMock(LoansApi.class);
        Call<GetLoansLoanIdResponse> responseLoan = control.createMock(Call.class);


        LoanShortInfo loanShortInfo = control.createMock(LoanShortInfo.class);
        LoanTransactionsApi loanTransactionsApi = control.createMock(LoanTransactionsApi.class);
        Call<GetLoansLoanIdTransactionsTemplateResponse> responseGetLoanTransactionTemplate = control.createMock(Call.class);
        GetLoansLoanIdTransactionsTemplateResponse getTransactionTemplate = control.createMock(GetLoansLoanIdTransactionsTemplateResponse.class);
        TransactionTemplate transactionTemplate = control.createMock(TransactionTemplate.class);

        expect(loanId.getValue()).andReturn(1L);

        expect(findLoanUseCase.getLoanShortInfo(loanId, "clientId", "status")).andReturn(loanShortInfo);

        templateClientValidator.validate(customerInternalId, loanShortInfo);
        templateStatusValidator.validate(loanShortInfo);

        expect(context.loanTransactionApi()).andReturn(loanTransactionsApi);
        expect(loanTransactionMapper.toCommand(LoanTransactionType.PREPAY_LOAN)).andReturn("test");
        expect(loanTransactionsApi.retrieveTransactionTemplate(1L, "test", null, null, null))
                .andReturn(responseGetLoanTransactionTemplate);
        expect(context.getResponseBody(responseGetLoanTransactionTemplate)).andReturn(getTransactionTemplate);
        expect(loanTransactionMapper.toDomainBnplTransactionTemplate(getTransactionTemplate)).andReturn(transactionTemplate);
        control.replay();

        var actual = useCase.findLoanTransactionTemplate(customerInternalId, loanId, LoanTransactionType.PREPAY_LOAN);

        control.verify();

        assertThat(actual).isSameAs(transactionTemplate);
    }

}