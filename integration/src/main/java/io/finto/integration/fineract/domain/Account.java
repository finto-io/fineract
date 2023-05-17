package io.finto.integration.fineract.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
@AllArgsConstructor
public class Account {
    @NonNull
    AccountId id;
    @NonNull
    ProductId productId;
    String branch;
    String number;
    String type;
    String alternateNumber;
    String name;
    String iban;
    String swift;
    String bankName;
    Customer customer;
    String currencyCode;
    Boolean noDebit;
    Boolean noCredit;
    Boolean dormant;
    BigDecimal creditCurrentBalance;
    BigDecimal localCreditCurrentBalance;
    BigDecimal creditBlockedAmount;
    BigDecimal creditAvailableBalance;
    String externalAccountNumber;
    String externalAccountName;
}

