package io.finto.integration.fineract.validators.loan;

import io.finto.domain.id.fineract.LoanStatus;
import org.easymock.IMocksControl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.easymock.EasyMock.createStrictControl;
import static org.junit.jupiter.api.Assertions.assertEquals;

class LoanStatusValidatorTest {

    private IMocksControl control;
    private LoanStatusValidator validator;

    @BeforeEach
    void setUp() {
        control = createStrictControl();
        validator = new LoanStatusValidator();
    }

    @ParameterizedTest
    @CsvSource({
            "Submitted and pending approval, APPROVED, true",
            "Submitted and pending approval, WITHDRAWN, true",
            "Submitted and pending approval, REJECTED, true",
            "Approved, ACTIVATED, true",
            "Some other status, APPROVED, false"
    })
    void testValidateStatusChange(String currentStatus, LoanStatus newStatus, boolean expectedResult) {
        boolean result = validator.validateStatusChange(currentStatus, newStatus);

        assertEquals(expectedResult, result);
    }

}