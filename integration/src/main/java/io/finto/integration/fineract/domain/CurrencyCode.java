package io.finto.integration.fineract.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
public class CurrencyCode {

    @NonNull
    String value;

    public static CurrencyCode of(String type) {
        return CurrencyCode.builder().value(type).build();
    }
    
}

