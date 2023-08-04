package io.finto.integration.fineract.converter;

import io.finto.domain.customer.Address;
import io.finto.domain.customer.Profession;
import io.finto.domain.customer.UpdatingCustomer;
import io.finto.fineract.sdk.models.PostClientClientIdAddressesRequest;
import org.junit.jupiter.api.Test;

import static io.finto.fineract.sdk.Constants.RESIDENCE_ADDRESS_CODE_NAME;
import static io.finto.fineract.sdk.Constants.WORK_ADDRESS_CODE_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FineractAddressMapperTest {

    private final FineractAddressMapper mapper = FineractAddressMapper.INSTANCE;

    @Test
    void toResidenceAddressDomain() {
        var request = UpdatingCustomer.builder()
                .addressLine1("addressLine1")
                .addressLine2("addressLine2")
                .addressLine1("addressLine3")
                .city("city")
                .country("country")
                .postalCode("postalCode")
                .build();
        var actual = mapper.toResidenceAddressDomain(request);
        var expected = Address.builder()
                .type(RESIDENCE_ADDRESS_CODE_NAME)
                .addressLine1("addressLine1")
                .addressLine2("addressLine2")
                .addressLine1("addressLine3")
                .city("city")
                .country("country")
                .postalCode("postalCode")
                .build();
        assertEquals(expected, actual);
    }

    @Test
    void toResidenceAddressDomain_null() {
        var actual = mapper.toResidenceAddressDomain(null);
        var expected = Address.builder()
                .type(RESIDENCE_ADDRESS_CODE_NAME)
                .build();
        assertEquals(expected, actual);
    }


    @Test
    void toWorkAddressDomain() {
        var request = Profession.builder()
                .address1("addressLine1")
                .address2("addressLine2")
                .address3("addressLine3")
                .country("country")
                .build();
        var actual = mapper.toWorkAddressDomain(request);
        var expected = Address.builder()
                .type(WORK_ADDRESS_CODE_NAME)
                .addressLine1("addressLine1")
                .addressLine2("addressLine2")
                .addressLine3("addressLine3")
                .country("country")
                .build();
        assertEquals(expected, actual);
    }

    @Test
    void toWorkAddressDomain_null() {
        var actual = mapper.toWorkAddressDomain(null);
        var expected = Address.builder()
                .type(WORK_ADDRESS_CODE_NAME)
                .build();
        assertEquals(expected, actual);
    }


    @Test
    void toCreateAddressDto() {
        var countryId = 1L;
        var postalCodeId = 2L;
        var request = Address.builder()
                .addressLine1("addressLine1")
                .addressLine2("addressLine2")
                .addressLine1("addressLine3")
                .street("street")
                .city("city")
                .build();
        var actual = mapper.toCreateAddressDto(request, countryId, postalCodeId);
        var expected = PostClientClientIdAddressesRequest.builder()
                .addressLine1("addressLine1")
                .addressLine2("addressLine2")
                .addressLine1("addressLine3")
                .street("street")
                .city("city")
                .countryId(1)
                .postalCode(2L)
                .isActive(true)
                .build();
        assertEquals(expected, actual);
    }
}