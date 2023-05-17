package io.finto.integration.fineract.domain;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class BankName {

    @NonNull
    String value;

    public static BankName of(String name) {
        return BankName.builder().value(name).build();
    }

}
