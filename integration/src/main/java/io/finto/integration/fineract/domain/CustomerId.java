package io.finto.integration.fineract.domain;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class CustomerId {


    @NonNull
    Long value;

    public static CustomerId of(Long id) {
        return CustomerId.builder().value(id).build();
    }

    public static CustomerId of(String id) {
        return of(Long.parseLong(id));
    }

}
