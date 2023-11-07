package io.finto.integration.fineract.converter;

import io.finto.domain.bnpl.enums.LoanTransactionType;
import io.finto.domain.bnpl.transaction.TransactionSubmit;
import io.finto.fineract.sdk.models.PostLoansLoanIdTransactionsRequest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class FineractLoanTransactionMapperTest {

    private final FineractLoanTransactionMapper mapper = FineractLoanTransactionMapper.INSTANCE;

    @Test
    void testFromLocalDate() {
        var localDate = LocalDate.now();
        assertEquals(localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd", new Locale("en"))), mapper.fromLocalDate(localDate));
        assertNull(mapper.fromLocalDate(null));
    }

    @Test
    void testLoanTransactionSubmissionOther() {
        var localDate = LocalDate.now();
        assertEquals(generatePostLoansLoanIdTransactionsRequestForOthers(localDate),
                mapper.loanTransactionSubmissionOther(generateTransactionSubmit(localDate)));
    }

    private PostLoansLoanIdTransactionsRequest generatePostLoansLoanIdTransactionsRequestForOthers(LocalDate localDate) {
        return PostLoansLoanIdTransactionsRequest.builder()
                .transactionDate(localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd", new Locale("en"))))
                .transactionAmount(new BigDecimal("1"))
                .paymentTypeId(2L)
                .locale("en")
                .dateFormat("yyyy-MM-dd")
                .build();
    }

    @Test
    void testLoanTransactionSubmissionForeclosure() {
        var localDate = LocalDate.now();
        assertEquals(generatePostLoansLoanIdTransactionsRequestForForeclosure(localDate),
                mapper.loanTransactionSubmissionForeclosure(generateTransactionSubmit(localDate)));
    }

    private PostLoansLoanIdTransactionsRequest generatePostLoansLoanIdTransactionsRequestForForeclosure(LocalDate localDate) {
        return PostLoansLoanIdTransactionsRequest.builder()
                .transactionDate(localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd", new Locale("en"))))
                .locale("en")
                .dateFormat("yyyy-MM-dd")
                .build();
    }

    private TransactionSubmit generateTransactionSubmit(LocalDate localDate) {
        return TransactionSubmit.builder()
                .type(LoanTransactionType.PREPAY_LOAN)
                .date(localDate)
                .amount(new BigDecimal("1"))
                .paymentTypeId(2L)
                .build();
    }

}