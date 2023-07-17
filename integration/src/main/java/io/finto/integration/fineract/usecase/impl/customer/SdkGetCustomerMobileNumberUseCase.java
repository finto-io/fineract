package io.finto.integration.fineract.usecase.impl.customer;

import io.finto.domain.customer.CustomerId;
import io.finto.domain.customer.CustomerMobileNumber;
import io.finto.fineract.sdk.models.GetClientsClientIdResponse;
import io.finto.integration.fineract.converter.FineractCustomerMapper;
import io.finto.integration.fineract.usecase.impl.SdkFineractUseCaseContext;
import io.finto.usecase.customer.GetCustomerMobileNumberUseCase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;

@AllArgsConstructor
@Builder
public class SdkGetCustomerMobileNumberUseCase implements GetCustomerMobileNumberUseCase {

    @NonNull
    private final SdkFineractUseCaseContext context;
    @NonNull
    private final FineractCustomerMapper customerMapper;

    @Override
    public CustomerMobileNumber getCustomerMobileNumber(CustomerId id) {
        GetClientsClientIdResponse responseFineract = context.getResponseBody(context.clientApi().retrieveOneClient(id.getValue(), false, "mobileNo"));
        return customerMapper.toCustomerMobileNumber(responseFineract.getMobileNo());
    }
}
