package io.finto.integration.fineract.validators.loan;

import io.finto.domain.id.CustomerInternalId;
import io.finto.exceptions.core.generic.BadRequestException;
import io.finto.fineract.sdk.models.GetLoansLoanIdResponse;
import org.easymock.IMocksControl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.easymock.EasyMock.createStrictControl;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoanOwnershipValidatorTest {

    private LoanOwnershipValidator validator;
    private IMocksControl control;

    @BeforeEach
    void setUp() {
        control = createStrictControl();
        validator = new LoanOwnershipValidator();
    }

    @Test
    void test_validateLoanOwnership_success() {
        CustomerInternalId customerInternalId = new CustomerInternalId("123");
        GetLoansLoanIdResponse loan = new GetLoansLoanIdResponse();
        loan.setClientId(123L);

        validator.validateLoanOwnership(customerInternalId, loan);
    }

    @Test
    void test_validateLoanOwnership_failure() {
        CustomerInternalId customerInternalId = new CustomerInternalId("123");
        GetLoansLoanIdResponse loan = new GetLoansLoanIdResponse();
        loan.setClientId(456L);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> validator.validateLoanOwnership(customerInternalId, loan));

        assertTrue(exception.getErrorCode().contains("400056"));
        assertTrue(exception.getMessage().contains("The loan does not belong to the customer"));
    }

}