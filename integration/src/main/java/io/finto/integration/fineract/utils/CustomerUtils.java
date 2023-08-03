package io.finto.integration.fineract.utils;

import io.finto.domain.customer.Address;
import io.finto.domain.customer.Customer;

import java.util.Comparator;

public class CustomerUtils {

    public static CustomerUtils INSTANCE = new CustomerUtils();

    private Comparator<Customer> customerComparator;
    private Comparator<Address> addressComparator;

    public Comparator<Customer> getCustomerComparator() {
        if (customerComparator == null) {
            customerComparator = Comparator.comparing(Customer::getLegalForm)
                    .thenComparing(Customer::getFirstName)
                    .thenComparing(Customer::getMiddleName)
                    .thenComparing(Customer::getLastName)
                    .thenComparing(Customer::getIsStaff)
                    .thenComparing(Customer::getMobileNumber)
                    .thenComparing(Customer::getDateOfBirth)
                    .thenComparing(Customer::getSex);
        }
        return customerComparator;
    }

    public Comparator<Address> getAddressComparator() {
        if (addressComparator == null) {
            addressComparator = Comparator.comparing(Address::getAddressLine1)
                    .thenComparing(Address::getAddressLine2)
                    .thenComparing(Address::getAddressLine3)
                    .thenComparing(Address::getType)
                    .thenComparing(Address::getStreet)
                    .thenComparing(Address::getStateProvince)
                    .thenComparing(Address::getCountry)
                    .thenComparing(Address::getPostalCode);
        }
        return addressComparator;
    }

}
