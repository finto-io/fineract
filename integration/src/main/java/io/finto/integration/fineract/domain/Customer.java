package io.finto.integration.fineract.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
public class Customer {
    String name;
    String fullName;
    String type;
}