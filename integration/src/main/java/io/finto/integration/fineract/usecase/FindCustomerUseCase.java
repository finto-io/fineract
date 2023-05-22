package io.finto.integration.fineract.usecase;

import io.finto.integration.fineract.domain.Customer;
import io.finto.integration.fineract.domain.CustomerId;

public interface FindCustomerUseCase {
    Customer findCustomer(CustomerId customerId);
}
