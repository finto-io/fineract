package io.finto.integration.fineract.validators.loan.template;

import io.finto.domain.bnpl.loan.LoanShortInfo;

public interface TemplateStatusValidator {
    void validate(LoanShortInfo loan);
}
