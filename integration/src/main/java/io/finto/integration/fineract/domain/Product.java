package io.finto.integration.fineract.domain;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class Product {

    @NonNull
    ProductId id;

    public static Product of(ProductId id) {
        return Product.builder().id(id).build();
    }

}
