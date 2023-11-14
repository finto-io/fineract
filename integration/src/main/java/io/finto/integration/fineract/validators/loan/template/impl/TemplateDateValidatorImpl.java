package io.finto.integration.fineract.validators.loan.template.impl;

import io.finto.domain.bnpl.loan.LoanShortInfo;
import io.finto.domain.bnpl.transaction.TransactionSubmit;
import io.finto.exceptions.core.generic.BadRequestException;
import io.finto.integration.fineract.validators.loan.template.TemplateDateValidator;

import java.time.LocalDate;

public class TemplateDateValidatorImpl implements TemplateDateValidator {
    @Override
    public void validate(TransactionSubmit request, LoanShortInfo loan) {
        var actualDisbursementDate = loan.getActualDisbursementDate();
        var date = request.getDate();
        if (date.isBefore(actualDisbursementDate) || date.isAfter(getCurrentDate())) {
            throw new BadRequestException(BadRequestException.DEFAULT_ERROR_CODE,
                    String.format("Invalid loan transaction request (%s)", "date is invalid"));
        }
    }

    protected LocalDate getCurrentDate() {
        return LocalDate.now();
    }
}

