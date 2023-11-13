package io.finto.integration.fineract.validators.loan.template.impl;

import io.finto.exceptions.core.generic.BadRequestException;
import io.finto.fineract.sdk.models.GetLoansLoanIdResponse;
import io.finto.integration.fineract.validators.loan.template.TemplateStatusValidator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TemplateStatusValidatorImpl implements TemplateStatusValidator {
    @Override
    public void validate(GetLoansLoanIdResponse loan) {
        var status = loan.getStatus();
        if (status != null && Boolean.FALSE.equals(status.getActive())) {
            throw new BadRequestException(BadRequestException.DEFAULT_ERROR_CODE,
                    String.format("Invalid loan transaction request (%s)", "loan is inactive"));
        }
    }
}

