package io.finto.integration.fineract.validators.loan.template.impl;

import io.finto.domain.id.CustomerInternalId;
import io.finto.exceptions.core.generic.BadRequestException;
import io.finto.fineract.sdk.models.GetLoansLoanIdResponse;
import io.finto.integration.fineract.validators.loan.template.TemplateClientValidator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TemplateClientValidatorImpl implements TemplateClientValidator {
    @Override
    public void validate(CustomerInternalId customerInternalId, GetLoansLoanIdResponse loan) {
        if (!customerInternalId.getAsLong().equals(loan.getClientId())) {
            throw new BadRequestException(BadRequestException.DEFAULT_ERROR_CODE, "Incorrect customer");
        }
    }
}

