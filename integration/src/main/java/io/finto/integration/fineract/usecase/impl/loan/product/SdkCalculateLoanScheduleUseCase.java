package io.finto.integration.fineract.usecase.impl.loan.product;

import io.finto.domain.bnpl.schedule.Schedule;
import io.finto.domain.bnpl.schedule.ScheduleCalculate;
import io.finto.domain.id.fineract.LoanProductId;
import io.finto.domain.loanproduct.LoanProduct;
import io.finto.fineract.sdk.models.PostLoansRequest;
import io.finto.integration.fineract.converter.FineractLoanProductMapper;
import io.finto.integration.fineract.usecase.impl.SdkFineractUseCaseContext;
import io.finto.usecase.loan.product.CalculateLoanScheduleUseCase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;

import java.util.Objects;
import java.util.function.Function;

import static io.finto.fineract.sdk.Constants.CALCULATE_LOAN_SCHEDULE;

@AllArgsConstructor
@Builder
public class SdkCalculateLoanScheduleUseCase implements CalculateLoanScheduleUseCase {

    @NonNull
    private final SdkFineractUseCaseContext context;
    @NonNull
    private final FineractLoanProductMapper loanProductMapper;
    @NonNull
    private final Function<LoanProductId, LoanProduct> getLoanProduct;

    public static class SdkCalculateLoanScheduleUseCaseBuilder {
        private FineractLoanProductMapper loanProductMapper = FineractLoanProductMapper.INSTANCE;
    }

    @Override
    public Schedule calculateLoanSchedule(LoanProductId loanProductId, ScheduleCalculate request) {
        var loanProduct = context.getResponseBody(context.loanProductApi()
                .retrieveLoanProductDetails(loanProductId.getValue()));
        PostLoansRequest fineractRequest = loanProductMapper.loanScheduleCalculationFineractRequest(
                loanProductId, request, loanProduct
        );

        return loanProductMapper.toSchedule(fineractRequest, Objects.requireNonNull(context
                        .getResponseBody(
                                context.loanApi().calculateLoanScheduleOrSubmitLoanApplication(fineractRequest, CALCULATE_LOAN_SCHEDULE)
                        )),
                request.getDigitsAfterDecimal());
    }

}
