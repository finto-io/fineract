package io.finto.integration.fineract.validators.loan.template;

import io.finto.fineract.sdk.models.GetLoansLoanIdResponse;

public interface TemplateStatusValidator {
    void validate(GetLoansLoanIdResponse loan);
}
