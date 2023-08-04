package io.finto.integration.fineract.utils;

import io.finto.domain.customer.Address;
import io.finto.domain.customer.Customer;

import java.util.Comparator;

import static java.util.Comparator.comparing;

public class CustomerUtils {

    public static CustomerUtils INSTANCE = new CustomerUtils();

    private final Comparator<String> nullCheckString = Comparator.nullsFirst(Comparator.naturalOrder());
    private final Comparator<Boolean> nullCheckBoolean = Comparator.nullsFirst(Comparator.naturalOrder());
    private Comparator<Customer> customerComparator;
    private Comparator<Address> addressComparator;

    public Comparator<Customer> getCustomerComparator() {
        if (customerComparator == null) {
            customerComparator = comparing(Customer::getLegalForm, nullCheckString)
                    .thenComparing(Customer::getFirstName, nullCheckString)
                    .thenComparing(Customer::getMiddleName, nullCheckString)
                    .thenComparing(Customer::getLastName, nullCheckString)
                    .thenComparing(Customer::getIsStaff, nullCheckBoolean)
                    .thenComparing(Customer::getMobileNumber, nullCheckString)
                    .thenComparing(Customer::getDateOfBirth, nullCheckString)
                    .thenComparing(Customer::getSex, nullCheckString);
        }
        return customerComparator;
    }

    public Comparator<Address> getAddressComparator() {
        if (addressComparator == null) {
            addressComparator = comparing(Address::getAddressLine1, nullCheckString)
                    .thenComparing(Address::getAddressLine2, nullCheckString)
                    .thenComparing(Address::getAddressLine3, nullCheckString)
                    .thenComparing(Address::getType, nullCheckString)
                    .thenComparing(Address::getStreet, nullCheckString)
                    .thenComparing(Address::getStateProvince, nullCheckString)
                    .thenComparing(Address::getCountry, nullCheckString)
                    .thenComparing(Address::getPostalCode, nullCheckString);
        }
        return addressComparator;
    }

}
