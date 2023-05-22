package io.finto.integration.fineract.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
public class AccountType {

    @NonNull
    String value;

    public static AccountType of(String type) {
        return AccountType.builder().value(type).build();
    }

}

