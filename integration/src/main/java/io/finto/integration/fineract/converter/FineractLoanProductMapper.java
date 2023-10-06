package io.finto.integration.fineract.converter;

import io.finto.domain.charge.ChargeCreate;
import io.finto.domain.id.fineract.ChargeId;
import io.finto.domain.loanproduct.Fee;
import io.finto.domain.loanproduct.FeeCalcType;
import io.finto.domain.loanproduct.FeeCreate;
import io.finto.domain.loanproduct.FeeType;
import io.finto.domain.loanproduct.InterestType;
import io.finto.domain.loanproduct.LoanProduct;
import io.finto.domain.loanproduct.LoanProductCreate;
import io.finto.fineract.sdk.models.ChargeData;
import io.finto.fineract.sdk.models.GetLoanProductsProductIdResponse;
import io.finto.fineract.sdk.models.GetProductsCharges;
import io.finto.fineract.sdk.models.PostLoanProductsRequest;
import io.finto.integration.fineract.dto.LoanProductDetailsCreateDto;
import io.finto.integration.fineract.dto.LoanProductDetailsDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static io.finto.fineract.sdk.Constants.*;

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

    @Mapping(target = "numberOfRepayments", expression = "java(getNumberOfRepayments(request.getMinimumPeriod(), request.getMaximumPeriod(), request.getNumberOfRepayments()))")
    @Mapping(target = "minNumberOfRepayments", source = "request.minimumPeriod")
    @Mapping(target = "maxNumberOfRepayments", source = "request.maximumPeriod")
    @Mapping(target = "repaymentEvery", constant = "1")
    @Mapping(target = "repaymentFrequencyType", constant = "2")
    @Mapping(target = "interestRatePerPeriod", source = "request.interest")
    @Mapping(target = "interestRateFrequencyType", constant = "3")
    @Mapping(target = "interestType", source = "request.interestType", qualifiedByName = "toInterestType")
    @Mapping(target = "graceOnPrincipalPayment", source = "request.installmentGracePeriod")
    @Mapping(target = "graceOnInterestPayment", source = "request.installmentGracePeriod")
    @Mapping(target = "isInterestRecalculationEnabled", constant = "false")
    @Mapping(target = "fundSourceAccountId", expression = "java(request.getAccountingRule() == 1 ? null : io.finto.fineract.sdk.Constants.FUND_SOURCE_ID)")
    @Mapping(target = "loanPortfolioAccountId", expression = "java(request.getAccountingRule() == 1 ? null : io.finto.fineract.sdk.Constants.LOAN_PORTFOLIO_ID)")
    @Mapping(target = "receivableFeeAccountId", expression = "java(request.getAccountingRule() == 1 ? null : io.finto.fineract.sdk.Constants.FEES_RECEIVABLE_ID)")
    @Mapping(target = "receivableInterestAccountId", expression = "java(request.getAccountingRule() == 1 ? null : io.finto.fineract.sdk.Constants.INTEREST_RECEIVABLE_ID)")
    @Mapping(target = "receivablePenaltyAccountId", expression = "java(request.getAccountingRule() == 1 ? null : io.finto.fineract.sdk.Constants.PENALTIES_RECEIVABLE_ID)")
    @Mapping(target = "transfersInSuspenseAccountId", expression = "java(request.getAccountingRule() == 1 ? null : io.finto.fineract.sdk.Constants.TRANSFER_IN_SUSPENSE_ID)")
    @Mapping(target = "interestOnLoanAccountId", expression = "java(request.getAccountingRule() == 1 ? null : io.finto.fineract.sdk.Constants.INCOME_FROM_INTEREST_ID)")
    @Mapping(target = "incomeFromFeeAccountId", expression = "java(request.getAccountingRule() == 1 ? null : io.finto.fineract.sdk.Constants.INCOME_FROM_FEES_ID)")
    @Mapping(target = "incomeFromPenaltyAccountId", expression = "java(request.getAccountingRule() == 1 ? null : io.finto.fineract.sdk.Constants.INCOME_FROM_PENALTIES_ID)")
    @Mapping(target = "incomeFromRecoveryAccountId", expression = "java(request.getAccountingRule() == 1 ? null : io.finto.fineract.sdk.Constants.INCOME_FROM_PENALTIES_ID)")
    @Mapping(target = "writeOffAccountId", expression = "java(request.getAccountingRule() == 1 ? null : io.finto.fineract.sdk.Constants.LOSSES_WRITTEN_OFF_ID)")
    @Mapping(target = "overpaymentLiabilityAccountId", expression = "java(request.getAccountingRule() == 1 ? null : io.finto.fineract.sdk.Constants.OVER_PAYMENT_LIABILITY_ID)")
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
    @Mapping(target = "partnerName", source = "request.partnerName")
    @Mapping(target = "externalId", source = "request.externalId")
    @Mapping(target = "latePaymentBlockUser", source = "request.latePaymentBlockUser")
    @Mapping(target = "earlySettlementAllowed", source = "request.earlySettlementAllowed")
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
        return value.format(io.finto.fineract.sdk.Constants.DEFAULT_DATE_FORMATTER);
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
}