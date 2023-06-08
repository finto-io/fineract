package io.finto.integration.fineract.converter;

import io.finto.domain.transaction.Transaction;
import io.finto.domain.transaction.TransactionsStatus;
import io.finto.fineract.sdk.models.GetSavingsAccountsAccountIdTransactionsResponse;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface FineractTransactionMapper {

    FineractTransactionMapper INSTANCE = Mappers.getMapper(FineractTransactionMapper.class);

    @ValueMapping(source = "BLOCKED", target = "block")
    @ValueMapping(source = "UNBLOCKED", target = "unblock")
    String mapStatusToCommand(TransactionsStatus status);

    @Mapping(target = "date", source = "date", qualifiedByName = "mapIntegerListToDate")
    @Mapping(target = "valueDate", source = "date", qualifiedByName = "mapIntegerListToDate")
    @Mapping(target = "description", source = "transactionType.value")
    @Mapping(target = "debitCreditIndicator", source = "transactionType.code", qualifiedByName = "mapTransactionTypeCode")
    @Mapping(target = "transactionAmount", source = "amount")
    @Mapping(target = "transactionCurrency", source = "currency.code")
    Transaction toTransaction(GetSavingsAccountsAccountIdTransactionsResponse transaction);

    @Named("mapIntegerListToDate")
    default LocalDate mapIntegerListToDate(List<Integer> date) {
        return LocalDate.of(date.get(0), date.get(1), date.get(2));
    }

    @Named("mapTransactionTypeCode")
    default String mapTransactionTypeCode(String transactionTypeCode) {
        if ("savingsAccountTransactionType.withdrawal".equals(transactionTypeCode)
                || "savingsAccountTransactionType.feeDeduction".equals(transactionTypeCode)
                || "savingsAccountTransactionType.overdraftInterest".equals(transactionTypeCode)
                || "savingsAccountTransactionType.withholdTax".equals(transactionTypeCode)) {
            return "D";
        } else {
            return "C";
        }
    }


}


