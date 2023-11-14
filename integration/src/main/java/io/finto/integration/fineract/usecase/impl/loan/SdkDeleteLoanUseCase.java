package io.finto.integration.fineract.usecase.impl.loan;

import io.finto.domain.id.fineract.LoanId;
import io.finto.integration.fineract.usecase.impl.SdkFineractUseCaseContext;
import io.finto.usecase.loan.DeleteLoanUseCase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;

@AllArgsConstructor
@Builder
public class SdkDeleteLoanUseCase implements DeleteLoanUseCase {

    @NonNull
    private final SdkFineractUseCaseContext context;

    @Override
    public LoanId deleteLoan(LoanId loanId) {
        var deleteLoanResult = context.getResponseBody(context.loanApi()
                .deleteLoanApplication(loanId.getValue()));
        assert deleteLoanResult.getLoanId() != null;
        return LoanId.of(deleteLoanResult.getLoanId());
    }

}
