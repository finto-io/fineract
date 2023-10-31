package io.finto.integration.fineract.usecase.impl.loan;

import io.finto.domain.id.CustomerInternalId;
import io.finto.domain.id.fineract.LoanId;
import io.finto.domain.id.fineract.LoanStatus;
import io.finto.exceptions.core.FintoApiException;
import io.finto.exceptions.core.generic.BadRequestException;
import io.finto.fineract.sdk.api.LoansApi;
import io.finto.fineract.sdk.models.GetLoansLoanIdResponse;
import io.finto.fineract.sdk.models.GetLoansLoanIdStatus;
import io.finto.fineract.sdk.models.PostLoansLoanIdRequest;
import io.finto.fineract.sdk.models.PostLoansLoanIdResponse;
import io.finto.integration.fineract.converter.FineractLoanMapper;
import io.finto.integration.fineract.usecase.impl.SdkFineractUseCaseContext;
import io.finto.integration.fineract.validators.loan.LoanOwnershipValidator;
import io.finto.integration.fineract.validators.loan.LoanStatusValidator;
import org.easymock.IMocksControl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Call;

import static io.finto.fineract.sdk.Constants.LOCALE;
import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SdkSetLoanStatusUseCaseTest {

    private IMocksControl control;
    private SdkFineractUseCaseContext context;
    private FineractLoanMapper loanMapper;
    private LoanStatusValidator loanStatusValidator;
    private LoanOwnershipValidator loanOwnershipValidator;
    private SdkSetLoanStatusUseCase useCase;

    @BeforeEach
    void setUp() {
        control = createStrictControl();
        context = control.createMock(SdkFineractUseCaseContext.class);
        loanMapper = control.createMock(FineractLoanMapper.class);
        loanStatusValidator = control.createMock(LoanStatusValidator.class);
        loanOwnershipValidator = control.createMock(LoanOwnershipValidator.class);

        useCase = SdkSetLoanStatusUseCase.builder()
                .context(context)
                .loanMapper(loanMapper)
                .loanStatusValidator(loanStatusValidator)
                .loanOwnershipValidator(loanOwnershipValidator)
                .build();
    }

    /**
     * Method under test: {@link SdkSetLoanStatusUseCase#setLoanStatus(CustomerInternalId, LoanId, LoanStatus)}
     */
    @Test
    void setLoanStatus_success() {
        CustomerInternalId customerInternalId = control.createMock(CustomerInternalId.class);
        LoanId loanId = LoanId.of(1L);
        LoanStatus loanStatus = LoanStatus.APPROVED;
        LoansApi loansApi = control.createMock(LoansApi.class);
        Call<GetLoansLoanIdResponse> responseGetLoan = control.createMock(Call.class);
        Call<PostLoansLoanIdResponse> responseCall = control.createMock(Call.class);
        GetLoansLoanIdResponse loanResponse = control.createMock(GetLoansLoanIdResponse.class);
        GetLoansLoanIdStatus loanIdStatus = control.createMock(GetLoansLoanIdStatus.class);
        PostLoansLoanIdRequest request = PostLoansLoanIdRequest.builder()
                .locale(LOCALE)
                .build();

        expect(context.loanApi()).andStubReturn(loansApi);
        expect(loansApi.retrieveLoan(1L, null, null, null, null))
                .andReturn(responseGetLoan);
        expect(context.getResponseBody(responseGetLoan)).andReturn(loanResponse);

        loanOwnershipValidator.validateLoanOwnership(customerInternalId, loanResponse);
        expectLastCall();

        expect(loanResponse.getStatus()).andReturn(loanIdStatus).times(2);
        expect(loanIdStatus.getValue()).andReturn("Submitted and pending approval");

        expect(loanStatusValidator.validateStatusChange("Submitted and pending approval", loanStatus))
                .andReturn(true);
        expect(loanMapper.toRequestWithDate(loanStatus)).andReturn(request);
        expect(loanMapper.toCommandDto(loanStatus)).andReturn(io.finto.integration.fineract.dto.enums.LoanStatus.APPROVED);

        expect(loansApi.stateTransitions(1L, request, "approve")).andReturn(responseCall);
        expect(context.getResponseBody(responseCall)).andReturn(null);

        control.replay();

        useCase.setLoanStatus(customerInternalId, loanId, loanStatus);

        control.verify();
    }

    /**
     * Method under test: {@link SdkSetLoanStatusUseCase#setLoanStatus(CustomerInternalId, LoanId, LoanStatus)}
     */
    @Test
    void setLoanStatus_failure() {
        CustomerInternalId customerInternalId = control.createMock(CustomerInternalId.class);
        LoanId loanId = LoanId.of(1L);
        LoanStatus loanStatus = LoanStatus.APPROVED;
        LoansApi loansApi = control.createMock(LoansApi.class);
        Call<GetLoansLoanIdResponse> responseGetLoan = control.createMock(Call.class);
        GetLoansLoanIdResponse loanResponse = control.createMock(GetLoansLoanIdResponse.class);
        GetLoansLoanIdStatus loanIdStatus = control.createMock(GetLoansLoanIdStatus.class);

        expect(context.loanApi()).andStubReturn(loansApi);
        expect(loansApi.retrieveLoan(1L, null, null, null, null))
                .andReturn(responseGetLoan);
        expect(context.getResponseBody(responseGetLoan)).andReturn(loanResponse);

        loanOwnershipValidator.validateLoanOwnership(customerInternalId, loanResponse);
        expectLastCall();

        expect(loanResponse.getStatus()).andReturn(loanIdStatus).times(2);
        expect(loanIdStatus.getValue()).andReturn("Submitted and pending approval");

        expect(loanStatusValidator.validateStatusChange("Submitted and pending approval", loanStatus))
                .andReturn(false);

        control.replay();

        assertThrows(BadRequestException.class, () -> useCase.setLoanStatus(customerInternalId, loanId, loanStatus));

        control.verify();
    }

    /**
     * Method under test: {@link SdkSetLoanStatusUseCase#setLoanStatus(CustomerInternalId, LoanId, LoanStatus)}
     */
    @Test
    void setLoanStatus_loanStatusReturnNull_failure() {
        CustomerInternalId customerInternalId = control.createMock(CustomerInternalId.class);
        LoanId loanId = LoanId.of(1L);
        LoanStatus loanStatus = LoanStatus.APPROVED;
        LoansApi loansApi = control.createMock(LoansApi.class);
        Call<GetLoansLoanIdResponse> responseGetLoan = control.createMock(Call.class);
        GetLoansLoanIdResponse loanResponse = control.createMock(GetLoansLoanIdResponse.class);

        expect(context.loanApi()).andStubReturn(loansApi);
        expect(loansApi.retrieveLoan(1L, null, null, null, null))
                .andReturn(responseGetLoan);
        expect(context.getResponseBody(responseGetLoan)).andReturn(loanResponse);

        loanOwnershipValidator.validateLoanOwnership(customerInternalId, loanResponse);
        expectLastCall();

        expect(loanResponse.getStatus()).andReturn(null);

        control.replay();

        assertThrows(FintoApiException.class, () -> useCase.setLoanStatus(customerInternalId, loanId, loanStatus));

        control.verify();
    }

}