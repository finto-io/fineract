package io.finto.integration.fineract.validators.loan.template;

import io.finto.domain.bnpl.loan.LoanShortInfo;
import io.finto.domain.id.CustomerInternalId;

public interface TemplateClientValidator {
    void validate(CustomerInternalId customerInternalId, LoanShortInfo loan);
}
