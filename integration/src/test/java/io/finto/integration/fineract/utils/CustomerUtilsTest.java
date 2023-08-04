package io.finto.integration.fineract.utils;

import io.finto.domain.customer.Address;
import io.finto.domain.customer.Customer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CustomerUtilsTest {

    private Customer generateCustomer(){
        return Customer.builder()
                .legalForm("legalForm")
                .firstName("firstName")
                .middleName("middleName")
                .lastName("lastName")
                .isStaff(true)
                .mobileNumber("mobileNumber")
                .dateOfBirth("dateOfBirth")
                .build();
    }

    private Address generateAddress(){
        return Address.builder()
                .addressLine1("addressLine1")
                .addressLine2("addressLine1")
                .addressLine3("addressLine1")
                .type("addressLine1")
                .street("street")
                .stateProvince("stateProvince")
                .country("country")
                .postalCode("postalCode")
                .build();
    }

    @Test
    void getCustomerComparator_nullValue() {
        var oldCustomer = generateCustomer();
        var newCustomer = Customer.builder().build();
        var comparator = CustomerUtils.INSTANCE.getCustomerComparator();
        assertTrue(comparator.compare(oldCustomer, newCustomer) != 0);
    }

    @Test
    void getCustomerComparator_newField() {
        var oldCustomer = generateCustomer();
        var newCustomer = generateCustomer();
        newCustomer.setFirstName("newName");

        var comparator = CustomerUtils.INSTANCE.getCustomerComparator();
        assertTrue(comparator.compare(oldCustomer, newCustomer) != 0);
    }

    @Test
    void getCustomerComparator_otherField() {
        var oldCustomer = generateCustomer();
        var newCustomer = generateCustomer();
        newCustomer.setBranch("newBranch");

        var comparator = CustomerUtils.INSTANCE.getCustomerComparator();
        assertTrue(comparator.compare(oldCustomer, newCustomer) == 0);
    }

    @Test
    void getCustomerComparator_theSame() {
        var oldCustomer = generateCustomer();
        var newCustomer = generateCustomer();
        var comparator = CustomerUtils.INSTANCE.getCustomerComparator();
        assertTrue(comparator.compare(oldCustomer, newCustomer) == 0);
    }

    @Test
    void getAddressComparator_nullValue() {
        var oldCustomer = generateAddress();
        var newCustomer = Address.builder().build();
        var comparator = CustomerUtils.INSTANCE.getAddressComparator();
        assertTrue(comparator.compare(oldCustomer, newCustomer) != 0);
    }

    @Test
    void getAddressComparator_newField() {
        var oldCustomer = generateAddress();
        var newCustomer = generateAddress();
        newCustomer.setAddressLine1("newLine1");

        var comparator = CustomerUtils.INSTANCE.getAddressComparator();
        assertTrue(comparator.compare(oldCustomer, newCustomer) != 0);
    }

    @Test
    void getAddressComparator_theSame() {
        var oldCustomer = generateAddress();
        var newCustomer = generateAddress();
        var comparator = CustomerUtils.INSTANCE.getAddressComparator();
        assertTrue(comparator.compare(oldCustomer, newCustomer) == 0);
    }
}