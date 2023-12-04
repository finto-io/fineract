package io.finto.integration.fineract.converter;

import io.finto.fineract.sdk.models.PostLoansLoanIdRequest;
import io.finto.fineract.sdk.models.PostLoansLoanIdTransactionsRequest;
import io.finto.integration.fineract.dto.enums.LoanStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static io.finto.fineract.sdk.Constants.DEFAULT_DATE_FORMATTER;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FineractLoanMapperTest {

    private final FineractLoanMapper mapper = FineractLoanMapper.INSTANCE;

    private static Stream<Arguments> provideLoanStatus() {
        return Stream.of(
                Arguments.of(io.finto.domain.id.fineract.LoanStatus.APPROVED, LoanStatus.APPROVED),
                Arguments.of(io.finto.domain.id.fineract.LoanStatus.REJECTED, LoanStatus.REJECTED),
                Arguments.of(io.finto.domain.id.fineract.LoanStatus.WITHDRAWN, LoanStatus.WITHDRAWN),
                Arguments.of(io.finto.domain.id.fineract.LoanStatus.ACTIVATED, LoanStatus.ACTIVATED),
                Arguments.of(io.finto.domain.id.fineract.LoanStatus.CLOSED, LoanStatus.CLOSED)
        );
    }

    @ParameterizedTest
    @MethodSource("provideLoanStatus")
    void toCommandDto(io.finto.domain.id.fineract.LoanStatus inputStatus, LoanStatus expected) {
        LoanStatus status = mapper.toCommandDto(inputStatus);

        assertEquals(expected, status);
    }

    @ParameterizedTest
    @EnumSource(io.finto.domain.id.fineract.LoanStatus.class)
    void toRequestWithDate(io.finto.domain.id.fineract.LoanStatus inputStatus) {
        String currentDate = LocalDateTime.now().format(DEFAULT_DATE_FORMATTER);

        if (!inputStatus.equals(io.finto.domain.id.fineract.LoanStatus.CLOSED)) {
            PostLoansLoanIdRequest request = mapper.toRequestWithDate(inputStatus);
            assertEquals(currentDate, getRequestDate(request, inputStatus));
        } else {
            PostLoansLoanIdTransactionsRequest request = mapper.toRequestWithDateForClose(inputStatus);
            assertEquals(currentDate, getRequestDateForClose(request));
        }
    }

    private String getRequestDate(PostLoansLoanIdRequest request, io.finto.domain.id.fineract.LoanStatus loanStatus) {
        switch (loanStatus) {
            case APPROVED:
                return request.getApprovedOnDate();
            case REJECTED:
                return request.getRejectedOnDate();
            case WITHDRAWN:
                return request.getWithdrawnOnDate();
            case ACTIVATED:
                return request.getActualDisbursementDate();
            default:
                throw new IllegalArgumentException("Invalid loan status");
        }
    }

    private String getRequestDateForClose(PostLoansLoanIdTransactionsRequest request) {
        return request.getTransactionDate();
    }

}