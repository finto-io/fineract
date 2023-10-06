package io.finto.integration.fineract.converter;

import io.finto.domain.charge.ChargeCreate;
import io.finto.domain.loanproduct.FeeCalcType;
import io.finto.domain.loanproduct.FeeType;
import io.finto.fineract.sdk.models.PostChargesRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FineractChargeMapperTest {

    private final FineractChargeMapper mapper = FineractChargeMapper.INSTANCE;

    private static Stream<Arguments> chargeCreationFineractCases() {
        return Stream.of(
                Arguments.of(generateChargeCreateCase1(), generatePostChargesRequestCase1()),
                Arguments.of(generateChargeCreateCase2(), generatePostChargesRequestCase2()),
                Arguments.of(generateChargeCreateCase3(), generatePostChargesRequestCase3())
        );
    }

    @ParameterizedTest
    @MethodSource("chargeCreationFineractCases")
    void testChargeCreationFineractRequest_1(ChargeCreate source, PostChargesRequest expected) {
        var actual = mapper.chargeCreationFineractRequest(source);

        assertEquals(expected, actual);
    }

    private static PostChargesRequest generatePostChargesRequestCase1() {
        return PostChargesRequest.builder()
                .active(true)
                .amount(10D)
                .chargeAppliesTo(1)
                .chargeCalculationType(1)
                .chargePaymentMode(1)
                .chargeTimeType(1)
                .currencyCode("USD")
                .locale("en")
                .name("name")
                .penalty(false)
                .minCap(12D)
                .maxCap(11D)
                .build();
    }

    private static ChargeCreate generateChargeCreateCase1() {
        return ChargeCreate.builder()
                .currencyCode("USD")
                .name("name")
                .feeType(FeeType.FEES)
                .feeCalcType(FeeCalcType.FIXED)
                .chargePaymentMode(1)
                .feeAmount(BigDecimal.valueOf(10))
                .toRange(BigDecimal.valueOf(11))
                .fromRange(BigDecimal.valueOf(12))
                .build();
    }

    private static PostChargesRequest generatePostChargesRequestCase2() {
        return PostChargesRequest.builder()
                .active(true)
                .amount(10D)
                .chargeAppliesTo(1)
                .chargeCalculationType(2)
                .chargePaymentMode(1)
                .chargeTimeType(9)
                .currencyCode("currencyCode")
                .locale("en")
                .name("name")
                .penalty(true)
                .minCap(12D)
                .maxCap(11D)
                .build();
    }

    private static ChargeCreate generateChargeCreateCase2() {
        return ChargeCreate.builder()
                .currencyCode("currencyCode")
                .name("name")
                .feeType(FeeType.LATE_PAYMENT)
                .feeCalcType(FeeCalcType.PERCENTAGE)
                .chargePaymentMode(1)
                .feeAmount(BigDecimal.valueOf(10))
                .toRange(BigDecimal.valueOf(11))
                .fromRange(BigDecimal.valueOf(12))
                .build();
    }

    private static PostChargesRequest generatePostChargesRequestCase3() {
        return PostChargesRequest.builder()
                .active(true)
                .amount(10D)
                .chargeAppliesTo(1)
                .chargeCalculationType(2)
                .chargePaymentMode(1)
                .chargeTimeType(2)
                .currencyCode("currencyCode")
                .locale("en")
                .name("name")
                .penalty(false)
                .minCap(12D)
                .maxCap(11D)
                .build();
    }

    private static ChargeCreate generateChargeCreateCase3() {
        return ChargeCreate.builder()
                .currencyCode("currencyCode")
                .name("name")
                .feeType(FeeType.EARLY_SETTLEMENT)
                .feeCalcType(FeeCalcType.PERCENTAGE)
                .chargePaymentMode(1)
                .feeAmount(BigDecimal.valueOf(10))
                .toRange(BigDecimal.valueOf(11))
                .fromRange(BigDecimal.valueOf(12))
                .build();
    }
}