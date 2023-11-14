package io.finto.integration.fineract.validators.loan.template.impl;

import io.finto.domain.bnpl.enums.LoanTransactionType;
import io.finto.domain.bnpl.transaction.TransactionSubmit;
import io.finto.exceptions.core.generic.BadRequestException;
import io.finto.integration.fineract.validators.loan.template.TemplateSubmitRequestValidator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@AllArgsConstructor
public class TemplateSubmitRequestValidatorImpl implements TemplateSubmitRequestValidator {
    @Override
    public void validate(TransactionSubmit request) {
        if (request.getType() != null) {
            String BAD_REQUEST_MESSAGE = "Invalid loan transaction request (%s)";
            if (request.getType() == LoanTransactionType.PREPAY_LOAN ||
                    request.getType() == LoanTransactionType.REPAYMENT) {
                if (request.getAmount() == null) {
                    throw new BadRequestException(BadRequestException.DEFAULT_ERROR_CODE,
                            String.format(BAD_REQUEST_MESSAGE, "amount is required"));
                }
                if (request.getPaymentTypeId() == null) {
                    throw new BadRequestException(BadRequestException.DEFAULT_ERROR_CODE,
                            String.format(BAD_REQUEST_MESSAGE, "paymentTypeId is required"));
                }
            }
            if (request.getType() == LoanTransactionType.FORECLOSURE) {
                if (!request.getDate().isEqual(LocalDate.now())) {
                    throw new BadRequestException(BadRequestException.DEFAULT_ERROR_CODE,
                            String.format(BAD_REQUEST_MESSAGE, "date must be equal to current date"));
                }
            }
        }
    }
}

