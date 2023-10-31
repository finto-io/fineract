package io.finto.integration.fineract.validators.loan;

import io.finto.domain.id.CustomerInternalId;
import io.finto.exceptions.core.generic.BadRequestException;
import io.finto.fineract.sdk.models.GetLoansLoanIdResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class LoanOwnershipValidator {
    public void validateLoanOwnership(CustomerInternalId customerInternalId, GetLoansLoanIdResponse loan) {
        if (loan.getClientId() != null && !customerInternalId.getValue().equals(loan.getClientId().toString())) {
            throw new BadRequestException("400056", "The loan does not belong to the customer");
        }
    }
}

