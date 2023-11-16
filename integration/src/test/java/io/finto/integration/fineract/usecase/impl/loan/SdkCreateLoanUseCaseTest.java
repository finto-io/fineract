package io.finto.integration.fineract.usecase.impl.loan;

import io.finto.domain.bnpl.loan.LoanCreate;
import io.finto.domain.id.CustomerInternalId;
import io.finto.domain.id.fineract.LoanId;
import io.finto.domain.loanproduct.LoanProductCreate;
import io.finto.fineract.sdk.api.LoanProductsApi;
import io.finto.fineract.sdk.api.LoansApi;
import io.finto.fineract.sdk.models.GetLoanProductsProductIdResponse;
import io.finto.fineract.sdk.models.PostLoansRequest;
import io.finto.fineract.sdk.models.PostLoansResponse;
import io.finto.integration.fineract.converter.FineractLoanProductMapper;
import io.finto.integration.fineract.usecase.impl.SdkFineractUseCaseContext;
import io.finto.integration.fineract.usecase.impl.loan.SdkCreateLoanUseCase;
import io.finto.integration.fineract.usecase.impl.loan.product.SdkCreateLoanProductUseCase;
import org.easymock.IMocksControl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Call;

import static org.assertj.core.api.Assertions.assertThat;
import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;

class SdkCreateLoanUseCaseTest {
    private IMocksControl control;
    private SdkFineractUseCaseContext context;
    private FineractLoanProductMapper loanProductMapper;
    private SdkCreateLoanUseCase useCase;

    @BeforeEach
    void setUp() {
        control = createStrictControl();
        context = control.createMock(SdkFineractUseCaseContext.class);
        loanProductMapper = control.createMock(FineractLoanProductMapper.class);
        useCase = SdkCreateLoanUseCase.builder()
                .context(context)
                .loanProductMapper(loanProductMapper)
                .build();
    }

    /**
     * Method under test: {@link SdkCreateLoanProductUseCase#createLoanProduct(LoanProductCreate)}
     */
    @Test
    void test_createLoan() {
        CustomerInternalId customerInternalId = CustomerInternalId.of(1L);
        LoanCreate request = control.createMock(LoanCreate.class);
        Call<GetLoanProductsProductIdResponse> responseGetLoanProduct = control.createMock(Call.class);
        GetLoanProductsProductIdResponse bodyGetLoanProduct = control.createMock(GetLoanProductsProductIdResponse.class);
        PostLoansRequest postLoansRequest = control.createMock(PostLoansRequest.class);
        LoansApi loansApi = control.createMock(LoansApi.class);
        Call<PostLoansResponse> responsePostLoan = control.createMock(Call.class);
        PostLoansResponse bodyPostLoan = control.createMock(PostLoansResponse.class);
        LoanProductsApi loanProductsApi = control.createMock(LoanProductsApi.class);

        expect(context.loanProductApi()).andReturn(loanProductsApi);
        expect(request.getProductId()).andReturn(2L);
        expect(loanProductsApi.retrieveLoanProductDetails(2L)).andReturn(responseGetLoanProduct);
        expect(context.getResponseBody(responseGetLoanProduct)).andReturn(bodyGetLoanProduct);
        expect(loanProductMapper.loanCreationFineractRequest(1L, request, bodyGetLoanProduct))
                .andReturn(postLoansRequest);
        expect(context.loanApi()).andReturn(loansApi);
        expect(loansApi.calculateLoanScheduleOrSubmitLoanApplication(postLoansRequest, null))
                .andReturn(responsePostLoan);
        expect(context.getResponseBody(responsePostLoan)).andReturn(bodyPostLoan);
        expect(bodyPostLoan.getResourceId()).andReturn(3L);
        control.replay();

        LoanId actual = useCase.createLoan(customerInternalId, request);

        control.verify();

        assertThat(actual).isEqualTo(LoanId.of(3L));
    }

}