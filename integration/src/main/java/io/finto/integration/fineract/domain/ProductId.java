package io.finto.integration.fineract.domain;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class ProductId {


    @NonNull
    Long value;

    public static ProductId of(Long id) {
        return ProductId.builder().value(id).build();
    }

    public static ProductId of(Integer id) {
        return of(Long.valueOf(id));
    }

    public static ProductId of(String id) {
        return of(Long.parseLong(id));
    }

}
