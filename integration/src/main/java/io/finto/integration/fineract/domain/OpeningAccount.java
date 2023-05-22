package io.finto.integration.fineract.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
public class OpeningAccount {
    @NonNull
    CustomerId customerId;
    @NonNull
    AccountType accountType;
    @NonNull
    CurrencyCode currencyCode;
}

