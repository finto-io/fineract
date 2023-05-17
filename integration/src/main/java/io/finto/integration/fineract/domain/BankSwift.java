package io.finto.integration.fineract.domain;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class BankSwift {

    @NonNull
    String value;

    public static BankSwift of(String name) {
        return BankSwift.builder().value(name).build();
    }

}
