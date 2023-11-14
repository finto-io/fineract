package io.finto.integration.fineract.validators.loan.template;

import io.finto.domain.bnpl.loan.LoanShortInfo;
import io.finto.domain.bnpl.transaction.TransactionSubmit;

public interface TemplateDateValidator {
    void validate(TransactionSubmit request, LoanShortInfo loan);
}
