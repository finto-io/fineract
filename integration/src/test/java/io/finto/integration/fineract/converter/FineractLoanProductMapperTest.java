package io.finto.integration.fineract.converter;

import io.finto.domain.bnpl.enums.AvailableLoanStatus;
import io.finto.domain.bnpl.enums.InstallmentFrequency;
import io.finto.domain.bnpl.loan.Loan;
import io.finto.domain.bnpl.loan.LoanCreate;
import io.finto.domain.bnpl.loan.Transaction;
import io.finto.domain.bnpl.schedule.Period;
import io.finto.domain.bnpl.schedule.Schedule;
import io.finto.domain.bnpl.schedule.ScheduleCalculate;
import io.finto.domain.charge.ChargeCreate;
import io.finto.domain.id.CustomerInternalId;
import io.finto.domain.id.fineract.ChargeId;
import io.finto.domain.id.fineract.LoanProductId;
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
import io.finto.fineract.sdk.models.GetLoansLoanIdCurrency;
import io.finto.fineract.sdk.models.GetLoansLoanIdLoanTransactionEnumData;
import io.finto.fineract.sdk.models.GetLoansLoanIdRepaymentFrequencyType;
import io.finto.fineract.sdk.models.GetLoansLoanIdRepaymentPeriod;
import io.finto.fineract.sdk.models.GetLoansLoanIdRepaymentSchedule;
import io.finto.fineract.sdk.models.GetLoansLoanIdResponse;
import io.finto.fineract.sdk.models.GetLoansLoanIdStatus;
import io.finto.fineract.sdk.models.GetLoansLoanIdTimeline;
import io.finto.fineract.sdk.models.GetLoansLoanIdTransactions;
import io.finto.fineract.sdk.models.GetLoansProductsInterestCalculationPeriodType;
import io.finto.fineract.sdk.models.GetProductsCharges;
import io.finto.fineract.sdk.models.PostLoanProductsRequest;
import io.finto.fineract.sdk.models.PostLoansChargeRequest;
import io.finto.fineract.sdk.models.PostLoansRepaymentSchedulePeriods;
import io.finto.fineract.sdk.models.PostLoansRequest;
import io.finto.fineract.sdk.models.PostLoansRequestDatatablesInner;
import io.finto.fineract.sdk.models.PostLoansRequestDatatablesInnerData;
import io.finto.fineract.sdk.models.PostLoansResponse;
import io.finto.fineract.sdk.models.ResultsetColumnHeaderData;
import io.finto.fineract.sdk.models.ResultsetRowData;
import io.finto.fineract.sdk.models.RunReportsResponse;
import io.finto.integration.fineract.dto.LoanProductDetailsCreateDto;
import io.finto.integration.fineract.dto.LoanProductDetailsDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
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
        result.setInterestRatePerPeriod(new BigDecimal("8"));
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
        assertEquals(generateFee(5L), mapper.toDomain(generateGetProductsCharges(5)));
    }

    private Fee generateFee(Long id) {
        return Fee.builder()
                .internalFeeId(id)
                .feeCalcType(FeeCalcType.PERCENTAGE)
                .feeAmount(BigDecimal.valueOf(3.0))
                .fromRange(BigDecimal.valueOf(1.0))
                .toRange(BigDecimal.valueOf(2.0))
                .feeType(FeeType.FEES)
                .feeName("name")
                .build();
    }

    private GetProductsCharges generateGetProductsCharges(Integer id) {
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
                        .id(1)
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
                .id(id)
                .name("name1234")
                .penalty(true)
                .build();
    }

    @Test
    void testFromZonedDateTime() {
        var dt = ZonedDateTime.now();
        assertEquals(dt.format(io.finto.fineract.sdk.Constants.LOAN_PRODUCT_DATE_TIME_FORMATTER), mapper.fromZonedDateTime(dt));
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
                .interest(BigDecimal.valueOf(10))
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
                .installmentGracePeriod(11)
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
                .fees(List.of(generateFee(5L), generateFee(1L)))
                .createdAt(dt.format(io.finto.fineract.sdk.Constants.LOAN_PRODUCT_DATE_TIME_FORMATTER))
                .createdBy("loadedBy")
                .closedAt(dt.format(io.finto.fineract.sdk.Constants.LOAN_PRODUCT_DATE_TIME_FORMATTER))
                .closedBy("closedBy")
                .updatedAt(dt.format(io.finto.fineract.sdk.Constants.LOAN_PRODUCT_DATE_TIME_FORMATTER))
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
        result.setCharges(List.of(generateGetProductsCharges(5), generateGetProductsCharges(1)));
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
        result.setInterestRatePerPeriod(new BigDecimal("10"));
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
        result.setGraceOnPrincipalPayment(11);
        result.setGraceOnInterestPayment(22);
        return result;
    }

    @Test
    void testFromLocalDate() {
        var localDate = LocalDate.now();
        assertEquals(localDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("en"))), mapper.fromLocalDate(localDate));
        assertNull(mapper.fromLocalDate(null));
    }

    @Test
    void testLoanScheduleCalculationFineractRequest() {
        var localDate = LocalDate.now();
        assertEquals(generatePostLoansRequest(localDate),
                mapper.loanScheduleCalculationFineractRequest(LoanProductId.of(321L),
                        generateScheduleCalculate(localDate),
                        generateGetLoanProductsProductIdResponse()));
        assertNull(mapper.fromLocalDate(null));
    }

    private PostLoansRequest generatePostLoansRequest(LocalDate localDate) {
        return PostLoansRequest.builder()
                .dateFormat("dd MMMM yyyy")
                .locale("en")
                .clientId(111L)
                .productId(321)
                .submittedOnDate(localDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("en"))))
                .expectedDisbursementDate(localDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("en"))))
                .principal(BigDecimal.valueOf(444))
                .loanType("individual")
                .loanTermFrequency(21)
                .loanTermFrequencyType(9)
                .numberOfRepayments(21)
                .repaymentEvery(8)
                .repaymentFrequencyType(9)
                .interestRatePerPeriod(new BigDecimal("10"))
                .amortizationType(14)
                .interestType(15)
                .interestCalculationPeriodType(16)
                .transactionProcessingStrategyCode("setTransactionProcessingStrategyCode")
                .graceOnPrincipalPayment(11)
                .graceOnInterestPayment(22)
                .charges(List.of(PostLoansChargeRequest.builder()
                                .chargeId(5L)
                                .amount(BigDecimal.valueOf(3.0))
                                .build(),
                        PostLoansChargeRequest.builder()
                                .chargeId(1L)
                                .amount(BigDecimal.valueOf(3.0))
                                .build()))
                .build();
    }

    private ScheduleCalculate generateScheduleCalculate(LocalDate localDate) {
        return ScheduleCalculate.builder()
                .internalCustomerId(111L)
                .requestDate(localDate)
                .expectedDisbursementDate(localDate)
                .amount(BigDecimal.valueOf(444))
                .numberOfInstallments(21)
                .digitsAfterDecimal(3)
                .build();
    }

    @Test
    void testCalculateApr() {
        assertEquals(new BigDecimal("18250.000"),
                mapper.calculateApr(BigDecimal.valueOf(3),
                        BigDecimal.valueOf(4),
                        BigDecimal.valueOf(5),
                        BigDecimal.valueOf(6),
                        4, 3));
        assertEquals(new BigDecimal("11.864"),
                mapper.calculateApr(new BigDecimal("10.0"),
                        new BigDecimal("0.0"),
                        new BigDecimal("0.921"),
                        new BigDecimal("100.0"),
                        336, 3));
    }

    @Test
    void testToNumberOfInstallments() {
        var localDate = LocalDate.now();
        assertEquals(3, mapper.toNumberOfInstallments(generatePostLoansRepaymentSchedulePeriodsSet(localDate)));
        assertNull(mapper.toNumberOfInstallments(null));
    }

    private Set<PostLoansRepaymentSchedulePeriods> generatePostLoansRepaymentSchedulePeriodsSet(LocalDate localDate) {
        var localDate1 = localDate.minus(1, ChronoUnit.DAYS);
        var localDate2 = localDate.plus(1, ChronoUnit.DAYS);
        var item1 = PostLoansRepaymentSchedulePeriods.builder()
                .dueDate(localDate1)
                .fromDate(localDate1)
                .obligationsMetOnDate(localDate1)
                .complete(true)
                .feeChargesDue(BigDecimal.valueOf(101))
                .totalInstallmentAmountForPeriod(BigDecimal.valueOf(102))
                .principalOriginalDue(BigDecimal.valueOf(103))
                .principalDue(BigDecimal.valueOf(104))
                .principalOutstanding(BigDecimal.valueOf(105))
                .interestOriginalDue(BigDecimal.valueOf(106))
                .interestDue(BigDecimal.valueOf(107))
                .interestOutstanding(BigDecimal.valueOf(108))
                .penaltyChargesDue(BigDecimal.valueOf(109))
                .feeChargesPaid(BigDecimal.valueOf(110))
                .feeChargesWaived(BigDecimal.valueOf(111))
                .feeChargesWrittenOff(BigDecimal.valueOf(112))
                .interestPaid(BigDecimal.valueOf(113))
                .interestWaived(BigDecimal.valueOf(114))
                .interestWrittenOff(BigDecimal.valueOf(115))
                .penaltyChargesOutstanding(BigDecimal.valueOf(116))
                .penaltyChargesPaid(BigDecimal.valueOf(117))
                .penaltyChargesWaived(BigDecimal.valueOf(118))
                .penaltyChargesWrittenOff(BigDecimal.valueOf(119))
                .principalPaid(BigDecimal.valueOf(120))
                .principalWrittenOff(BigDecimal.valueOf(121))
                .totalPaidInAdvanceForPeriod(BigDecimal.valueOf(122))
                .totalPaidLateForPeriod(BigDecimal.valueOf(123))
                .totalWaivedForPeriod(BigDecimal.valueOf(124))
                .totalWrittenOffForPeriod(BigDecimal.valueOf(125))
                .feeChargesOutstanding(BigDecimal.valueOf(126))
                .daysInPeriod(127)
                .period(1)
                .principalDisbursed(BigDecimal.valueOf(129))
                .principalLoanBalanceOutstanding(BigDecimal.valueOf(130))
                .totalPaidForPeriod(BigDecimal.valueOf(131))
                .totalActualCostOfLoanForPeriod(BigDecimal.valueOf(132))
                .totalDueForPeriod(BigDecimal.valueOf(133))
                .totalOriginalDueForPeriod(BigDecimal.valueOf(134))
                .totalOutstandingForPeriod(BigDecimal.valueOf(135))
                .totalCredits(BigDecimal.valueOf(136))
                .totalOverdue(BigDecimal.valueOf(137))
                .build();
        var item2 = PostLoansRepaymentSchedulePeriods.builder()
                .dueDate(localDate2)
                .fromDate(localDate2)
                .obligationsMetOnDate(localDate2)
                .complete(true)
                .feeChargesDue(BigDecimal.valueOf(201))
                .totalInstallmentAmountForPeriod(BigDecimal.valueOf(202))
                .principalOriginalDue(BigDecimal.valueOf(203))
                .principalDue(BigDecimal.valueOf(204))
                .principalOutstanding(BigDecimal.valueOf(205))
                .interestOriginalDue(BigDecimal.valueOf(206))
                .interestDue(BigDecimal.valueOf(207))
                .interestOutstanding(BigDecimal.valueOf(208))
                .penaltyChargesDue(BigDecimal.valueOf(209))
                .feeChargesPaid(BigDecimal.valueOf(210))
                .feeChargesWaived(BigDecimal.valueOf(211))
                .feeChargesWrittenOff(BigDecimal.valueOf(212))
                .interestPaid(BigDecimal.valueOf(213))
                .interestWaived(BigDecimal.valueOf(214))
                .interestWrittenOff(BigDecimal.valueOf(215))
                .penaltyChargesOutstanding(BigDecimal.valueOf(216))
                .penaltyChargesPaid(BigDecimal.valueOf(217))
                .penaltyChargesWaived(BigDecimal.valueOf(218))
                .penaltyChargesWrittenOff(BigDecimal.valueOf(219))
                .principalPaid(BigDecimal.valueOf(220))
                .principalWrittenOff(BigDecimal.valueOf(221))
                .totalPaidInAdvanceForPeriod(BigDecimal.valueOf(222))
                .totalPaidLateForPeriod(BigDecimal.valueOf(223))
                .totalWaivedForPeriod(BigDecimal.valueOf(224))
                .totalWrittenOffForPeriod(BigDecimal.valueOf(225))
                .feeChargesOutstanding(BigDecimal.valueOf(226))
                .daysInPeriod(227)
                .period(2)
                .principalDisbursed(BigDecimal.valueOf(229))
                .principalLoanBalanceOutstanding(BigDecimal.valueOf(230))
                .totalPaidForPeriod(BigDecimal.valueOf(231))
                .totalActualCostOfLoanForPeriod(BigDecimal.valueOf(232))
                .totalDueForPeriod(BigDecimal.valueOf(233))
                .totalOriginalDueForPeriod(BigDecimal.valueOf(234))
                .totalOutstandingForPeriod(BigDecimal.valueOf(235))
                .totalCredits(BigDecimal.valueOf(236))
                .totalOverdue(BigDecimal.valueOf(237))
                .build();
        var item3 = PostLoansRepaymentSchedulePeriods.builder()
                .dueDate(localDate)
                .fromDate(localDate)
                .obligationsMetOnDate(localDate)
                .complete(true)
                .feeChargesDue(BigDecimal.valueOf(301))
                .totalInstallmentAmountForPeriod(BigDecimal.valueOf(302))
                .principalOriginalDue(BigDecimal.valueOf(303))
                .principalDue(BigDecimal.valueOf(304))
                .principalOutstanding(BigDecimal.valueOf(305))
                .interestOriginalDue(BigDecimal.valueOf(306))
                .interestDue(BigDecimal.valueOf(307))
                .interestOutstanding(BigDecimal.valueOf(308))
                .penaltyChargesDue(BigDecimal.valueOf(309))
                .feeChargesPaid(BigDecimal.valueOf(310))
                .feeChargesWaived(BigDecimal.valueOf(311))
                .feeChargesWrittenOff(BigDecimal.valueOf(312))
                .interestPaid(BigDecimal.valueOf(313))
                .interestWaived(BigDecimal.valueOf(314))
                .interestWrittenOff(BigDecimal.valueOf(315))
                .penaltyChargesOutstanding(BigDecimal.valueOf(316))
                .penaltyChargesPaid(BigDecimal.valueOf(317))
                .penaltyChargesWaived(BigDecimal.valueOf(318))
                .penaltyChargesWrittenOff(BigDecimal.valueOf(319))
                .principalPaid(BigDecimal.valueOf(320))
                .principalWrittenOff(BigDecimal.valueOf(321))
                .totalPaidInAdvanceForPeriod(BigDecimal.valueOf(322))
                .totalPaidLateForPeriod(BigDecimal.valueOf(323))
                .totalWaivedForPeriod(BigDecimal.valueOf(324))
                .totalWrittenOffForPeriod(BigDecimal.valueOf(325))
                .feeChargesOutstanding(BigDecimal.valueOf(326))
                .daysInPeriod(327)
                .period(3)
                .principalDisbursed(BigDecimal.valueOf(329))
                .principalLoanBalanceOutstanding(BigDecimal.valueOf(330))
                .totalPaidForPeriod(BigDecimal.valueOf(331))
                .totalActualCostOfLoanForPeriod(BigDecimal.valueOf(332))
                .totalDueForPeriod(BigDecimal.valueOf(333))
                .totalOriginalDueForPeriod(BigDecimal.valueOf(334))
                .totalOutstandingForPeriod(BigDecimal.valueOf(335))
                .totalCredits(BigDecimal.valueOf(336))
                .totalOverdue(BigDecimal.valueOf(337))
                .build();
        return Set.of(item1, item2, item3);
    }

    @Test
    void testToInstallmentFrequency() {
        assertEquals(InstallmentFrequency.DAILY, mapper.toInstallmentFrequency(0));
        assertEquals(InstallmentFrequency.WEEKLY, mapper.toInstallmentFrequency(1));
        assertEquals(InstallmentFrequency.MONTHLY, mapper.toInstallmentFrequency(2));
        assertEquals(InstallmentFrequency.UNKNOWN, mapper.toInstallmentFrequency(3));
    }

    @Test
    void testToInstallmentStartDate() {
        var localDate = LocalDate.now();
        assertEquals(localDate.minus(1, ChronoUnit.DAYS),
                mapper.toInstallmentStartDate(generatePostLoansRepaymentSchedulePeriodsSet(localDate)));
        assertNull(mapper.toInstallmentStartDate(null));
    }

    @Test
    void testToInstallmentEndDate() {
        var localDate = LocalDate.now();
        assertEquals(localDate.plus(1, ChronoUnit.DAYS),
                mapper.toInstallmentEndDate(generatePostLoansRepaymentSchedulePeriodsSet(localDate)));
        assertNull(mapper.toInstallmentEndDate(null));
    }

    @Test
    void testToFirstInstallmentAmount() {
        var localDate = LocalDate.now();
        assertEquals(BigDecimal.valueOf(102),
                mapper.toFirstInstallmentAmount(generatePostLoansRepaymentSchedulePeriodsSet(localDate)));
        assertNull(mapper.toFirstInstallmentAmount(null));
    }

    @Test
    void testToSubSeqInstallmentAmount() {
        var localDate = LocalDate.now();
        assertEquals(BigDecimal.valueOf(202),
                mapper.toSubSeqInstallmentAmount(generatePostLoansRepaymentSchedulePeriodsSet(localDate)));
        assertNull(mapper.toSubSeqInstallmentAmount(null));
    }

    private PostLoansRepaymentSchedulePeriods generatePostLoansRepaymentSchedulePeriods(LocalDate localDate) {
        return PostLoansRepaymentSchedulePeriods.builder()
                .dueDate(localDate)
                .fromDate(localDate)
                .obligationsMetOnDate(localDate)
                .complete(true)
                .feeChargesDue(BigDecimal.valueOf(301))
                .totalInstallmentAmountForPeriod(BigDecimal.valueOf(302))
                .principalOriginalDue(BigDecimal.valueOf(303))
                .principalDue(BigDecimal.valueOf(304))
                .principalOutstanding(BigDecimal.valueOf(305))
                .interestOriginalDue(BigDecimal.valueOf(306))
                .interestDue(BigDecimal.valueOf(307))
                .interestOutstanding(BigDecimal.valueOf(308))
                .penaltyChargesDue(BigDecimal.valueOf(309))
                .feeChargesPaid(BigDecimal.valueOf(310))
                .feeChargesWaived(BigDecimal.valueOf(311))
                .feeChargesWrittenOff(BigDecimal.valueOf(312))
                .interestPaid(BigDecimal.valueOf(313))
                .interestWaived(BigDecimal.valueOf(314))
                .interestWrittenOff(BigDecimal.valueOf(315))
                .penaltyChargesOutstanding(BigDecimal.valueOf(316))
                .penaltyChargesPaid(BigDecimal.valueOf(317))
                .penaltyChargesWaived(BigDecimal.valueOf(318))
                .penaltyChargesWrittenOff(BigDecimal.valueOf(319))
                .principalPaid(BigDecimal.valueOf(320))
                .principalWrittenOff(BigDecimal.valueOf(321))
                .totalPaidInAdvanceForPeriod(BigDecimal.valueOf(322))
                .totalPaidLateForPeriod(BigDecimal.valueOf(323))
                .totalWaivedForPeriod(BigDecimal.valueOf(324))
                .totalWrittenOffForPeriod(BigDecimal.valueOf(325))
                .feeChargesOutstanding(BigDecimal.valueOf(326))
                .daysInPeriod(327)
                .period(1)
                .principalDisbursed(BigDecimal.valueOf(329))
                .principalLoanBalanceOutstanding(BigDecimal.valueOf(330))
                .totalPaidForPeriod(BigDecimal.valueOf(331))
                .totalActualCostOfLoanForPeriod(BigDecimal.valueOf(332))
                .totalDueForPeriod(BigDecimal.valueOf(333))
                .totalOriginalDueForPeriod(BigDecimal.valueOf(334))
                .totalOutstandingForPeriod(BigDecimal.valueOf(335))
                .totalCredits(BigDecimal.valueOf(336))
                .totalOverdue(BigDecimal.valueOf(337))
                .build();
    }

    @Test
    void testToPeriod() {
        var localDate = LocalDate.now();
        assertEquals(generatePeriod(localDate),
                mapper.toPeriod(generatePostLoansRepaymentSchedulePeriods(localDate)));
    }

    private Period generatePeriod(LocalDate localDate) {
        return Period.builder()
                .dueDate(localDate)
                .fromDate(localDate)
                .obligationsMetOnDate(localDate)
                .isComplete(true)
                .feeChargesDue(BigDecimal.valueOf(301))
                .totalInstallmentAmountForPeriod(BigDecimal.valueOf(302))
                .principalOriginalDue(BigDecimal.valueOf(303))
                .principalDue(BigDecimal.valueOf(304))
                .principalOutstanding(BigDecimal.valueOf(305))
                .interestOriginalDue(BigDecimal.valueOf(306))
                .interestDue(BigDecimal.valueOf(307))
                .interestOutstanding(BigDecimal.valueOf(308))
                .penaltyChargesDue(BigDecimal.valueOf(309))
                .feeChargesPaid(BigDecimal.valueOf(310))
                .feeChargesWaived(BigDecimal.valueOf(311))
                .feeChargesWrittenOff(BigDecimal.valueOf(312))
                .interestPaid(BigDecimal.valueOf(313))
                .interestWaived(BigDecimal.valueOf(314))
                .interestWrittenOff(BigDecimal.valueOf(315))
                .penaltyChargesOutstanding(BigDecimal.valueOf(316))
                .penaltyChargesPaid(BigDecimal.valueOf(317))
                .penaltyChargesWaived(BigDecimal.valueOf(318))
                .penaltyChargesWrittenOff(BigDecimal.valueOf(319))
                .principalPaid(BigDecimal.valueOf(320))
                .principalWrittenOff(BigDecimal.valueOf(321))
                .totalPaidInAdvanceForPeriod(BigDecimal.valueOf(322))
                .totalPaidLateForPeriod(BigDecimal.valueOf(323))
                .totalWaivedForPeriod(BigDecimal.valueOf(324))
                .totalWrittenOffForPeriod(BigDecimal.valueOf(325))
                .feeChargesOutstanding(BigDecimal.valueOf(326))
                .daysInPeriod(327)
                .period(1)
                .principalDisbursed(BigDecimal.valueOf(329))
                .principalLoanBalanceOutstanding(BigDecimal.valueOf(330))
                .totalPaidForPeriod(BigDecimal.valueOf(331))
                .totalActualCostOfLoanForPeriod(BigDecimal.valueOf(332))
                .totalDueForPeriod(BigDecimal.valueOf(333))
                .totalOriginalDueForPeriod(BigDecimal.valueOf(334))
                .totalOutstandingForPeriod(BigDecimal.valueOf(335))
                .totalCredits(BigDecimal.valueOf(336))
                .totalOverdue(BigDecimal.valueOf(337))
                .build();
    }

    @Test
    void testToSchedule() {
        var localDate = LocalDate.now();
        assertEquals(generateSchedule(localDate),
                mapper.toSchedule(generatePostLoansRequest(localDate), generatePostLoansResponse(localDate), 3));
    }

    private Schedule generateSchedule(LocalDate localDate) {
        return Schedule.builder()
                .internalCustomerId(111L)
                .internalProductId(321L)
                .amount(BigDecimal.valueOf(444))
                .numberOfInstallments(1)
                .loanTermInDays(3)
                .interestRate(BigDecimal.valueOf(10))
                .apr(new BigDecimal("29012.821"))
                .installmentFrequency(InstallmentFrequency.UNKNOWN)
                .installmentStartDate(localDate)
                .installmentEndDate(localDate)
                .firstInstallmentAmount(BigDecimal.valueOf(302))
                .subSeqInstallmentAmount(null)
                .totalPrincipalDisbursed(BigDecimal.valueOf(13))
                .totalPrincipalExpected(BigDecimal.valueOf(14))
                .totalPrincipalPaid(BigDecimal.valueOf(15))
                .totalInterestCharged(BigDecimal.valueOf(10))
                .totalFeeChargesCharged(BigDecimal.valueOf(9))
                .totalPenaltyChargesCharged(BigDecimal.valueOf(12))
                .totalRepaymentExpected(BigDecimal.valueOf(17))
                .totalOutstanding(BigDecimal.valueOf(11))
                .totalCredits(BigDecimal.valueOf(6))
                .totalPaidInAdvance(BigDecimal.valueOf(7))
                .totalPaidLate(BigDecimal.valueOf(8))
                .totalRepayment(BigDecimal.valueOf(16))
                .totalWaived(BigDecimal.valueOf(18))
                .totalWrittenOff(BigDecimal.valueOf(19))
                .periods(List.of(generatePeriod(localDate)))
                .build();
    }

    private PostLoansResponse generatePostLoansResponse(LocalDate localDate) {
        return PostLoansResponse.builder()
                .clientId(1)
                .currency(null)
                .loanId(2L)
                .loanTermInDays(3)
                .officeId(4)
                .periods(Set.of(generatePostLoansRepaymentSchedulePeriods(localDate)))
                .resourceExternalId("resourceExternalId")
                .resourceId(5L)
                .totalCredits(BigDecimal.valueOf(6))
                .totalPaidInAdvance(BigDecimal.valueOf(7))
                .totalPaidLate(BigDecimal.valueOf(8))
                .totalFeeChargesCharged(BigDecimal.valueOf(9))
                .totalInterestCharged(BigDecimal.valueOf(10))
                .totalOutstanding(BigDecimal.valueOf(11))
                .totalPenaltyChargesCharged(BigDecimal.valueOf(12))
                .totalPrincipalDisbursed(BigDecimal.valueOf(13))
                .totalPrincipalExpected(BigDecimal.valueOf(14))
                .totalPrincipalPaid(BigDecimal.valueOf(15))
                .totalRepayment(BigDecimal.valueOf(16))
                .totalRepaymentExpected(BigDecimal.valueOf(17))
                .totalWaived(BigDecimal.valueOf(18))
                .totalWrittenOff(BigDecimal.valueOf(19))
                .build();
    }

    @Test
    void testToPostLoansChargeRequest() {
        var getProductsCharges = List.of(GetProductsCharges.builder()
                        .id(111)
                        .amount(1D)
                        .chargeTimeType(GetChargeTimeType.builder()
                                .id(1)
                                .build())
                        .build(),
                GetProductsCharges.builder()
                        .id(222)
                        .build(),
                GetProductsCharges.builder()
                        .id(888)
                        .chargeTimeType(GetChargeTimeType.builder()
                                .id(8)
                                .build())
                        .build());
        var expected = List.of(PostLoansChargeRequest.builder()
                        .chargeId(111L)
                        .amount(BigDecimal.valueOf(1D))
                        .build(),
                PostLoansChargeRequest.builder()
                        .chargeId(888L)
                        .build());
        assertEquals(expected, mapper.toPostLoansChargeRequest(getProductsCharges));
        assertNull(mapper.toPostLoansChargeRequest(null));
    }

    @Test
    void testToLoanDataTables() {
        var localDate = LocalDate.now();
        var expected = List.of(PostLoansRequestDatatablesInner.builder()
                .registeredTableName("loan_fields")
                .data(PostLoansRequestDatatablesInnerData.builder()
                        .locale("en")
                        .requestId("requestId")
                        .offerId("offerId")
                        .customerId("customerId")
                        .partnerId("partnerId")
                        .partnerName("partnerName")
                        .build())
                .build());
        assertEquals(expected, mapper.toLoanDataTables(generateLoanCreate(localDate)));
        assertNull(mapper.toLoanDataTables(null));
    }

    private LoanCreate generateLoanCreate(LocalDate localDate) {
        return LoanCreate.builder()
                .partnerName("partnerName")
                .requestId("requestId")
                .offerId("offerId")
                .productId(1L)
                .requestDate(localDate)
                .expectedDisbursementDate(localDate)
                .amount(new BigDecimal("2"))
                .numberOfInstallments(3)
                .customerId("customerId")
                .partnerId("partnerId")
                .build();
    }

    @Test
    void testLoanCreationFineractRequest() {
        var localDate = LocalDate.now();
        assertEquals(generatePostLoansRequestForLoanCreation(localDate), mapper.loanCreationFineractRequest(
                123L,
                generateLoanCreate(localDate),
                generateGetLoanProductsProductIdResponse()
        ));
    }

    private PostLoansRequest generatePostLoansRequestForLoanCreation(LocalDate localDate) {
        return PostLoansRequest.builder()
                .dateFormat("dd MMMM yyyy")
                .locale("en")
                .clientId(123L)
                .productId(1)
                .submittedOnDate(localDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("en"))))
                .expectedDisbursementDate(localDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("en"))))
                .principal(new BigDecimal("2"))
                .loanType("individual")
                .loanTermFrequency(3)
                .loanTermFrequencyType(9)
                .numberOfRepayments(3)
                .repaymentEvery(8)
                .repaymentFrequencyType(9)
                .interestRatePerPeriod(new BigDecimal("10"))
                .amortizationType(14)
                .interestType(15)
                .interestCalculationPeriodType(16)
                .transactionProcessingStrategyCode("setTransactionProcessingStrategyCode")
                .graceOnPrincipalPayment(11)
                .graceOnInterestPayment(22)
                .datatables(List.of(PostLoansRequestDatatablesInner.builder()
                        .registeredTableName("loan_fields")
                        .data(PostLoansRequestDatatablesInnerData.builder()
                                .locale("en")
                                .requestId("requestId")
                                .offerId("offerId")
                                .customerId("customerId")
                                .partnerId("partnerId")
                                .partnerName("partnerName")
                                .build())
                        .build()))
                .charges(List.of(PostLoansChargeRequest.builder()
                                .chargeId(5L)
                                .amount(BigDecimal.valueOf(3.0))
                                .build(),
                        PostLoansChargeRequest.builder()
                                .chargeId(1L)
                                .amount(BigDecimal.valueOf(3.0))
                                .build()))
                .build();
    }

    @Test
    void testFromDataTablesToString() {
        var localDate = LocalDate.now();
        assertEquals("m1", mapper.fromDataTablesToString(
                generateDataTables(localDate), "modified_by"
        ));
        assertEquals("p1", mapper.fromDataTablesToString(
                generateDataTables(localDate), "partner_id"
        ));
        assertNull(mapper.fromDataTablesToString(
                generateDataTables(localDate), "partner_name"
        ));
        assertNull(mapper.fromDataTablesToString(null, "partner_id"
        ));
        assertNull(mapper.fromDataTablesToString(null, "partner_name"
        ));
    }

    @Test
    void testFromDataTablesToLocalDate() {
        var localDate = LocalDate.now();
        assertEquals(localDate, mapper.fromDataTablesToLocalDate(
                generateDataTables(localDate), "modified_at"
        ));
        assertNull(mapper.fromDataTablesToLocalDate(null, "partner_id"
        ));
        assertNull(mapper.fromDataTablesToLocalDate(null, "partner_name"
        ));
        List<Object> nullList = new ArrayList<>();
        nullList.add(null);
        assertNull(mapper.fromDataTablesToLocalDate(RunReportsResponse.builder()
                .columnHeaders(List.of(
                        ResultsetColumnHeaderData.builder()
                                .columnName("modified_at")
                                .build()
                ))
                .data(List.of(ResultsetRowData.builder()
                        .row(nullList)
                        .build()))
                .build(), "modified_at"
        ));
    }


    private RunReportsResponse generateDataTables(LocalDate localDate) {
        return RunReportsResponse.builder()
                .columnHeaders(List.of(
                        ResultsetColumnHeaderData.builder()
                                .columnName("modified_at")
                                .build(),
                        ResultsetColumnHeaderData.builder()
                                .columnName("modified_by")
                                .build(),
                        ResultsetColumnHeaderData.builder()
                                .columnName("request_id")
                                .build(),
                        ResultsetColumnHeaderData.builder()
                                .columnName("offer_id")
                                .build(),
                        ResultsetColumnHeaderData.builder()
                                .columnName("customer_id")
                                .build(),
                        ResultsetColumnHeaderData.builder()
                                .columnName("partner_id")
                                .build()
                ))
                .data(List.of(ResultsetRowData.builder()
                        .row(List.of(
                                new int[]{localDate.getYear(), localDate.getMonthValue(), localDate.getDayOfMonth()},
                                "m1",
                                "r1",
                                "o1",
                                "c1",
                                "p1"
                        ))
                        .build()))
                .build();
    }

    @Test
    void testToInstallmentStartDateForLoan() {
        var localDate = LocalDate.now();
        assertEquals(localDate.minus(1, ChronoUnit.DAYS),
                mapper.toInstallmentStartDateForLoan(generateGetLoansLoanIdRepaymentPeriodList(localDate)));
        assertNull(mapper.toInstallmentStartDateForLoan(null));
    }

    @Test
    void testToInstallmentEndDateForLoan() {
        var localDate = LocalDate.now();
        assertEquals(localDate.plus(1, ChronoUnit.DAYS),
                mapper.toInstallmentEndDateForLoan(generateGetLoansLoanIdRepaymentPeriodList(localDate)));
        assertNull(mapper.toInstallmentEndDateForLoan(null));
    }

    @Test
    void testToFirstInstallmentAmountForLoan() {
        var localDate = LocalDate.now();
        assertEquals(BigDecimal.valueOf(102),
                mapper.toFirstInstallmentAmountForLoan(generateGetLoansLoanIdRepaymentPeriodList(localDate)));
        assertNull(mapper.toFirstInstallmentAmountForLoan(null));
    }

    @Test
    void testToSubSeqInstallmentAmountForLoan() {
        var localDate = LocalDate.now();
        assertEquals(BigDecimal.valueOf(202),
                mapper.toSubSeqInstallmentAmountForLoan(generateGetLoansLoanIdRepaymentPeriodList(localDate)));
        assertNull(mapper.toSubSeqInstallmentAmountForLoan(null));
    }

    private List<GetLoansLoanIdRepaymentPeriod> generateGetLoansLoanIdRepaymentPeriodList(LocalDate localDate) {
        var localDate1 = localDate.minus(1, ChronoUnit.DAYS);
        var localDate2 = localDate.plus(1, ChronoUnit.DAYS);
        var item1 = GetLoansLoanIdRepaymentPeriod.builder()
                .dueDate(localDate1)
                .fromDate(localDate1)
                .obligationsMetOnDate(localDate1)
                .complete(true)
                .feeChargesDue(BigDecimal.valueOf(101))
                .totalInstallmentAmountForPeriod(BigDecimal.valueOf(102))
                .principalOriginalDue(BigDecimal.valueOf(103))
                .principalDue(BigDecimal.valueOf(104))
                .principalOutstanding(BigDecimal.valueOf(105))
                .interestOriginalDue(BigDecimal.valueOf(106))
                .interestDue(BigDecimal.valueOf(107))
                .interestOutstanding(BigDecimal.valueOf(108))
                .penaltyChargesDue(BigDecimal.valueOf(109))
                .feeChargesPaid(BigDecimal.valueOf(110))
                .feeChargesWaived(BigDecimal.valueOf(111))
                .feeChargesWrittenOff(BigDecimal.valueOf(112))
                .interestPaid(BigDecimal.valueOf(113))
                .interestWaived(BigDecimal.valueOf(114))
                .interestWrittenOff(BigDecimal.valueOf(115))
                .penaltyChargesOutstanding(BigDecimal.valueOf(116))
                .penaltyChargesPaid(BigDecimal.valueOf(117))
                .penaltyChargesWaived(BigDecimal.valueOf(118))
                .penaltyChargesWrittenOff(BigDecimal.valueOf(119))
                .principalPaid(BigDecimal.valueOf(120))
                .principalWrittenOff(BigDecimal.valueOf(121))
                .totalPaidInAdvanceForPeriod(BigDecimal.valueOf(122))
                .totalPaidLateForPeriod(BigDecimal.valueOf(123))
                .totalWaivedForPeriod(BigDecimal.valueOf(124))
                .totalWrittenOffForPeriod(BigDecimal.valueOf(125))
                .feeChargesOutstanding(BigDecimal.valueOf(126))
                .daysInPeriod(127L)
                .period(1)
                .principalDisbursed(BigDecimal.valueOf(129))
                .principalLoanBalanceOutstanding(BigDecimal.valueOf(130))
                .totalPaidForPeriod(BigDecimal.valueOf(131))
                .totalActualCostOfLoanForPeriod(BigDecimal.valueOf(132))
                .totalDueForPeriod(BigDecimal.valueOf(133))
                .totalOriginalDueForPeriod(BigDecimal.valueOf(134))
                .totalOutstandingForPeriod(BigDecimal.valueOf(135))
                .totalCredits(BigDecimal.valueOf(136))
                .totalOverdue(BigDecimal.valueOf(137))
                .build();
        var item2 = GetLoansLoanIdRepaymentPeriod.builder()
                .dueDate(localDate2)
                .fromDate(localDate2)
                .obligationsMetOnDate(localDate2)
                .complete(true)
                .feeChargesDue(BigDecimal.valueOf(201))
                .totalInstallmentAmountForPeriod(BigDecimal.valueOf(202))
                .principalOriginalDue(BigDecimal.valueOf(203))
                .principalDue(BigDecimal.valueOf(204))
                .principalOutstanding(BigDecimal.valueOf(205))
                .interestOriginalDue(BigDecimal.valueOf(206))
                .interestDue(BigDecimal.valueOf(207))
                .interestOutstanding(BigDecimal.valueOf(208))
                .penaltyChargesDue(BigDecimal.valueOf(209))
                .feeChargesPaid(BigDecimal.valueOf(210))
                .feeChargesWaived(BigDecimal.valueOf(211))
                .feeChargesWrittenOff(BigDecimal.valueOf(212))
                .interestPaid(BigDecimal.valueOf(213))
                .interestWaived(BigDecimal.valueOf(214))
                .interestWrittenOff(BigDecimal.valueOf(215))
                .penaltyChargesOutstanding(BigDecimal.valueOf(216))
                .penaltyChargesPaid(BigDecimal.valueOf(217))
                .penaltyChargesWaived(BigDecimal.valueOf(218))
                .penaltyChargesWrittenOff(BigDecimal.valueOf(219))
                .principalPaid(BigDecimal.valueOf(220))
                .principalWrittenOff(BigDecimal.valueOf(221))
                .totalPaidInAdvanceForPeriod(BigDecimal.valueOf(222))
                .totalPaidLateForPeriod(BigDecimal.valueOf(223))
                .totalWaivedForPeriod(BigDecimal.valueOf(224))
                .totalWrittenOffForPeriod(BigDecimal.valueOf(225))
                .feeChargesOutstanding(BigDecimal.valueOf(226))
                .daysInPeriod(227L)
                .period(2)
                .principalDisbursed(BigDecimal.valueOf(229))
                .principalLoanBalanceOutstanding(BigDecimal.valueOf(230))
                .totalPaidForPeriod(BigDecimal.valueOf(231))
                .totalActualCostOfLoanForPeriod(BigDecimal.valueOf(232))
                .totalDueForPeriod(BigDecimal.valueOf(233))
                .totalOriginalDueForPeriod(BigDecimal.valueOf(234))
                .totalOutstandingForPeriod(BigDecimal.valueOf(235))
                .totalCredits(BigDecimal.valueOf(236))
                .totalOverdue(BigDecimal.valueOf(237))
                .build();
        var item3 = GetLoansLoanIdRepaymentPeriod.builder()
                .dueDate(localDate)
                .fromDate(localDate)
                .obligationsMetOnDate(localDate)
                .complete(true)
                .feeChargesDue(BigDecimal.valueOf(301))
                .totalInstallmentAmountForPeriod(BigDecimal.valueOf(302))
                .principalOriginalDue(BigDecimal.valueOf(303))
                .principalDue(BigDecimal.valueOf(304))
                .principalOutstanding(BigDecimal.valueOf(305))
                .interestOriginalDue(BigDecimal.valueOf(306))
                .interestDue(BigDecimal.valueOf(307))
                .interestOutstanding(BigDecimal.valueOf(308))
                .penaltyChargesDue(BigDecimal.valueOf(309))
                .feeChargesPaid(BigDecimal.valueOf(310))
                .feeChargesWaived(BigDecimal.valueOf(311))
                .feeChargesWrittenOff(BigDecimal.valueOf(312))
                .interestPaid(BigDecimal.valueOf(313))
                .interestWaived(BigDecimal.valueOf(314))
                .interestWrittenOff(BigDecimal.valueOf(315))
                .penaltyChargesOutstanding(BigDecimal.valueOf(316))
                .penaltyChargesPaid(BigDecimal.valueOf(317))
                .penaltyChargesWaived(BigDecimal.valueOf(318))
                .penaltyChargesWrittenOff(BigDecimal.valueOf(319))
                .principalPaid(BigDecimal.valueOf(320))
                .principalWrittenOff(BigDecimal.valueOf(321))
                .totalPaidInAdvanceForPeriod(BigDecimal.valueOf(322))
                .totalPaidLateForPeriod(BigDecimal.valueOf(323))
                .totalWaivedForPeriod(BigDecimal.valueOf(324))
                .totalWrittenOffForPeriod(BigDecimal.valueOf(325))
                .feeChargesOutstanding(BigDecimal.valueOf(326))
                .daysInPeriod(327L)
                .period(3)
                .principalDisbursed(BigDecimal.valueOf(329))
                .principalLoanBalanceOutstanding(BigDecimal.valueOf(330))
                .totalPaidForPeriod(BigDecimal.valueOf(331))
                .totalActualCostOfLoanForPeriod(BigDecimal.valueOf(332))
                .totalDueForPeriod(BigDecimal.valueOf(333))
                .totalOriginalDueForPeriod(BigDecimal.valueOf(334))
                .totalOutstandingForPeriod(BigDecimal.valueOf(335))
                .totalCredits(BigDecimal.valueOf(336))
                .totalOverdue(BigDecimal.valueOf(337))
                .build();
        return List.of(item1, item2, item3);
    }

    @Test
    void testToTransaction() {
        var localDate = LocalDate.now();
        assertEquals(generateTransaction(localDate),
                mapper.toTransaction(generateGetLoansLoanIdTransactions(localDate)));
        assertNull(mapper.toTransaction(null));
    }

    private Transaction generateTransaction(LocalDate localDate) {
        return Transaction.builder()
                .id(1L)
                .date(localDate)
                .type("typeValue")
                .amount(new BigDecimal("2"))
                .currency("CurrencyCode")
                .principalPortion(new BigDecimal("3"))
                .interestPortion(new BigDecimal("4"))
                .feeChargesPortion(new BigDecimal("5"))
                .penaltyChargesPortion(new BigDecimal("6"))
                .overpaymentPortion(new BigDecimal("7"))
                .otherIncomePortion(new BigDecimal("8"))
                .outstandingLoanBalance(new BigDecimal("9"))
                .isReversed(true)
                .reversalDate(localDate)
                .build();
    }

    private GetLoansLoanIdTransactions generateGetLoansLoanIdTransactions(LocalDate localDate) {
        return GetLoansLoanIdTransactions.builder()
                .id(1L)
                .date(localDate)
                .type(GetLoansLoanIdLoanTransactionEnumData.builder()
                        .value("typeValue")
                        .build())
                .amount(new BigDecimal("2"))
                .currency(GetLoansLoanIdCurrency.builder()
                        .code("CurrencyCode")
                        .build())
                .principalPortion(new BigDecimal("3"))
                .interestPortion(new BigDecimal("4"))
                .feeChargesPortion(new BigDecimal("5"))
                .penaltyChargesPortion(new BigDecimal("6"))
                .overpaymentPortion(new BigDecimal("7"))
                .unrecognizedIncomePortion(new BigDecimal("8"))
                .outstandingLoanBalance(new BigDecimal("9"))
                .manuallyReversed(true)
                .reversedOnDate(localDate)
                .build();
    }

    @Test
    void testToDomain_Loan() {
        var localDate = LocalDate.now();
        assertEquals(generateLoan(localDate),
                mapper.toDomain(generateGetLoansLoanIdResponse(localDate), generateDataTables(localDate), 3));
    }

    private Loan generateLoan(LocalDate localDate) {
        return Loan.builder()
                .id(1L)
                .status("statusValue")
                .internalCustomerId(CustomerInternalId.of(2L))
                .internalProductId(LoanProductId.of(3L))
                .currencyCode("currencyCode")
                .createdAt(localDate)
                .createdBy("submittedByUsername")
                .approvedAt(localDate)
                .approvedBy("approvedByUsername")
                .disbursedAt(localDate)
                .disbursedBy("disbursedByUsername")
                .closedAt(localDate)
                .closedBy("closedByName")
                .updatedAt(localDate)
                .updatedBy("m1")
                .requestId("r1")
                .offerId("o1")
                .customerId("c1")
                .partnerId("p1")
                .partnerName(null)
                .amount(new BigDecimal("4"))
                .numberOfInstallments(5)
                .loanTermInDays(6)
                .interestRate(new BigDecimal("6"))
                .apr(new BigDecimal("14600.000"))
                .installmentFrequency(InstallmentFrequency.WEEKLY)
                .installmentStartDate(localDate)
                .installmentEndDate(localDate)
                .firstInstallmentAmount(BigDecimal.valueOf(302))
                .subSeqInstallmentAmount(null)
                .totalPrincipalDisbursed(new BigDecimal("10"))
                .totalPrincipalExpected(new BigDecimal("11"))
                .totalPrincipalPaid(new BigDecimal("12"))
                .totalInterestCharged(new BigDecimal("9"))
                .totalFeeChargesCharged(new BigDecimal("7"))
                .totalPenaltyChargesCharged(new BigDecimal("8"))
                .totalRepaymentExpected(new BigDecimal("13"))
                .totalOutstanding(new BigDecimal("14"))
                .totalCredits(new BigDecimal("15"))
                .totalPaidInAdvance(new BigDecimal("16"))
                .totalPaidLate(new BigDecimal("17"))
                .totalRepayment(new BigDecimal("18"))
                .totalWaived(new BigDecimal("19"))
                .totalWrittenOff(new BigDecimal("20"))
                .isInArrears(true)
                .isNPA(true)
                .periods(List.of(generatePeriod(localDate)))
                .transactions(List.of(generateTransaction(localDate)))
                .build();
    }

    private GetLoansLoanIdResponse generateGetLoansLoanIdResponse(LocalDate localDate) {
        return GetLoansLoanIdResponse.builder()
                .id(1L)
                .status(GetLoansLoanIdStatus.builder()
                        .value("statusValue")
                        .build())
                .clientId(2L)
                .loanProductId(3L)
                .currency(GetLoansLoanIdCurrency.builder()
                        .code("currencyCode")
                        .build())
                .timeline(GetLoansLoanIdTimeline.builder()
                        .submittedOnDate(localDate)
                        .submittedByUsername("submittedByUsername")
                        .approvedOnDate(localDate)
                        .approvedByUsername("approvedByUsername")
                        .actualDisbursementDate(localDate)
                        .disbursedByUsername("disbursedByUsername")
                        .closedOnDate(localDate)
                        .closedByName("closedByName")
                        .build())
                .principal(new BigDecimal("4"))
                .numberOfRepayments(5)
                .repaymentFrequencyType(GetLoansLoanIdRepaymentFrequencyType.builder()
                        .id(1)
                        .build())
                .repaymentSchedule(GetLoansLoanIdRepaymentSchedule.builder()
                        .loanTermInDays(6)
                        .totalFeeChargesCharged(new BigDecimal("7"))
                        .totalPenaltyChargesCharged(new BigDecimal("8"))
                        .totalInterestCharged(new BigDecimal("9"))
                        .totalPrincipalDisbursed(new BigDecimal("10"))
                        .periods(List.of(generateGetLoansLoanIdRepaymentPeriod(localDate)))
                        .totalPrincipalExpected(new BigDecimal("11"))
                        .totalPrincipalPaid(new BigDecimal("12"))
                        .totalRepaymentExpected(new BigDecimal("13"))
                        .totalOutstanding(new BigDecimal("14"))
                        .totalCredits(new BigDecimal("15"))
                        .totalPaidInAdvance(new BigDecimal("16"))
                        .totalPaidLate(new BigDecimal("17"))
                        .totalRepayment(new BigDecimal("18"))
                        .totalWaived(new BigDecimal("19"))
                        .totalWrittenOff(new BigDecimal("20"))
                        .build())
                .interestRatePerPeriod(new BigDecimal("6"))
                .inArrears(true)
                .isNPA(true)
                .transactions(List.of(generateGetLoansLoanIdTransactions(localDate)))
                .build();
    }

    private GetLoansLoanIdRepaymentPeriod generateGetLoansLoanIdRepaymentPeriod(LocalDate localDate) {
        return GetLoansLoanIdRepaymentPeriod.builder()
                .dueDate(localDate)
                .fromDate(localDate)
                .obligationsMetOnDate(localDate)
                .complete(true)
                .feeChargesDue(BigDecimal.valueOf(301))
                .totalInstallmentAmountForPeriod(BigDecimal.valueOf(302))
                .principalOriginalDue(BigDecimal.valueOf(303))
                .principalDue(BigDecimal.valueOf(304))
                .principalOutstanding(BigDecimal.valueOf(305))
                .interestOriginalDue(BigDecimal.valueOf(306))
                .interestDue(BigDecimal.valueOf(307))
                .interestOutstanding(BigDecimal.valueOf(308))
                .penaltyChargesDue(BigDecimal.valueOf(309))
                .feeChargesPaid(BigDecimal.valueOf(310))
                .feeChargesWaived(BigDecimal.valueOf(311))
                .feeChargesWrittenOff(BigDecimal.valueOf(312))
                .interestPaid(BigDecimal.valueOf(313))
                .interestWaived(BigDecimal.valueOf(314))
                .interestWrittenOff(BigDecimal.valueOf(315))
                .penaltyChargesOutstanding(BigDecimal.valueOf(316))
                .penaltyChargesPaid(BigDecimal.valueOf(317))
                .penaltyChargesWaived(BigDecimal.valueOf(318))
                .penaltyChargesWrittenOff(BigDecimal.valueOf(319))
                .principalPaid(BigDecimal.valueOf(320))
                .principalWrittenOff(BigDecimal.valueOf(321))
                .totalPaidInAdvanceForPeriod(BigDecimal.valueOf(322))
                .totalPaidLateForPeriod(BigDecimal.valueOf(323))
                .totalWaivedForPeriod(BigDecimal.valueOf(324))
                .totalWrittenOffForPeriod(BigDecimal.valueOf(325))
                .feeChargesOutstanding(BigDecimal.valueOf(326))
                .daysInPeriod(327L)
                .period(1)
                .principalDisbursed(BigDecimal.valueOf(329))
                .principalLoanBalanceOutstanding(BigDecimal.valueOf(330))
                .totalPaidForPeriod(BigDecimal.valueOf(331))
                .totalActualCostOfLoanForPeriod(BigDecimal.valueOf(332))
                .totalDueForPeriod(BigDecimal.valueOf(333))
                .totalOriginalDueForPeriod(BigDecimal.valueOf(334))
                .totalOutstandingForPeriod(BigDecimal.valueOf(335))
                .totalCredits(BigDecimal.valueOf(336))
                .totalOverdue(BigDecimal.valueOf(337))
                .build();
    }

    @Test
    void testToAvailableLoanStatus() {
        assertEquals(AvailableLoanStatus.NEW, mapper.toAvailableLoanStatus(100));
        assertEquals(AvailableLoanStatus.APPROVED, mapper.toAvailableLoanStatus(200));
        assertEquals(AvailableLoanStatus.REJECTED, mapper.toAvailableLoanStatus(300));
        assertEquals(AvailableLoanStatus.WITHDRAWN, mapper.toAvailableLoanStatus(301));
        assertEquals(AvailableLoanStatus.ACTIVE, mapper.toAvailableLoanStatus(302));
        assertEquals(AvailableLoanStatus.OVERDUE, mapper.toAvailableLoanStatus(400));
        assertEquals(AvailableLoanStatus.NPA, mapper.toAvailableLoanStatus(500));
        assertEquals(AvailableLoanStatus.CLOSED, mapper.toAvailableLoanStatus(600));
        assertEquals(AvailableLoanStatus.WRITTENOFF, mapper.toAvailableLoanStatus(601));
        assertEquals(AvailableLoanStatus.RESCHEDULED, mapper.toAvailableLoanStatus(602));
        assertEquals(AvailableLoanStatus.OVERPAID, mapper.toAvailableLoanStatus(700));
        assertNull(mapper.toAvailableLoanStatus(null));
        assertNull(mapper.toAvailableLoanStatus(800));
    }
}