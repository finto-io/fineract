package io.finto.integration.fineract.usecase.impl.customer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.finto.domain.customer.CustomerId;
import io.finto.domain.customer.CustomerStatus;
import io.finto.exceptions.core.generic.EntityNotFoundException;
import io.finto.exceptions.core.specific.CustomerNotFoundException;
import io.finto.integration.fineract.converter.FineractCustomerMapper;
import io.finto.integration.fineract.usecase.impl.SdkFineractUseCaseContext;
import io.finto.usecase.customer.GetCustomerStatusUseCase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

@AllArgsConstructor
@Builder
public class SdkGetCustomerStatusUseCase implements GetCustomerStatusUseCase {

    @NonNull
    private final SdkFineractUseCaseContext context;
    @NonNull
    private final FineractCustomerMapper customerMapper;
    @NonNull
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private final ObjectMapper objectMapper;

    public static class SdkGetCustomerStatusUseCaseBuilder {
        private FineractCustomerMapper customerMapper = FineractCustomerMapper.INSTANCE;
        private ObjectMapper objectMapper = JsonMapper.builder().findAndAddModules().build();

    }

    @Override
    public CustomerStatus getCustomerStatus(CustomerId customerId) {
        try {
            var client = context.getResponseBody(context.clientApi()
                    .retrieveOneClient(customerId.getValue(), false, "status"));
            return customerMapper.toCustomerStatus(client.getStatus());
        } catch (EntityNotFoundException e) {
            throw new CustomerNotFoundException(e);
        }
    }

}
