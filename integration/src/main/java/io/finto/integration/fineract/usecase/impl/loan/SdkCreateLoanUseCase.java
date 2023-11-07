package io.finto.integration.fineract.usecase.impl.loan;

import io.finto.domain.bnpl.loan.LoanCreate;
import io.finto.domain.id.CustomerInternalId;
import io.finto.domain.id.fineract.LoanId;
import io.finto.fineract.sdk.models.PostLoansRequest;
import io.finto.integration.fineract.converter.FineractLoanProductMapper;
import io.finto.integration.fineract.usecase.impl.SdkFineractUseCaseContext;
import io.finto.usecase.loan.CreateLoanUseCase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;

import java.util.Objects;

@AllArgsConstructor
@Builder
public class SdkCreateLoanUseCase implements CreateLoanUseCase {

    @NonNull
    private final SdkFineractUseCaseContext context;
    @NonNull
    private final FineractLoanProductMapper loanProductMapper;

    public static class SdkCreateLoanUseCaseBuilder {
        private FineractLoanProductMapper loanProductMapper = FineractLoanProductMapper.INSTANCE;
    }

    @Override
    public LoanId createLoan(CustomerInternalId customerInternalId, LoanCreate request) {
        var loanProduct = context.getResponseBody(context.loanProductApi()
                .retrieveLoanProductDetails(request.getProductId()));

        PostLoansRequest fineractRequest = loanProductMapper.loanCreationFineractRequest(
                customerInternalId.getAsLong(), request, loanProduct
        );

        return LoanId.of(
                Objects.requireNonNull(context
                        .getResponseBody(
                                context.loanApi().calculateLoanScheduleOrSubmitLoanApplication(fineractRequest,
                                        null)
                        )
                        .getResourceId())
        );
    }

}
