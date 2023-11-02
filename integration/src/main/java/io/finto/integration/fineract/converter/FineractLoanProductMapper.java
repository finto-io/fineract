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
import io.finto.domain.loanproduct.Fee;
import io.finto.domain.loanproduct.FeeCalcType;
import io.finto.domain.loanproduct.FeeCreate;
import io.finto.domain.loanproduct.FeeType;
import io.finto.domain.loanproduct.InterestType;
import io.finto.domain.loanproduct.LoanProduct;
import io.finto.domain.loanproduct.LoanProductCreate;
import io.finto.fineract.sdk.models.ChargeData;
import io.finto.fineract.sdk.models.GetLoanProductsProductIdResponse;
import io.finto.fineract.sdk.models.GetLoansLoanIdRepaymentPeriod;
import io.finto.fineract.sdk.models.GetLoansLoanIdResponse;
import io.finto.fineract.sdk.models.GetLoansLoanIdTransactions;
import io.finto.fineract.sdk.models.GetProductsCharges;
import io.finto.fineract.sdk.models.PostLoanProductsRequest;
import io.finto.fineract.sdk.models.PostLoansChargeRequest;
import io.finto.fineract.sdk.models.PostLoansRepaymentSchedulePeriods;
import io.finto.fineract.sdk.models.PostLoansRequest;
import io.finto.fineract.sdk.models.PostLoansRequestDatatablesInner;
import io.finto.fineract.sdk.models.PostLoansRequestDatatablesInnerData;
import io.finto.fineract.sdk.models.PostLoansResponse;
import io.finto.fineract.sdk.models.ResultsetColumnHeaderData;
import io.finto.fineract.sdk.models.RunReportsResponse;
import io.finto.integration.fineract.dto.LoanProductDetailsCreateDto;
import io.finto.integration.fineract.dto.LoanProductDetailsDto;
import org.jetbrains.annotations.Nullable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static io.finto.fineract.sdk.Constants.*;
import static io.finto.fineract.sdk.CustomDatatableNames.LOAN_FIELDS;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface FineractLoanProductMapper {

    FineractLoanProductMapper INSTANCE = Mappers.getMapper(FineractLoanProductMapper.class);

    @Named("toChargeTimeType")
    default Integer toChargeTimeType(FeeType value) {
        switch (value) {
            case FEES:
                return CHARGE_TIME_TYPE_FEES_ID;
            case LATE_PAYMENT:
                return CHARGE_TIME_TYPE_LATE_PAYMENT_ID;
            default:
                return CHARGE_TIME_TYPE_EARLY_SETTLEMENT_ID;
        }
    }

    @Named("toChargeCalculationType")
    default Integer toChargeCalculationType(FeeCalcType value) {
        if (value == FeeCalcType.FIXED) {
            return CHARGE_CALCULATION_TYPE_FIXED_ID;
        } else {
            return CHARGE_CALCULATION_TYPE_PERCENTAGE_ID;
        }
    }

    @Named("toPenalty")
    default Boolean toPenalty(FeeType value) {
        return value == FeeType.LATE_PAYMENT;
    }

    default Integer getNumberOfRepayments(Integer minimumPeriod, Integer maximumPeriod, Integer numberOfRepayments) {
        if (minimumPeriod == null && maximumPeriod == null) {
            return numberOfRepayments;
        }
        Integer min = minimumPeriod == null ? 0 : minimumPeriod;
        Integer max = maximumPeriod == null ? 0 : maximumPeriod;
        return (min + max) / 2;
    }

    @Named("toInterestType")
    default Integer toInterestType(InterestType value) {
        return value == InterestType.FIXED ? INTEREST_TYPE_FIXED_ID : INTEREST_TYPE_REDUCING_ID;
    }

    @Named("toCharges")
    default List<ChargeData> toCharges(List<ChargeId> value) {
        if (value == null) {
            return null;
        }
        return value.stream().map(item -> ChargeData.builder().id(item.getValue()).build()).collect(Collectors.toList());
    }

    @Named("fromOptional")
    default <T> T fromOptional(Optional<T> value) {
        return value.orElse(null);
    }

    @Mapping(target = "numberOfRepayments", expression = "java(getNumberOfRepayments(request.getMinimumPeriod().orElse(null), request.getMaximumPeriod().orElse(null), request.getNumberOfRepayments().orElse(null)))")
    @Mapping(target = "minNumberOfRepayments", source = "request.minimumPeriod", qualifiedByName = "fromOptional")
    @Mapping(target = "currencyCode", source = "request.currencyCode", qualifiedByName = "fromOptional")
    @Mapping(target = "amortizationType", source = "request.amortizationType", qualifiedByName = "fromOptional")
    @Mapping(target = "accountingRule", source = "request.accountingRule", qualifiedByName = "fromOptional")
    @Mapping(target = "daysInMonthType", source = "request.daysInMonthType", qualifiedByName = "fromOptional")
    @Mapping(target = "daysInYearType", source = "request.daysInYearType", qualifiedByName = "fromOptional")
    @Mapping(target = "digitsAfterDecimal", source = "request.digitsAfterDecimal", qualifiedByName = "fromOptional")
    @Mapping(target = "interestCalculationPeriodType", source = "request.interestCalculationPeriodType", qualifiedByName = "fromOptional")
    @Mapping(target = "transactionProcessingStrategyCode", source = "request.transactionProcessingStrategyCode", qualifiedByName = "fromOptional")
    @Mapping(target = "maxNumberOfRepayments", source = "request.maximumPeriod", qualifiedByName = "fromOptional")
    @Mapping(target = "repaymentEvery", constant = "1")
    @Mapping(target = "repaymentFrequencyType", constant = "2")
    @Mapping(target = "interestRatePerPeriod", source = "request.interest")
    @Mapping(target = "interestRateFrequencyType", constant = "3")
    @Mapping(target = "interestType", source = "request.interestType", qualifiedByName = "toInterestType")
    @Mapping(target = "graceOnPrincipalPayment", source = "request.installmentGracePeriod", qualifiedByName = "fromOptional")
    @Mapping(target = "graceOnInterestPayment", source = "request.installmentGracePeriod", qualifiedByName = "fromOptional")
    @Mapping(target = "isInterestRecalculationEnabled", constant = "false")
    @Mapping(target = "fundSourceAccountId", expression = "java(request.getAccountingRule().orElse(0) == 1 ? null : io.finto.fineract.sdk.Constants.FUND_SOURCE_ID)")
    @Mapping(target = "loanPortfolioAccountId", expression = "java(request.getAccountingRule().orElse(0) == 1 ? null : io.finto.fineract.sdk.Constants.LOAN_PORTFOLIO_ID)")
    @Mapping(target = "receivableFeeAccountId", expression = "java(request.getAccountingRule().orElse(0) == 1 ? null : io.finto.fineract.sdk.Constants.FEES_RECEIVABLE_ID)")
    @Mapping(target = "receivableInterestAccountId", expression = "java(request.getAccountingRule().orElse(0) == 1 ? null : io.finto.fineract.sdk.Constants.INTEREST_RECEIVABLE_ID)")
    @Mapping(target = "receivablePenaltyAccountId", expression = "java(request.getAccountingRule().orElse(0) == 1 ? null : io.finto.fineract.sdk.Constants.PENALTIES_RECEIVABLE_ID)")
    @Mapping(target = "transfersInSuspenseAccountId", expression = "java(request.getAccountingRule().orElse(0) == 1 ? null : io.finto.fineract.sdk.Constants.TRANSFER_IN_SUSPENSE_ID)")
    @Mapping(target = "interestOnLoanAccountId", expression = "java(request.getAccountingRule().orElse(0) == 1 ? null : io.finto.fineract.sdk.Constants.INCOME_FROM_INTEREST_ID)")
    @Mapping(target = "incomeFromFeeAccountId", expression = "java(request.getAccountingRule().orElse(0) == 1 ? null : io.finto.fineract.sdk.Constants.INCOME_FROM_FEES_ID)")
    @Mapping(target = "incomeFromPenaltyAccountId", expression = "java(request.getAccountingRule().orElse(0) == 1 ? null : io.finto.fineract.sdk.Constants.INCOME_FROM_PENALTIES_ID)")
    @Mapping(target = "incomeFromRecoveryAccountId", expression = "java(request.getAccountingRule().orElse(0) == 1 ? null : io.finto.fineract.sdk.Constants.INCOME_FROM_PENALTIES_ID)")
    @Mapping(target = "writeOffAccountId", expression = "java(request.getAccountingRule().orElse(0) == 1 ? null : io.finto.fineract.sdk.Constants.LOSSES_WRITTEN_OFF_ID)")
    @Mapping(target = "overpaymentLiabilityAccountId", expression = "java(request.getAccountingRule().orElse(0) == 1 ? null : io.finto.fineract.sdk.Constants.OVER_PAYMENT_LIABILITY_ID)")
    @Mapping(target = "charges", source = "charges", qualifiedByName = "toCharges")
    @Mapping(target = "locale", constant = "en")
    PostLoanProductsRequest loanProductCreationFineractRequest(LoanProductCreate request, List<ChargeId> charges);

    @Mapping(target = "name", expression = "java(feeCreate.getFeeName() + shortName)")
    ChargeCreate toChargeCreate(FeeCreate feeCreate, String shortName);

    @Mapping(target = "dateFormat", expression = "java(io.finto.fineract.sdk.Constants.DATE_TIME_FORMAT_WITHOUT_SEC_PATTERN)")
    @Mapping(target = "locale", constant = "en")
    @Mapping(target = "loadedAt", expression = "java(io.finto.fineract.sdk.Constants.DEFAULT_DATE_TIME_WITHOUT_SEC_FORMATTER.format(localDateTime))")
    @Mapping(target = "loadedBy", expression = "java(io.finto.fineract.sdk.Constants.USER)")
    @Mapping(target = "partnerId", source = "request.partnerId")
    @Mapping(target = "partnerName", source = "request.partnerName", qualifiedByName = "fromOptional")
    @Mapping(target = "externalId", source = "request.externalId", qualifiedByName = "fromOptional")
    @Mapping(target = "latePaymentBlockUser", source = "request.latePaymentBlockUser", qualifiedByName = "fromOptional")
    @Mapping(target = "earlySettlementAllowed", source = "request.earlySettlementAllowed", qualifiedByName = "fromOptional")
    LoanProductDetailsCreateDto toLoanProductDetailsCreateDto(LoanProductCreate request, LocalDateTime localDateTime);

    @Named("toActive")
    default Boolean toActive(String value) {
        return "loanProduct.active".equals(value);
    }

    @Named("toInterestType")
    default String toInterestType(Long value) {
        if (value == INTEREST_TYPE_FIXED_ID.intValue()) {
            return "FIXED";
        } else {
            return "REDUCING";
        }
    }

    @Named("toFeeCalcType")
    default FeeCalcType toFeeCalcType(Integer value) {
        if (Objects.equals(value, CHARGE_CALCULATION_TYPE_FIXED_ID)) {
            return FeeCalcType.FIXED;
        } else {
            return FeeCalcType.PERCENTAGE;
        }
    }

    @Named("toFeeType")
    default FeeType toFeeType(Integer value) {
        if (Objects.equals(value, CHARGE_TIME_TYPE_LATE_PAYMENT_ID)) {
            return FeeType.LATE_PAYMENT;
        } else if (Objects.equals(value, CHARGE_TIME_TYPE_EARLY_SETTLEMENT_ID)) {
            return FeeType.EARLY_SETTLEMENT;
        } else {
            return FeeType.FEES;
        }
    }

    @Named("toFeeName")
    default String toFeeName(String value) {
        return value.substring(0, value.length() - 4);
    }

    @Mapping(target = "internalFeeId", source = "id")
    @Mapping(target = "feeCalcType", source = "chargeCalculationType.id", qualifiedByName = "toFeeCalcType")
    @Mapping(target = "feeAmount", source = "amount")
    @Mapping(target = "fromRange", source = "minCap")
    @Mapping(target = "toRange", source = "maxCap")
    @Mapping(target = "feeType", source = "chargeTimeType.id", qualifiedByName = "toFeeType")
    @Mapping(target = "feeName", source = "name", qualifiedByName = "toFeeName")
    Fee toDomain(GetProductsCharges source);

    @Named("fromZonedDateTime")
    default String fromZonedDateTime(ZonedDateTime value) {
        if (value == null) {
            return null;
        }
        return value.format(io.finto.fineract.sdk.Constants.LOAN_PRODUCT_DATE_TIME_FORMATTER);
    }

    @Mapping(target = "fees", source = "loanProduct.charges")
    @Mapping(target = "internalId", source = "loanProduct.id")
    @Mapping(target = "name", source = "loanProduct.name")
    @Mapping(target = "shortName", source = "loanProduct.shortName")
    @Mapping(target = "active", source = "loanProduct.status", qualifiedByName = "toActive")
    @Mapping(target = "currencyCode", source = "loanProduct.currency.code")
    @Mapping(target = "principal", source = "loanProduct.principal")
    @Mapping(target = "minPrincipal", source = "loanProduct.minPrincipal")
    @Mapping(target = "maxPrincipal", source = "loanProduct.maxPrincipal")
    @Mapping(target = "numberOfRepayments", source = "loanProduct.numberOfRepayments")
    @Mapping(target = "minNumberOfRepayments", source = "loanProduct.minNumberOfRepayments")
    @Mapping(target = "maxNumberOfRepayments", source = "loanProduct.maxNumberOfRepayments")
    @Mapping(target = "repaymentEvery", source = "loanProduct.repaymentEvery")
    @Mapping(target = "repaymentFrequencyType", source = "loanProduct.repaymentFrequencyType")
    @Mapping(target = "interest", source = "loanProduct.interestRatePerPeriod")
    @Mapping(target = "minInterestRatePerPeriod", source = "loanProduct.minInterestRatePerPeriod")
    @Mapping(target = "maxInterestRatePerPeriod", source = "loanProduct.maxInterestRatePerPeriod")
    @Mapping(target = "interestRateFrequencyType", source = "loanProduct.interestRateFrequencyType")
    @Mapping(target = "amortizationType", source = "loanProduct.amortizationType")
    @Mapping(target = "interestType.id", source = "loanProduct.interestType.id")
    @Mapping(target = "interestType.value", source = "loanProduct.interestType.id", qualifiedByName = "toInterestType")
    @Mapping(target = "interestCalculationPeriodType", source = "loanProduct.interestCalculationPeriodType")
    @Mapping(target = "transactionProcessingStrategy.code", source = "loanProduct.transactionProcessingStrategyCode")
    @Mapping(target = "transactionProcessingStrategy.value", source = "loanProduct.transactionProcessingStrategyName")
    @Mapping(target = "installmentGracePeriod", source = "loanProduct.graceOnPrincipalPayment")
    @Mapping(target = "daysInMonthType", source = "loanProduct.daysInMonthType")
    @Mapping(target = "daysInYearType", source = "loanProduct.daysInYearType")
    @Mapping(target = "isInterestRecalculationEnabled", source = "loanProduct.isInterestRecalculationEnabled")
    @Mapping(target = "accountingRule", source = "loanProduct.accountingRule")
    @Mapping(target = "accountingMappings", source = "loanProduct.accountingMappings")
    @Mapping(target = "allowAttributeOverrides", source = "loanProduct.allowAttributeOverrides")
    @Mapping(target = "createdAt", source = "additionalDetails.loadedAt", qualifiedByName = "fromZonedDateTime")
    @Mapping(target = "createdBy", source = "additionalDetails.loadedBy")
    @Mapping(target = "updatedAt", source = "additionalDetails.modifiedAt", qualifiedByName = "fromZonedDateTime")
    @Mapping(target = "updatedBy", source = "additionalDetails.modifiedBy")
    @Mapping(target = "closedAt", source = "additionalDetails.closedAt", qualifiedByName = "fromZonedDateTime")
    @Mapping(target = "closedBy", source = "additionalDetails.closedBy")
    @Mapping(target = "partnerId", source = "additionalDetails.partnerId")
    @Mapping(target = "partnerName", source = "additionalDetails.partnerName")
    @Mapping(target = "externalId", source = "additionalDetails.externalId")
    @Mapping(target = "latePaymentBlockUser", source = "additionalDetails.latePaymentBlockUser")
    @Mapping(target = "earlySettlementAllowed", source = "additionalDetails.earlySettlementAllowed")
    LoanProduct toDomain(GetLoanProductsProductIdResponse loanProduct, LoanProductDetailsDto additionalDetails);

    @Named("fromLocalDate")
    default String fromLocalDate(LocalDate value) {
        if (value == null) {
            return null;
        }
        return value.format(SCHEDULE_DATE_FORMATTER);
    }

    @Named("toPostLoansChargeRequest")
    default List<PostLoansChargeRequest> toPostLoansChargeRequest(List<GetProductsCharges> value) {
        if (value == null) {
            return null;
        }
        return value.stream().filter(item -> item.getChargeTimeType() != null &&
                        item.getChargeTimeType().getId() != null &&
                        (item.getChargeTimeType().getId() == 1 || item.getChargeTimeType().getId() == 8))
                .map(item -> {
                    var builder = PostLoansChargeRequest.builder();
                    if (item.getId() != null) {
                        builder.chargeId(Long.valueOf(item.getId()));
                    }
                    if (item.getAmount() != null) {
                        builder.amount(BigDecimal.valueOf(item.getAmount()));
                    }
                    return builder.build();

                })
                .collect(Collectors.toList());
    }

    @Mapping(target = "dateFormat", expression = "java(io.finto.fineract.sdk.Constants.SCHEDULE_DATE_FORMAT_PATTERN)")
    @Mapping(target = "locale", constant = "en")
    @Mapping(target = "clientId", source = "request.internalCustomerId")
    @Mapping(target = "productId", source = "loanProductId.value")
    @Mapping(target = "submittedOnDate", source = "request.requestDate", qualifiedByName = "fromLocalDate")
    @Mapping(target = "expectedDisbursementDate", source = "request.expectedDisbursementDate", qualifiedByName = "fromLocalDate")
    @Mapping(target = "principal", source = "request.amount")
    @Mapping(target = "loanType", constant = "individual")
    @Mapping(target = "loanTermFrequency", source = "request.numberOfInstallments")
    @Mapping(target = "loanTermFrequencyType", source = "loanProduct.repaymentFrequencyType.id")
    @Mapping(target = "numberOfRepayments", source = "request.numberOfInstallments")
    @Mapping(target = "repaymentEvery", source = "loanProduct.repaymentEvery")
    @Mapping(target = "repaymentFrequencyType", source = "loanProduct.repaymentFrequencyType.id")
    @Mapping(target = "interestRatePerPeriod", source = "loanProduct.interestRatePerPeriod")
    @Mapping(target = "amortizationType", source = "loanProduct.amortizationType.id")
    @Mapping(target = "interestType", source = "loanProduct.interestType.id")
    @Mapping(target = "interestCalculationPeriodType", source = "loanProduct.interestCalculationPeriodType.id")
    @Mapping(target = "transactionProcessingStrategyCode", source = "loanProduct.transactionProcessingStrategyCode")
    @Mapping(target = "daysInYearType", expression = "java(null)")
    @Mapping(target = "fixedPrincipalPercentagePerInstallment", expression = "java(null)")
    @Mapping(target = "graceOnPrincipalPayment", source = "loanProduct.graceOnPrincipalPayment")
    @Mapping(target = "graceOnInterestPayment", source = "loanProduct.graceOnInterestPayment")
    @Mapping(target = "charges", source = "loanProduct.charges", qualifiedByName = "toPostLoansChargeRequest")
    PostLoansRequest loanScheduleCalculationFineractRequest(LoanProductId loanProductId,
                                                            ScheduleCalculate request,
                                                            GetLoanProductsProductIdResponse loanProduct);

    @Named("toNumberOfInstallments")
    default Integer toNumberOfInstallments(Set<PostLoansRepaymentSchedulePeriods> value) {
        if (value == null) {
            return null;
        }
        var intArray = value.stream().map(PostLoansRepaymentSchedulePeriods::getPeriod)
                .filter(Objects::nonNull).collect(Collectors.toList());
        return intArray.stream().max(Comparator.comparing(Integer::intValue)).orElse(null);
    }

    default BigDecimal calculateApr(BigDecimal totalFeeChargesCharged,
                                    BigDecimal totalPenaltyChargesCharged,
                                    BigDecimal totalInterestCharged,
                                    BigDecimal totalPrincipalDisbursed,
                                    Integer loanTermInDays,
                                    Integer digitsAfterDecimal) {
        return totalFeeChargesCharged.add(totalPenaltyChargesCharged).add(totalInterestCharged)
                .multiply(BigDecimal.valueOf(365))
                .multiply(BigDecimal.valueOf(100))
                .divide(totalPrincipalDisbursed, digitsAfterDecimal, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(loanTermInDays), digitsAfterDecimal, RoundingMode.HALF_UP)
                .setScale(digitsAfterDecimal, RoundingMode.HALF_UP);
    }

    @Named("toInstallmentFrequency")
    default InstallmentFrequency toInstallmentFrequency(Integer value) {
        switch (value) {
            case 0:
                return InstallmentFrequency.DAILY;
            case 1:
                return InstallmentFrequency.WEEKLY;
            case 2:
                return InstallmentFrequency.MONTHLY;
            default:
                return InstallmentFrequency.UNKNOWN;
        }
    }

    @Named("toInstallmentStartDate")
    default LocalDate toInstallmentStartDate(Set<PostLoansRepaymentSchedulePeriods> value) {
        if (value == null) {
            return null;
        }
        return value.stream().map(PostLoansRepaymentSchedulePeriods::getDueDate)
                .filter(Objects::nonNull)
                .min(LocalDate::compareTo).orElse(null);
    }

    @Named("toInstallmentEndDate")
    default LocalDate toInstallmentEndDate(Set<PostLoansRepaymentSchedulePeriods> value) {
        if (value == null) {
            return null;
        }
        return value.stream().map(PostLoansRepaymentSchedulePeriods::getDueDate)
                .filter(Objects::nonNull)
                .max(LocalDate::compareTo).orElse(null);
    }

    @Named("toFirstInstallmentAmount")
    default BigDecimal toFirstInstallmentAmount(Set<PostLoansRepaymentSchedulePeriods> value) {
        if (value == null) {
            return null;
        }

        return value.stream().filter(item -> item.getPeriod() != null && item.getPeriod() == 1).findFirst()
                .map(PostLoansRepaymentSchedulePeriods::getTotalInstallmentAmountForPeriod).orElse(null);
    }

    @Named("toSubSeqInstallmentAmount")
    default BigDecimal toSubSeqInstallmentAmount(Set<PostLoansRepaymentSchedulePeriods> value) {
        if (value == null || value.size() < 2) {
            return null;
        }

        return value.stream().filter(item -> item.getPeriod() != null && item.getPeriod() == 2).findFirst()
                .map(PostLoansRepaymentSchedulePeriods::getTotalInstallmentAmountForPeriod).orElse(null);
    }

    @Mapping(target = "isComplete", source = "complete")
    Period toPeriod(PostLoansRepaymentSchedulePeriods sorce);

    @Mapping(target = "internalCustomerId", source = "request.clientId")
    @Mapping(target = "internalProductId", source = "request.productId")
    @Mapping(target = "amount", source = "request.principal")
    @Mapping(target = "numberOfInstallments", source = "response.periods", qualifiedByName = "toNumberOfInstallments")
    @Mapping(target = "loanTermInDays", source = "response.loanTermInDays")
    @Mapping(target = "interestRate", source = "request.interestRatePerPeriod")
    @Mapping(target = "apr", expression = "java(calculateApr(response.getTotalFeeChargesCharged()," +
            "response.getTotalPenaltyChargesCharged()," +
            "response.getTotalInterestCharged()," +
            "response.getTotalPrincipalDisbursed()," +
            "response.getLoanTermInDays()," +
            "digitsAfterDecimal))")
    @Mapping(target = "installmentFrequency", source = "request.repaymentFrequencyType", qualifiedByName = "toInstallmentFrequency")
    @Mapping(target = "installmentStartDate", source = "response.periods", qualifiedByName = "toInstallmentStartDate")
    @Mapping(target = "installmentEndDate", source = "response.periods", qualifiedByName = "toInstallmentEndDate")
    @Mapping(target = "firstInstallmentAmount", source = "response.periods", qualifiedByName = "toFirstInstallmentAmount")
    @Mapping(target = "subSeqInstallmentAmount", source = "response.periods", qualifiedByName = "toSubSeqInstallmentAmount")
    @Mapping(target = "totalPrincipalDisbursed", source = "response.totalPrincipalDisbursed")
    @Mapping(target = "totalPrincipalExpected", source = "response.totalPrincipalExpected")
    @Mapping(target = "totalPrincipalPaid", source = "response.totalPrincipalPaid")
    @Mapping(target = "totalInterestCharged", source = "response.totalInterestCharged")
    @Mapping(target = "totalFeeChargesCharged", source = "response.totalFeeChargesCharged")
    @Mapping(target = "totalPenaltyChargesCharged", source = "response.totalPenaltyChargesCharged")
    @Mapping(target = "totalRepaymentExpected", source = "response.totalRepaymentExpected")
    @Mapping(target = "totalOutstanding", source = "response.totalOutstanding")
    @Mapping(target = "totalCredits", source = "response.totalCredits")
    @Mapping(target = "totalPaidInAdvance", source = "response.totalPaidInAdvance")
    @Mapping(target = "totalPaidLate", source = "response.totalPaidLate")
    @Mapping(target = "totalRepayment", source = "response.totalRepayment")
    @Mapping(target = "totalWaived", source = "response.totalWaived")
    @Mapping(target = "totalWrittenOff", source = "response.totalWrittenOff")
    Schedule toSchedule(PostLoansRequest request, PostLoansResponse response, Integer digitsAfterDecimal);

    @Named("toLoanDataTables")
    default List<PostLoansRequestDatatablesInner> toLoanDataTables(LoanCreate request) {
        if (request == null) {
            return null;
        }

        return List.of(PostLoansRequestDatatablesInner.builder()
                .registeredTableName(LOAN_FIELDS)
                .data(PostLoansRequestDatatablesInnerData.builder()
                        .locale(LOCALE)
                        .requestId(request.getRequestId())
                        .offerId(request.getOfferId())
                        .customerId(request.getCustomerId())
                        .partnerId(request.getPartnerId())
                        .partnerName(request.getPartnerName().orElse(null))
                        .build())
                .build());
    }

    @Mapping(target = "dateFormat", expression = "java(io.finto.fineract.sdk.Constants.SCHEDULE_DATE_FORMAT_PATTERN)")
    @Mapping(target = "locale", expression = "java(io.finto.fineract.sdk.Constants.LOCALE)")
    @Mapping(target = "clientId", source = "internalCustomerId")
    @Mapping(target = "productId", source = "request.productId")
    @Mapping(target = "submittedOnDate", source = "request.requestDate", qualifiedByName = "fromLocalDate")
    @Mapping(target = "expectedDisbursementDate", source = "request.expectedDisbursementDate", qualifiedByName = "fromLocalDate")
    @Mapping(target = "principal", source = "request.amount")
    @Mapping(target = "loanType", expression = "java(io.finto.fineract.sdk.Constants.INDIVIDUAL)")
    @Mapping(target = "loanTermFrequency", source = "request.numberOfInstallments")
    @Mapping(target = "loanTermFrequencyType", source = "loanProduct.repaymentFrequencyType.id")
    @Mapping(target = "numberOfRepayments", source = "request.numberOfInstallments")
    @Mapping(target = "repaymentEvery", source = "loanProduct.repaymentEvery")
    @Mapping(target = "repaymentFrequencyType", source = "loanProduct.repaymentFrequencyType.id")
    @Mapping(target = "interestRatePerPeriod", source = "loanProduct.interestRatePerPeriod")
    @Mapping(target = "amortizationType", source = "loanProduct.amortizationType.id")
    @Mapping(target = "interestType", source = "loanProduct.interestType.id")
    @Mapping(target = "interestCalculationPeriodType", source = "loanProduct.interestCalculationPeriodType.id")
    @Mapping(target = "transactionProcessingStrategyCode", source = "loanProduct.transactionProcessingStrategyCode")
    @Mapping(target = "graceOnPrincipalPayment", source = "loanProduct.graceOnPrincipalPayment")
    @Mapping(target = "graceOnInterestPayment", source = "loanProduct.graceOnInterestPayment")
    @Mapping(target = "charges", source = "loanProduct.charges", qualifiedByName = "toPostLoansChargeRequest")
    @Mapping(target = "datatables", source = "request", qualifiedByName = "toLoanDataTables")
    @Mapping(target = "daysInYearType", expression = "java(null)")
    PostLoansRequest loanCreationFineractRequest(Long internalCustomerId, LoanCreate request, GetLoanProductsProductIdResponse loanProduct);

    default String fromDataTablesToString(RunReportsResponse additionalDetails, String key) {
        Integer position = getPosition(additionalDetails, key);
        if (position == null || position < 0) return null;

        return (String) additionalDetails.getData().get(0).getRow().get(position);
    }

    default LocalDate fromDataTablesToLocalDate(RunReportsResponse additionalDetails, String key) {
        Integer position = getPosition(additionalDetails, key);
        if (position == null || position < 0) return null;

        var dateArray = (int[]) additionalDetails.getData().get(0).getRow().get(position);

        return dateArray == null ? null : LocalDate.of(dateArray[0], dateArray[1], dateArray[2]);
    }

    @Nullable
    private static Integer getPosition(RunReportsResponse additionalDetails, String key) {
        if (additionalDetails == null ||
                additionalDetails.getColumnHeaders() == null ||
                additionalDetails.getColumnHeaders().isEmpty() ||
                additionalDetails.getData() == null ||
                additionalDetails.getData().isEmpty() ||
                additionalDetails.getData().get(0).getRow() == null ||
                Objects.requireNonNull(additionalDetails.getData().get(0).getRow()).isEmpty()) {
            return null;
        }

        return additionalDetails.getColumnHeaders().stream().map(ResultsetColumnHeaderData::getColumnName)
                .collect(Collectors.toList())
                .indexOf(key);
    }

    @Named("toInstallmentStartDateForLoan")
    default LocalDate toInstallmentStartDateForLoan(List<GetLoansLoanIdRepaymentPeriod> value) {
        if (value == null) {
            return null;
        }
        return value.stream().map(GetLoansLoanIdRepaymentPeriod::getDueDate)
                .filter(Objects::nonNull)
                .min(LocalDate::compareTo).orElse(null);
    }

    @Named("toInstallmentEndDateForLoan")
    default LocalDate toInstallmentEndDateForLoan(List<GetLoansLoanIdRepaymentPeriod> value) {
        if (value == null) {
            return null;
        }
        return value.stream().map(GetLoansLoanIdRepaymentPeriod::getDueDate)
                .filter(Objects::nonNull)
                .max(LocalDate::compareTo).orElse(null);
    }

    @Named("toFirstInstallmentAmountForLoan")
    default BigDecimal toFirstInstallmentAmountForLoan(List<GetLoansLoanIdRepaymentPeriod> value) {
        if (value == null) {
            return null;
        }

        return value.stream().filter(item -> item.getPeriod() != null && item.getPeriod() == 1).findFirst()
                .map(GetLoansLoanIdRepaymentPeriod::getTotalInstallmentAmountForPeriod).orElse(null);
    }

    @Named("toSubSeqInstallmentAmountForLoan")
    default BigDecimal toSubSeqInstallmentAmountForLoan(List<GetLoansLoanIdRepaymentPeriod> value) {
        if (value == null || value.size() < 2) {
            return null;
        }

        return value.stream().filter(item -> item.getPeriod() != null && item.getPeriod() == 2).findFirst()
                .map(GetLoansLoanIdRepaymentPeriod::getTotalInstallmentAmountForPeriod).orElse(null);
    }

    @Named("toInternalCustomerId")
    default CustomerInternalId toInternalCustomerId(String value) {
        if (value == null) {
            return null;
        }

        return CustomerInternalId.of(value);
    }

    @Named("toInternalProductId")
    default LoanProductId toInternalProductId(String value) {
        if (value == null) {
            return null;
        }

        return LoanProductId.of(value);
    }

    @Mapping(target = "id", source = "loan.id")
    @Mapping(target = "status", source = "loan.status.value")
    @Mapping(target = "internalCustomerId", source = "loan.clientId", qualifiedByName = "toInternalCustomerId")
    @Mapping(target = "internalProductId", source = "loan.loanProductId", qualifiedByName = "toInternalProductId")
    @Mapping(target = "currencyCode", source = "loan.currency.code")
    @Mapping(target = "createdAt", source = "loan.timeline.submittedOnDate")
    @Mapping(target = "createdBy", source = "loan.timeline.submittedByUsername")
    @Mapping(target = "approvedAt", source = "loan.timeline.approvedOnDate")
    @Mapping(target = "approvedBy", source = "loan.timeline.approvedByUsername")
    @Mapping(target = "disbursedAt", source = "loan.timeline.actualDisbursementDate")
    @Mapping(target = "disbursedBy", source = "loan.timeline.disbursedByUsername")
    @Mapping(target = "closedAt", source = "loan.timeline.closedOnDate")
    @Mapping(target = "closedBy", source = "loan.timeline.closedByName")
    @Mapping(target = "updatedAt", expression = "java(fromDataTablesToLocalDate(additionalDetails, \"modified_at\"))")
    @Mapping(target = "updatedBy", expression = "java(fromDataTablesToString(additionalDetails, \"modified_by\"))")
    @Mapping(target = "requestId", expression = "java(fromDataTablesToString(additionalDetails, \"request_id\"))")
    @Mapping(target = "offerId", expression = "java(fromDataTablesToString(additionalDetails, \"offer_id\"))")
    @Mapping(target = "customerId", expression = "java(fromDataTablesToString(additionalDetails, \"customer_id\"))")
    @Mapping(target = "partnerId", expression = "java(fromDataTablesToString(additionalDetails, \"partner_id\"))")
    @Mapping(target = "partnerName", expression = "java(fromDataTablesToString(additionalDetails, \"partner_name\"))")
    @Mapping(target = "amount", source = "loan.principal")
    @Mapping(target = "numberOfInstallments", source = "loan.numberOfRepayments")
    @Mapping(target = "loanTermInDays", source = "loan.repaymentSchedule.loanTermInDays")
    @Mapping(target = "interestRate", source = "loan.interestRatePerPeriod")
    @Mapping(target = "apr", expression = "java(calculateApr(loan.getRepaymentSchedule().getTotalFeeChargesCharged()," +
            "loan.getRepaymentSchedule().getTotalPenaltyChargesCharged()," +
            "loan.getRepaymentSchedule().getTotalInterestCharged()," +
            "loan.getRepaymentSchedule().getTotalPrincipalDisbursed()," +
            "loan.getRepaymentSchedule().getLoanTermInDays()," +
            "digitsAfterDecimal))")
    @Mapping(target = "installmentFrequency", source = "loan.repaymentFrequencyType.id", qualifiedByName = "toInstallmentFrequency")
    @Mapping(target = "installmentStartDate", source = "loan.repaymentSchedule.periods", qualifiedByName = "toInstallmentStartDateForLoan")
    @Mapping(target = "installmentEndDate", source = "loan.repaymentSchedule.periods", qualifiedByName = "toInstallmentEndDateForLoan")
    @Mapping(target = "firstInstallmentAmount", source = "loan.repaymentSchedule.periods", qualifiedByName = "toFirstInstallmentAmountForLoan")
    @Mapping(target = "subSeqInstallmentAmount", source = "loan.repaymentSchedule.periods", qualifiedByName = "toSubSeqInstallmentAmountForLoan")
    @Mapping(target = "totalPrincipalDisbursed", source = "loan.repaymentSchedule.totalPrincipalDisbursed")
    @Mapping(target = "totalPrincipalExpected", source = "loan.repaymentSchedule.totalPrincipalExpected")
    @Mapping(target = "totalPrincipalPaid", source = "loan.repaymentSchedule.totalPrincipalPaid")
    @Mapping(target = "totalInterestCharged", source = "loan.repaymentSchedule.totalInterestCharged")
    @Mapping(target = "totalFeeChargesCharged", source = "loan.repaymentSchedule.totalFeeChargesCharged")
    @Mapping(target = "totalPenaltyChargesCharged", source = "loan.repaymentSchedule.totalPenaltyChargesCharged")
    @Mapping(target = "totalRepaymentExpected", source = "loan.repaymentSchedule.totalRepaymentExpected")
    @Mapping(target = "totalOutstanding", source = "loan.repaymentSchedule.totalOutstanding")
    @Mapping(target = "totalCredits", source = "loan.repaymentSchedule.totalCredits")
    @Mapping(target = "totalPaidInAdvance", source = "loan.repaymentSchedule.totalPaidInAdvance")
    @Mapping(target = "totalPaidLate", source = "loan.repaymentSchedule.totalPaidLate")
    @Mapping(target = "totalRepayment", source = "loan.repaymentSchedule.totalRepayment")
    @Mapping(target = "totalWaived", source = "loan.repaymentSchedule.totalWaived")
    @Mapping(target = "totalWrittenOff", source = "loan.repaymentSchedule.totalWrittenOff")
    @Mapping(target = "isInArrears", source = "loan.inArrears")
    @Mapping(target = "isNPA", source = "loan.isNPA")
    @Mapping(target = "periods", source = "loan.repaymentSchedule.periods")
    @Mapping(target = "transactions", source = "loan.transactions")
    Loan toDomain(GetLoansLoanIdResponse loan, RunReportsResponse additionalDetails, Integer digitsAfterDecimal);

    @Mapping(target = "isComplete", source = "complete")
    Period toPeriod(GetLoansLoanIdRepaymentPeriod source);

    @Mapping(target = "type", source = "type.value")
    @Mapping(target = "currency", source = "currency.code")
    @Mapping(target = "otherIncomePortion", source = "unrecognizedIncomePortion")
    @Mapping(target = "isReversed", source = "manuallyReversed")
    @Mapping(target = "reversalDate", source = "reversedOnDate")
    Transaction toTransaction(GetLoansLoanIdTransactions source);

    default AvailableLoanStatus toAvailableLoanStatus(Integer value) {
        if (value == null) {
            return null;
        }
        switch (value) {
            case 100:
                return AvailableLoanStatus.NEW;
            case 200:
                return AvailableLoanStatus.APPROVED;
            case 300:
                return AvailableLoanStatus.REJECTED;
            case 301:
                return AvailableLoanStatus.WITHDRAWN;
            case 302:
                return AvailableLoanStatus.ACTIVE;
            case 400:
                return AvailableLoanStatus.OVERDUE;
            case 500:
                return AvailableLoanStatus.NPA;
            case 600:
                return AvailableLoanStatus.CLOSED;
            case 601:
                return AvailableLoanStatus.WRITTENOFF;
            case 602:
                return AvailableLoanStatus.RESCHEDULED;
            case 700:
                return AvailableLoanStatus.OVERPAID;
            default:
                return null;
        }
    }
}