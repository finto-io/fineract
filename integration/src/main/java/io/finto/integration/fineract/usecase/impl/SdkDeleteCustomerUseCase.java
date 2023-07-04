package io.finto.integration.fineract.usecase.impl;

import io.finto.domain.customer.CustomerId;
import io.finto.usecase.customer.DeleteCustomerUseCase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;

@AllArgsConstructor
@Builder
public class SdkDeleteCustomerUseCase implements DeleteCustomerUseCase {

    @NonNull
    private final SdkFineractUseCaseContext context;

    @Override
    public void deleteCustomer(CustomerId id) {
        context.getResponseBody(context.clientApi().deleteClient(id.getValue()));
    }

}
