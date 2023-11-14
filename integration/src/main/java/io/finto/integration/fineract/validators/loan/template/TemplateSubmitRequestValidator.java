package io.finto.integration.fineract.validators.loan.template;

import io.finto.domain.bnpl.transaction.TransactionSubmit;

public interface TemplateSubmitRequestValidator {
    void validate(TransactionSubmit request);
}
