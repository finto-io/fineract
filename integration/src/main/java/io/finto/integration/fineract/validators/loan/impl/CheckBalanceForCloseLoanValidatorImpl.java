package io.finto.integration.fineract.validators.loan.impl;

import io.finto.exceptions.core.generic.BadRequestException;
import io.finto.fineract.sdk.models.GetLoansLoanIdResponse;
import io.finto.integration.fineract.validators.loan.CheckBalanceForCloseLoanValidator;

import java.util.Objects;

public class CheckBalanceForCloseLoanValidatorImpl implements CheckBalanceForCloseLoanValidator {
    private static final Double EPSILON = 0.000001d;
    @Override
    public void validate(GetLoansLoanIdResponse loan) {
        String currencyCode = Objects.requireNonNull(loan.getCurrency()).getCode();
        Double balance = Objects.requireNonNull(
                Objects.requireNonNull(loan.getSummary()).getTotalOutstanding()
        );

        if (Math.abs(balance) > EPSILON) {
            throw new BadRequestException("400057",
                    String.format("You cannot close this loan due to an outstanding balance of [%s] [%s]", currencyCode, balance)
            );
        }

    }
}
