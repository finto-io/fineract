package io.finto.integration.fineract.converter;

import io.finto.domain.charge.ChargeCreate;
import io.finto.domain.id.fineract.ChargeId;
import io.finto.domain.loanproduct.AccountingMapping;
import io.finto.domain.loanproduct.AccountingMappings;
import io.finto.domain.loanproduct.Fee;
import io.finto.domain.loanproduct.FeeCalcType;
import io.finto.domain.loanproduct.FeeCreate;
import io.finto.domain.loanproduct.FeeType;
import io.finto.domain.loanproduct.InterestType;
import io.finto.domain.loanproduct.LoanProduct;
import io.finto.domain.loanproduct.LoanProductCreate;
import io.finto.domain.loanproduct.TransactionProcessingStrategy;
import io.finto.domain.loanproduct.Type;
import io.finto.fineract.sdk.models.AllowAttributeOverrides;
import io.finto.fineract.sdk.models.ChargeData;
import io.finto.fineract.sdk.models.GetChargeAppliesTo;
import io.finto.fineract.sdk.models.GetChargeCalculationType;
import io.finto.fineract.sdk.models.GetChargePaymentMode;
import io.finto.fineract.sdk.models.GetChargeTimeType;
import io.finto.fineract.sdk.models.GetChargesCurrency;
import io.finto.fineract.sdk.models.GetGlAccountMapping;
import io.finto.fineract.sdk.models.GetLoanAccountingMappings;
import io.finto.fineract.sdk.models.GetLoanProductsAccountingRule;
import io.finto.fineract.sdk.models.GetLoanProductsAmortizationType;
import io.finto.fineract.sdk.models.GetLoanProductsCurrency;
import io.finto.fineract.sdk.models.GetLoanProductsDaysInMonthType;
import io.finto.fineract.sdk.models.GetLoanProductsDaysInYearType;
import io.finto.fineract.sdk.models.GetLoanProductsInterestRateFrequencyType;
import io.finto.fineract.sdk.models.GetLoanProductsInterestTemplateType;
import io.finto.fineract.sdk.models.GetLoanProductsProductIdResponse;
import io.finto.fineract.sdk.models.GetLoanProductsRepaymentFrequencyType;
import io.finto.fineract.sdk.models.GetLoansProductsInterestCalculationPeriodType;
import io.finto.fineract.sdk.models.GetProductsCharges;
import io.finto.fineract.sdk.models.PostLoanProductsRequest;
import io.finto.integration.fineract.dto.LoanProductDetailsCreateDto;
import io.finto.integration.fineract.dto.LoanProductDetailsDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class FineractLoanProductMapperTest {

    private final FineractLoanProductMapper mapper = FineractLoanProductMapper.INSTANCE;

    private static Stream<Arguments> testToChargeTimeTypeCases() {
        return Stream.of(
                Arguments.of(FeeType.FEES, 1),
                Arguments.of(FeeType.LATE_PAYMENT, 9),
                Arguments.of(FeeType.EARLY_SETTLEMENT, 2)
        );
    }

    @ParameterizedTest
    @MethodSource("testToChargeTimeTypeCases")
    void testToChargeTimeType(FeeType source, Integer expected) {
        assertEquals(expected, mapper.toChargeTimeType(source));
    }

    private static Stream<Arguments> testToChargeCalculationTypeCases() {
        return Stream.of(
                Arguments.of(FeeCalcType.PERCENTAGE, 2),
                Arguments.of(FeeCalcType.FIXED, 1)
        );
    }

    @ParameterizedTest
    @MethodSource("testToChargeCalculationTypeCases")
    void testToChargeCalculationType(FeeCalcType source, Integer expected) {
        assertEquals(expected, mapper.toChargeCalculationType(source));
    }

    @Test
    void testToPenalty() {
        assertTrue(mapper.toPenalty(FeeType.LATE_PAYMENT));
        assertFalse(mapper.toPenalty(FeeType.FEES));
        assertFalse(mapper.toPenalty(FeeType.EARLY_SETTLEMENT));
    }

    @Test
    void testGetNumberOfRepayments() {
        assertEquals(2, mapper.getNumberOfRepayments(2, 3, 4));
        assertEquals(4, mapper.getNumberOfRepayments(null, null, 4));
        assertEquals(1, mapper.getNumberOfRepayments(null, 2, 4));
        assertEquals(3, mapper.getNumberOfRepayments(6, null, 4));
    }

    @Test
    void testToInterestType() {
        assertEquals(0, mapper.toInterestType(InterestType.REDUCING));
        assertEquals(1, mapper.toInterestType(InterestType.FIXED));
    }

    @Test
    void testToCharges() {
        assertEquals(List.of(ChargeData.builder().id(1L).build(), ChargeData.builder().id(2L).build()),
                mapper.toCharges(List.of(ChargeId.of(1L), ChargeId.of(2L))));
        assertNull(mapper.toCharges(null));
    }

    @Test
    void testToChargeCreate() {
        assertEquals(ChargeCreate.builder()
                        .name("feeNameshortName")
                        .feeCalcType(FeeCalcType.FIXED)
                        .feeAmount(BigDecimal.valueOf(10))
                        .fromRange(BigDecimal.valueOf(11))
                        .toRange(BigDecimal.valueOf(12))
                        .feeType(FeeType.FEES)
                        .chargePaymentMode(2)
                        .currencyCode("currencyCode")
                        .build(),
                mapper.toChargeCreate(FeeCreate.builder()
                        .feeCalcType(FeeCalcType.FIXED)
                        .feeAmount(BigDecimal.valueOf(10))
                        .fromRange(BigDecimal.valueOf(11))
                        .toRange(BigDecimal.valueOf(12))
                        .feeType(FeeType.FEES)
                        .feeName("feeName")
                        .chargePaymentMode(2)
                        .currencyCode("currencyCode")
                        .build(), "shortName"));
    }

    @Test
    void testLoanProductCreationFineractRequest() {
        assertEquals(generatePostLoanProductsRequest(),
                mapper.loanProductCreationFineractRequest(generateLoanProductCreate(), List.of(ChargeId.of(20L))));
    }

    private PostLoanProductsRequest generatePostLoanProductsRequest() {
        var result = new PostLoanProductsRequest();

        result.setAccountingRule(5);
        result.setCharges(List.of(ChargeData.builder().id(20L).build()));
        result.setCurrencyCode("currencyCode");
        result.setDaysInMonthType(7);
        result.setDaysInYearType(6);
        result.setDigitsAfterDecimal(1);
        result.setFundSourceAccountId(2L);
        result.setGraceOnInterestPayment(9);
        result.setGraceOnPrincipalPayment(9);
        result.setIncomeFromFeeAccountId(10L);
        result.setIncomeFromPenaltyAccountId(9L);
        result.setIncomeFromRecoveryAccountId(9L);
        result.setInterestCalculationPeriodType(4);
        result.setInterestOnLoanAccountId(8L);
        result.setInterestRateFrequencyType(3);
        result.setInterestRatePerPeriod(8.0D);
        result.setInterestType(1);
        result.setIsInterestRecalculationEnabled(false);
        result.setLoanPortfolioAccountId(6L);
        result.setLocale("en");
        result.setMaxNumberOfRepayments(11);
        result.setMinNumberOfRepayments(10);
        result.setName("name");
        result.setNumberOfRepayments(10);
        result.setOverpaymentLiabilityAccountId(12L);
        result.setReceivableFeeAccountId(7L);
        result.setReceivableInterestAccountId(3L);
        result.setReceivablePenaltyAccountId(4L);
        result.setRepaymentEvery(1);
        result.setRepaymentFrequencyType(2);
        result.setShortName("shortName");
        result.setTransactionProcessingStrategyCode("transactionProcessingStrategyCode");
        result.setTransfersInSuspenseAccountId(5L);
        result.setWriteOffAccountId(11L);
        result.setAmortizationType(3);

        return result;
    }

    private LoanProductCreate generateLoanProductCreate() {
        return LoanProductCreate.builder()
                .currencyCode("currencyCode")
                .digitsAfterDecimal(1)
                .numberOfRepayments(2)
                .amortizationType(3)
                .interestCalculationPeriodType(4)
                .transactionProcessingStrategyCode("transactionProcessingStrategyCode")
                .accountingRule(5)
                .daysInYearType(6)
                .daysInMonthType(7)
                .shortName("shortName")
                .externalId("externalId")
                .name("name")
                .interestType(InterestType.FIXED)
                .interest(BigDecimal.valueOf(8))
                .installmentGracePeriod(9)
                .minimumPeriod(10)
                .maximumPeriod(11)
                .earlySettlementAllowed(true)
                .latePaymentBlockUser(true)
                .partnerId("partnerId")
                .partnerName("partnerName")
                .fees(List.of())
                .build();
    }

    @Test
    void testToLoanProductDetailsCreateDto() {
        var dt = LocalDateTime.now();
        var expected = LoanProductDetailsCreateDto.builder()
                .dateFormat("dd MMMM yyyy HH:mm")
                .locale("en")
                .partnerId("partnerId")
                .partnerName("partnerName")
                .externalId("externalId")
                .latePaymentBlockUser(true)
                .earlySettlementAllowed(true)
                .loadedAt(DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm", new Locale("en")).format(dt))
                .loadedBy("mifos")
                .build();
        assertEquals(expected, mapper.toLoanProductDetailsCreateDto(generateLoanProductCreate(), dt));
    }

    @Test
    void testToActive() {
        assertTrue(mapper.toActive("loanProduct.active"));
    }

    @Test
    void testToInterestType_FromLong() {
        assertEquals("FIXED", mapper.toInterestType(1L));
        assertEquals("REDUCING", mapper.toInterestType(2L));
    }

    @Test
    void testToFeeCalcType() {
        assertEquals(FeeCalcType.FIXED, mapper.toFeeCalcType(1));
        assertEquals(FeeCalcType.PERCENTAGE, mapper.toFeeCalcType(2));
    }

    @Test
    void testToFeeType() {
        assertEquals(FeeType.LATE_PAYMENT, mapper.toFeeType(9));
        assertEquals(FeeType.EARLY_SETTLEMENT, mapper.toFeeType(2));
        assertEquals(FeeType.FEES, mapper.toFeeType(1));
    }

    @Test
    void testToFeeName() {
        assertEquals("name", mapper.toFeeName("name1234"));
    }

    @Test
    void testToDomain_Fee() {
        assertEquals(generateFee(), mapper.toDomain(generateGetProductsCharges()));
    }

    private Fee generateFee() {
        return Fee.builder()
                .internalFeeId(5L)
                .feeCalcType(FeeCalcType.PERCENTAGE)
                .feeAmount(BigDecimal.valueOf(3.0))
                .fromRange(BigDecimal.valueOf(1.0))
                .toRange(BigDecimal.valueOf(2.0))
                .feeType(FeeType.FEES)
                .feeName("name")
                .build();
    }

    private GetProductsCharges generateGetProductsCharges() {
        return GetProductsCharges.builder()
                .active(true)
                .minCap(1D)
                .maxCap(2D)
                .amount(3D)
                .chargeAppliesTo(GetChargeAppliesTo.builder()
                        .id(1)
                        .code("code1")
                        .description("description1")
                        .build())
                .chargeCalculationType(GetChargeCalculationType.builder()
                        .id(2)
                        .code("code2")
                        .description("description2")
                        .build())
                .chargePaymentMode(GetChargePaymentMode.builder()
                        .id(3)
                        .code("code3")
                        .description("description3")
                        .build())
                .chargeTimeType(GetChargeTimeType.builder()
                        .id(4)
                        .code("code4")
                        .description("description4")
                        .build())
                .currency(GetChargesCurrency.builder()
                        .name("name")
                        .code("code")
                        .decimalPlaces(5)
                        .displayLabel("displayLabel")
                        .displaySymbol("displaySymbol")
                        .nameCode("nameCode")
                        .build())
                .id(5)
                .name("name1234")
                .penalty(true)
                .build();
    }

    @Test
    void testFromZonedDateTime() {
        var dt = ZonedDateTime.now();
        assertEquals(dt.format(io.finto.fineract.sdk.Constants.DEFAULT_DATE_FORMATTER), mapper.fromZonedDateTime(dt));
        assertNull(mapper.fromZonedDateTime(null));
    }

    @Test
    void testToDomain_LoanProduct() {
        var dt = ZonedDateTime.now();
        assertEquals(generateLoanProduct(dt),
                mapper.toDomain(generateGetLoanProductsProductIdResponse(), generateLoanProductDetailsDto(dt)));
    }

        private LoanProduct generateLoanProduct(ZonedDateTime dt) {
            return LoanProduct.builder()
                    .internalId(1L)
                    .name("name")
                    .shortName("shortName")
                    .active(false)
                    .currencyCode("code")
                    .principal(BigDecimal.valueOf(4.0))
                    .minPrincipal(BigDecimal.valueOf(2.0))
                    .maxPrincipal(BigDecimal.valueOf(3.0))
                    .numberOfRepayments(5)
                    .minNumberOfRepayments(6)
                    .maxNumberOfRepayments(7)
                    .repaymentEvery(8)
                    .repaymentFrequencyType(Type.builder()
                            .id(9L)
                            .value("value9")
                            .build())
                    .interest(BigDecimal.valueOf(10.0))
                    .minInterestRatePerPeriod(BigDecimal.valueOf(11.0))
                    .maxInterestRatePerPeriod(BigDecimal.valueOf(12.0))
                    .interestRateFrequencyType(Type.builder()
                            .id(13L)
                            .value("value13")
                            .build())
                    .amortizationType(Type.builder()
                            .id(14L)
                            .value("value14")
                            .build())
                    .interestType(Type.builder()
                            .id(15L)
                            .value("REDUCING")
                            .build())
                    .interestCalculationPeriodType(Type.builder()
                            .id(16L)
                            .value("value16")
                            .build())
                    .transactionProcessingStrategy(TransactionProcessingStrategy.builder()
                            .code("setTransactionProcessingStrategyCode")
                            .value("setTransactionProcessingStrategyName")
                            .build())
                    .installmentGracePeriod(17)
                    .daysInMonthType(Type.builder()
                            .id(18L)
                            .value("value18")
                            .build())
                    .daysInYearType(Type.builder()
                            .id(19L)
                            .value("value19")
                            .build())
                    .isInterestRecalculationEnabled(true)
                    .accountingRule(Type.builder()
                            .id(20L)
                            .value("value20")
                            .build())
                    .accountingMappings(AccountingMappings.builder()
                            .fundSourceAccount(AccountingMapping.builder()
                                    .id(21L)
                                    .name("name21")
                                    .glCode("glCode21")
                                    .build())
                            .loanPortfolioAccount(AccountingMapping.builder()
                                    .id(27L)
                                    .name("name27")
                                    .glCode("glCode27")
                                    .build())
                            .transfersInSuspenseAccount(AccountingMapping.builder()
                                    .id(31L)
                                    .name("name31")
                                    .glCode("glCode31")
                                    .build())
                            .receivableInterestAccount(AccountingMapping.builder()
                                    .id(29L)
                                    .name("name29")
                                    .glCode("glCode29")
                                    .build())
                            .receivableFeeAccount(AccountingMapping.builder()
                                    .id(33L)
                                    .name("name33")
                                    .glCode("glCode33")
                                    .build())
                            .receivablePenaltyAccount(AccountingMapping.builder()
                                    .id(30L)
                                    .name("name30")
                                    .glCode("glCode30")
                                    .build())
                            .interestOnLoanAccount(AccountingMapping.builder()
                                    .id(26L)
                                    .name("name26")
                                    .glCode("glCode26")
                                    .build())
                            .incomeFromFeeAccount(AccountingMapping.builder()
                                    .id(23L)
                                    .name("name23")
                                    .glCode("glCode23")
                                    .build())
                            .incomeFromPenaltyAccount(AccountingMapping.builder()
                                    .id(24L)
                                    .name("name24")
                                    .glCode("glCode24")
                                    .build())
                            .incomeFromRecoveryAccount(AccountingMapping.builder()
                                    .id(25L)
                                    .name("name25")
                                    .glCode("glCode25")
                                    .build())
                            .writeOffAccount(AccountingMapping.builder()
                                    .id(32L)
                                    .name("name32")
                                    .glCode("glCode32")
                                    .build())
                            .overpaymentLiabilityAccount(AccountingMapping.builder()
                                    .id(28L)
                                    .name("name28")
                                    .glCode("glCode28")
                                    .build())
                            .build())
                    .allowAttributeOverrides(io.finto.domain.loanproduct.AllowAttributeOverrides.builder()
                            .amortizationType(true)
                            .interestType(true)
                            .transactionProcessingStrategyCode(true)
                            .interestCalculationPeriodType(true)
                            .inArrearsTolerance(true)
                            .repaymentEvery(true)
                            .graceOnPrincipalAndInterestPayment(true)
                            .graceOnArrearsAgeing(true)
                            .build())
                    .fees(List.of(generateFee()))
                    .createdAt(dt.format(io.finto.fineract.sdk.Constants.DEFAULT_DATE_FORMATTER))
                    .createdBy("loadedBy")
                    .closedAt(dt.format(io.finto.fineract.sdk.Constants.DEFAULT_DATE_FORMATTER))
                    .closedBy("closedBy")
                    .updatedAt(dt.format(io.finto.fineract.sdk.Constants.DEFAULT_DATE_FORMATTER))
                    .updatedBy("modifiedBy")
                    .partnerId("partnerId")
                    .partnerName("partnerName")
                    .externalId("externalId")
                    .latePaymentBlockUser(true)
                    .earlySettlementAllowed(true)
                    .build();
        }

    private LoanProductDetailsDto generateLoanProductDetailsDto(ZonedDateTime dateTime) {
        return LoanProductDetailsDto.builder()
                .productLoanId(1L)
                .dateFormat("dateFormat")
                .locale("locale")
                .partnerId("partnerId")
                .partnerName("partnerName")
                .externalId("externalId")
                .latePaymentBlockUser(true)
                .earlySettlementAllowed(true)
                .loadedAt(dateTime)
                .loadedBy("loadedBy")
                .closedAt(dateTime)
                .closedBy("closedBy")
                .modifiedAt(dateTime)
                .modifiedBy("modifiedBy")
                .build();
    }

    private GetLoanProductsProductIdResponse generateGetLoanProductsProductIdResponse() {
        var result = new GetLoanProductsProductIdResponse();
        var a = new GetProductsCharges();
        result.setCharges(List.of(generateGetProductsCharges()));
        result.setId(1L);
        result.setName("name");
        result.setShortName("shortName");
        result.setStatus("status");
        result.setCurrency(GetLoanProductsCurrency.builder()
                .name("name")
                .code("code")
                .decimalPlaces(5)
                .displayLabel("displayLabel")
                .displaySymbol("displaySymbol")
                .nameCode("nameCode")
                .build());
        result.setPrincipal(4D);
        result.setMinPrincipal(2D);
        result.setMaxPrincipal(3D);
        result.setNumberOfRepayments(5);
        result.setMinNumberOfRepayments(6);
        result.setMaxNumberOfRepayments(7);
        result.setRepaymentEvery(8);
        result.setRepaymentFrequencyType(GetLoanProductsRepaymentFrequencyType.builder()
                .id(9L)
                .code("code9")
                .value("value9")
                .build());
        result.setInterestRatePerPeriod(10D);
        result.setMinInterestRatePerPeriod(11D);
        result.setMaxInterestRatePerPeriod(12D);
        result.setInterestRateFrequencyType(GetLoanProductsInterestRateFrequencyType.builder()
                .id(13L)
                .code("code13")
                .value("value13")
                .build());
        result.setAmortizationType(GetLoanProductsAmortizationType.builder()
                .id(14L)
                .code("code14")
                .value("value14")
                .build());
        result.setInterestType(GetLoanProductsInterestTemplateType.builder()
                .id(15L)
                .code("code15")
                .value("value15")
                .build());
        result.setInterestCalculationPeriodType(GetLoansProductsInterestCalculationPeriodType.builder()
                .id(16L)
                .code("code16")
                .value("value16")
                .build());
        result.setTransactionProcessingStrategyCode("setTransactionProcessingStrategyCode");
        result.setTransactionProcessingStrategyName("setTransactionProcessingStrategyName");
        result.setGraceOnPrincipalPayment(17);
        result.setDaysInMonthType(GetLoanProductsDaysInMonthType.builder()
                .id(18L)
                .code("code18")
                .value("value18")
                .build());
        result.setDaysInYearType(GetLoanProductsDaysInYearType.builder()
                .id(19L)
                .code("code19")
                .value("value19")
                .build());
        result.setIsInterestRecalculationEnabled(true);
        result.setAccountingRule(GetLoanProductsAccountingRule.builder()
                .id(20L)
                .code("code20")
                .value("value20")
                .build());
        result.setAccountingMappings(GetLoanAccountingMappings.builder()
                .fundSourceAccount(GetGlAccountMapping.builder()
                        .id(21L)
                        .name("name21")
                        .glCode("glCode21")
                        .build())
                .goodwillCreditAccount(GetGlAccountMapping.builder()
                        .id(22L)
                        .name("name22")
                        .glCode("glCode22")
                        .build())
                .incomeFromFeeAccount(GetGlAccountMapping.builder()
                        .id(23L)
                        .name("name23")
                        .glCode("glCode23")
                        .build())
                .incomeFromPenaltyAccount(GetGlAccountMapping.builder()
                        .id(24L)
                        .name("name24")
                        .glCode("glCode24")
                        .build())
                .incomeFromRecoveryAccount(GetGlAccountMapping.builder()
                        .id(25L)
                        .name("name25")
                        .glCode("glCode25")
                        .build())
                .interestOnLoanAccount(GetGlAccountMapping.builder()
                        .id(26L)
                        .name("name26")
                        .glCode("glCode26")
                        .build())
                .loanPortfolioAccount(GetGlAccountMapping.builder()
                        .id(27L)
                        .name("name27")
                        .glCode("glCode27")
                        .build())
                .overpaymentLiabilityAccount(GetGlAccountMapping.builder()
                        .id(28L)
                        .name("name28")
                        .glCode("glCode28")
                        .build())
                .receivableInterestAccount(GetGlAccountMapping.builder()
                        .id(29L)
                        .name("name29")
                        .glCode("glCode29")
                        .build())
                .receivablePenaltyAccount(GetGlAccountMapping.builder()
                        .id(30L)
                        .name("name30")
                        .glCode("glCode30")
                        .build())
                .transfersInSuspenseAccount(GetGlAccountMapping.builder()
                        .id(31L)
                        .name("name31")
                        .glCode("glCode31")
                        .build())
                .writeOffAccount(GetGlAccountMapping.builder()
                        .id(32L)
                        .name("name32")
                        .glCode("glCode32")
                        .build())
                .receivableFeeAccount(GetGlAccountMapping.builder()
                        .id(33L)
                        .name("name33")
                        .glCode("glCode33")
                        .build())
                .build());
        result.setAllowAttributeOverrides(AllowAttributeOverrides.builder()
                .amortizationType(true)
                .graceOnArrearsAgeing(true)
                .graceOnPrincipalAndInterestPayment(true)
                .inArrearsTolerance(true)
                .interestCalculationPeriodType(true)
                .interestType(true)
                .repaymentEvery(true)
                .transactionProcessingStrategyCode(true)
                .build());
        return result;
    }
}