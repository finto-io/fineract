package io.finto.integration.fineract.domain;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class AccountId {

    @NonNull
    Long value;

    public static AccountId of(Long id) {
        return AccountId.builder().value(id).build();
    }

    public static AccountId of(String id) {
        return of(Long.parseLong(id));
    }

}
