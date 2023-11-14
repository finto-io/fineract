package io.finto.integration.fineract.validators.loan;

import io.finto.fineract.sdk.models.GetLoansLoanIdResponse;

public interface CheckBalanceForCloseLoanValidator {
    void validate(GetLoansLoanIdResponse loan);
}
