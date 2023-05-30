package io.finto.integration.fineract.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;

@Value
@Builder
@AllArgsConstructor
public class Transaction {
    LocalDate date;
    LocalDate valueDate;
    String description;
    String debitCreditIndicator;
    BigDecimal transactionAmount;
    String transactionCurrency;
}

