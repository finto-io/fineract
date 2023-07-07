package io.finto.integration.fineract.converter;

import io.finto.domain.customer.CustomerMobileNumber;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FineractCustomerMapperTest {

    private final FineractCustomerMapper mapper = FineractCustomerMapper.INSTANCE;

    @Test
    void testToCustomerMobileNumber_WithNonNullMobileNo() {
        String mobileNo = "123456789";
        CustomerMobileNumber expected = CustomerMobileNumber.builder().mobileNumber(mobileNo).build();

        CustomerMobileNumber result = mapper.toCustomerMobileNumber(mobileNo);

        assertEquals(expected, result);
    }

    @Test
    void testToCustomerMobileNumber_WithNullMobileNo() {
        String mobileNo = null;
        CustomerMobileNumber expected = CustomerMobileNumber.builder().mobileNumber("").build();

        CustomerMobileNumber result = mapper.toCustomerMobileNumber(mobileNo);

        assertEquals(expected, result);
    }

}