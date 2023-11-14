package io.finto.integration.fineract.validators.loan.impl;

import io.finto.exceptions.core.generic.BadRequestException;
import io.finto.fineract.sdk.models.GetLoansLoanIdCurrency;
import io.finto.fineract.sdk.models.GetLoansLoanIdResponse;
import io.finto.fineract.sdk.models.GetLoansLoanIdSummary;
import org.easymock.IMocksControl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.easymock.EasyMock.createStrictControl;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CheckBalanceForCloseLoanValidatorImplTest {

    private CheckBalanceForCloseLoanValidatorImpl validator;
    private IMocksControl control;

    @BeforeEach
    void setUp() {
        control = createStrictControl();
        validator = new CheckBalanceForCloseLoanValidatorImpl();
    }

    private GetLoansLoanIdResponse createLoanWithBalance(Double balance) {

        return GetLoansLoanIdResponse.builder()
                .clientId(123L)
                .currency(GetLoansLoanIdCurrency.builder()
                        .code("JOD")
                        .build())
                .summary(GetLoansLoanIdSummary.builder()
                        .totalOutstanding(balance)
                        .build())
                .build();
    }

    @Test
    void test_validateWithZeroBalance_success() {
        GetLoansLoanIdResponse loan = createLoanWithBalance(0.0);
        validator.validate(loan);
    }

    @Test
    void test_validateWithNotZeroBalance_failure() {
        GetLoansLoanIdResponse loan = createLoanWithBalance(100.0);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> validator.validate(loan));

        assertTrue(exception.getErrorCode().contains("400057"));
        assertTrue(exception.getMessage().contains("You cannot close this loan due to an outstanding balance of [JOD] [100.0]"));
    }
}