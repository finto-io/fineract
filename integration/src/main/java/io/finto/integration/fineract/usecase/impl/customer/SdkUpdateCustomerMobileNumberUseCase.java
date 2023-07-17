package io.finto.integration.fineract.usecase.impl.customer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.finto.domain.customer.CustomerId;
import io.finto.exceptions.core.FintoApiException;
import io.finto.integration.fineract.converter.FineractCustomerMapper;
import io.finto.integration.fineract.usecase.impl.SdkFineractUseCaseContext;
import io.finto.usecase.customer.UpdateCustomerMobileNumberUseCase;
import lombok.*;

import static io.finto.fineract.sdk.CustomDatatableNames.CUSTOMER_ADDITIONAL_FIELDS;

@AllArgsConstructor
@Builder
public class SdkUpdateCustomerMobileNumberUseCase implements UpdateCustomerMobileNumberUseCase {

    @NonNull
    private final SdkFineractUseCaseContext context;
    @NonNull
    private final FineractCustomerMapper customerMapper;
    @NonNull
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private final ObjectMapper objectMapper;

    public static class SdkUpdateCustomerMobileNumberUseCaseBuilder {
        private FineractCustomerMapper customerMapper = FineractCustomerMapper.INSTANCE;
        private ObjectMapper objectMapper = JsonMapper.builder().findAndAddModules().build();
    }

    @Override
    public void updateMobileNumber(CustomerId customerId, String newMobileNumber) {
        context.getResponseBody(context.clientApi().updateClient(customerId.getValue(),
                customerMapper.toUpdateMobileNumberRequest(newMobileNumber))
        );
        try {
            context.getResponseBody(
                    context.dataTablesApi().updateDatatableEntryOnetoOne(CUSTOMER_ADDITIONAL_FIELDS, customerId.getValue(),
                            objectMapper.writeValueAsString(customerMapper.toUpdateTimeRequest()))
            );
        } catch (JsonProcessingException e) {
            throw new FintoApiException(e);
        }
    }
}
