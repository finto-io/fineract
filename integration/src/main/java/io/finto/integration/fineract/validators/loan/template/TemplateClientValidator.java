package io.finto.integration.fineract.validators.loan.template;

import io.finto.domain.id.CustomerInternalId;
import io.finto.fineract.sdk.models.GetLoansLoanIdResponse;

public interface TemplateClientValidator {
    void validate(CustomerInternalId customerInternalId, GetLoansLoanIdResponse loan);
}
