package io.finto.integration.fineract.test.helpers.client;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder(toBuilder = true)
public class TestClientAddress {
    private Long addressTypeId;
    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    private String city;
    private Integer countryId;
    private Long postalCode;
    private Boolean isActive;
}
