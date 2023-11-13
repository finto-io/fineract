package io.finto.integration.fineract.validators.loan.template;

import io.finto.domain.bnpl.transaction.TransactionSubmit;
import io.finto.fineract.sdk.models.GetLoansLoanIdResponse;

public interface TemplateDateValidator {
    void validate(TransactionSubmit request, GetLoansLoanIdResponse loan);
}
