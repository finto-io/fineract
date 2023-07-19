package io.finto.integration.fineract.common;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FineractBusinessErrorHandlerTest {

    private static Stream<Arguments> provideParamsForHandler() {
        return Stream.of(
                Arguments.of(
                        "Client Mark Zerkunder Head Office Branch already has a Id with unique key 9822346853",
                        "Client already exists with a Id with unique key 9822346853")
        );
    }

    @ParameterizedTest
    @MethodSource("provideParamsForHandler")
    void convertMessage(String errorMessage, String expectedResult) {
        var handler = new FineractBusinessErrorHandler();
        var res = handler.convertMessage(errorMessage);
        assertEquals(expectedResult, res);
    }
}