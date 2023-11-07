package io.finto.integration.fineract.usecase.impl.loan;

import io.finto.domain.id.CustomerInternalId;
import io.finto.domain.id.fineract.LoanId;
import io.finto.domain.id.fineract.LoanStatus;
import io.finto.exceptions.core.FintoApiException;
import io.finto.exceptions.core.generic.BadRequestException;
import io.finto.fineract.sdk.models.GetLoansLoanIdResponse;
import io.finto.fineract.sdk.models.PostLoansLoanIdRequest;
import io.finto.integration.fineract.converter.FineractLoanMapper;
import io.finto.integration.fineract.usecase.impl.SdkFineractUseCaseContext;
import io.finto.integration.fineract.validators.loan.LoanOwnershipValidator;
import io.finto.integration.fineract.validators.loan.LoanStatusValidator;
import io.finto.usecase.loan.SetLoanStatusUseCase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;

@AllArgsConstructor
@Builder
public class SdkSetLoanStatusUseCase implements SetLoanStatusUseCase {

    @NonNull
    private final SdkFineractUseCaseContext context;
    @NonNull
    private final FineractLoanMapper loanMapper;
    @NonNull
    private final LoanStatusValidator loanStatusValidator;
    @NonNull
    private final LoanOwnershipValidator loanOwnershipValidator;

    public static class SdkSetLoanStatusUseCaseBuilder {
        private FineractLoanMapper loanMapper = FineractLoanMapper.INSTANCE;
    }

    @Override
    public void setLoanStatus(CustomerInternalId customerInternalId, LoanId loanId, LoanStatus loanStatus) {

        GetLoansLoanIdResponse loan = getLoan(loanId);
        loanOwnershipValidator.validateLoanOwnership(customerInternalId, loan);

        if (loan.getStatus() == null) {
            throw new FintoApiException();
        }

        // Validate the status change
        if (!loanStatusValidator.validateStatusChange(loan.getStatus().getValue(), loanStatus)) {
            throw new BadRequestException("400055", "Invalid loan status change request");
        }
        // Create request depending on status
        PostLoansLoanIdRequest request = loanMapper.toRequestWithDate(loanStatus);
        // Change Status
        context.getResponseBody(context.loanApi().stateTransitions(loanId.getValue(), request, loanMapper.toCommandDto(loanStatus).getCommand()));

    }

    private GetLoansLoanIdResponse getLoan(LoanId loanId) {
        return context.getResponseBody(
                context.loanApi().retrieveLoan(loanId.getValue(), null, null, null, null)
        );
    }

}
