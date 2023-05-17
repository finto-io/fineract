package io.finto.integration.fineract.converter;

import io.finto.integration.fineract.domain.TransactionsStatus;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

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

}