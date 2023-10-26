package io.finto.integration.fineract.usecase.impl.loan;

import io.finto.domain.bnpl.loan.LoanShortInfo;
import io.finto.domain.id.CustomerInternalId;
import io.finto.domain.id.fineract.LoanId;
import io.finto.exceptions.core.FintoApiException;
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

    @Override
    public LoanShortInfo getLoanInfo(LoanId loanId) {
        var loan = context.getResponseBody(context.loanApi()
                .retrieveLoan(
                        loanId.getValue(),
                        null,
                        null,
                        null,
                        "clientId, status"
                )
        );
        if (loan.getClientId() == null || loan.getStatus() == null)
            throw new FintoApiException();
        return LoanShortInfo.builder()
                .customerInternalId(CustomerInternalId.of(loan.getClientId().toString()))
                .isPending(loan.getStatus().getPendingApproval())
                .build();
    }
}
