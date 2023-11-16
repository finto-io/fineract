package io.finto.integration.fineract.usecase.impl.loan.product;

import io.finto.domain.bnpl.schedule.Schedule;
import io.finto.domain.bnpl.schedule.ScheduleCalculate;
import io.finto.domain.id.fineract.LoanProductId;
import io.finto.domain.loanproduct.LoanProduct;
import io.finto.fineract.sdk.api.LoanProductsApi;
import io.finto.fineract.sdk.api.LoansApi;
import io.finto.fineract.sdk.models.GetLoanProductsProductIdResponse;
import io.finto.fineract.sdk.models.PostLoansRequest;
import io.finto.fineract.sdk.models.PostLoansResponse;
import io.finto.integration.fineract.converter.FineractLoanProductMapper;
import io.finto.integration.fineract.usecase.impl.SdkFineractUseCaseContext;
import org.easymock.IMocksControl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Call;

import java.util.function.Function;

import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SdkCalculateLoanScheduleUseCaseTest {
    private IMocksControl control;
    private SdkFineractUseCaseContext context;
    private FineractLoanProductMapper loanProductMapper;
    private LoanProduct loanProduct;
    private Function<LoanProductId, LoanProduct> getLoanProductResolver;
    private SdkCalculateLoanScheduleUseCase sdkCalculateLoanScheduleUseCase;

    @BeforeEach
    void setUp() {
        control = createStrictControl();
        context = control.createMock(SdkFineractUseCaseContext.class);
        loanProductMapper = control.createMock(FineractLoanProductMapper.class);

        loanProduct = control.createMock(LoanProduct.class);
        getLoanProductResolver = (LoanProductId id) -> loanProduct;

        sdkCalculateLoanScheduleUseCase = SdkCalculateLoanScheduleUseCase.builder()
                .context(context)
                .loanProductMapper(loanProductMapper)
                .getLoanProduct(getLoanProductResolver)
                .build();
    }

    /**
     * Method under test: {@link SdkCalculateLoanScheduleUseCase#calculateLoanSchedule(LoanProductId, ScheduleCalculate)}
     */
    @Test
    void test_calculateLoanSchedule() {
        LoanProductId loanProductId = LoanProductId.of(13L);
        ScheduleCalculate request = control.createMock(ScheduleCalculate.class);
        int digitsAfterDecimal = 3;

        // retrieve loan product
        LoanProductsApi loanProductsApiMock = control.createMock(LoanProductsApi.class);
        expect(context.loanProductApi())
                .andReturn(loanProductsApiMock);
        Call<GetLoanProductsProductIdResponse> apiCallMock = control.createMock(Call.class);
        expect(loanProductsApiMock.retrieveLoanProductDetails(loanProductId.getValue()))
                .andReturn(apiCallMock);
        GetLoanProductsProductIdResponse getLoanProductsProductIdResponseMock = control.createMock(GetLoanProductsProductIdResponse.class);
        expect(context.getResponseBody(apiCallMock))
                .andReturn(getLoanProductsProductIdResponseMock);

        // create request for schedule calculation
        PostLoansRequest postLoansRequestMock = control.createMock(PostLoansRequest.class);
        expect(loanProductMapper.loanScheduleCalculationFineractRequest(loanProductId, request, getLoanProductsProductIdResponseMock))
                .andReturn(postLoansRequestMock);

        // send request to calculate schedule
        LoansApi loansApiMock = control.createMock(LoansApi.class);
        expect(context.loanApi())
                .andReturn(loansApiMock);
        Call<PostLoansResponse> loansApiCallMock = control.createMock(Call.class);
        expect(loansApiMock.calculateLoanScheduleOrSubmitLoanApplication(postLoansRequestMock, "calculateLoanSchedule"))
                .andReturn(loansApiCallMock);
        PostLoansResponse postLoansResponseMock = control.createMock(PostLoansResponse.class);
        expect(context.getResponseBody(loansApiCallMock))
                .andReturn(postLoansResponseMock);

        // mapper
        expect(request.getDigitsAfterDecimal())
                .andReturn(digitsAfterDecimal);
        Schedule expected = Schedule.builder().build();
        expect(loanProductMapper.toSchedule(postLoansRequestMock, postLoansResponseMock, digitsAfterDecimal))
                .andReturn(expected);

        control.replay();
        var actual = sdkCalculateLoanScheduleUseCase.calculateLoanSchedule(loanProductId, request);
        control.verify();

        assertEquals(expected, actual);

    }

}