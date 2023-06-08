package io.finto.integration.fineract.converter;

import io.finto.domain.transaction.Transaction;
import io.finto.domain.transaction.TransactionsStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.stream.Stream;

import static io.finto.integration.fineract.test.Fixtures.testTransaction;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class FineractTransactionMapperTest {

    private FineractTransactionMapper mapper = FineractTransactionMapper.INSTANCE;

    @ParameterizedTest
    @MethodSource
    void test_mapStatusToCommand(TransactionsStatus status, String expected) {
        assertThat(mapper.mapStatusToCommand(status)).isEqualTo(expected);
    }

    private static Stream<Arguments> test_mapStatusToCommand() {
        return Stream.of(
                arguments(TransactionsStatus.BLOCKED, "block"),
                arguments(TransactionsStatus.UNBLOCKED, "unblock")
        );
    }

    @Test
    void test_transactionList() {
        var actual = mapper.toTransaction(testTransaction());
        var expected = Transaction.builder()
                .date(LocalDate.now())
                .valueDate(LocalDate.now())
                .description("top-up")
                .debitCreditIndicator("D")
                .transactionAmount(new BigDecimal(100.003).setScale(3, RoundingMode.FLOOR))
                .transactionCurrency("JOD")
                .build();

        assertThat(actual).isEqualTo(expected);
    }

}