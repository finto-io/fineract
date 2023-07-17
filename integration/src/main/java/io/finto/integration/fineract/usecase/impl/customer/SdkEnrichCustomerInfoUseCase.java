package io.finto.integration.fineract.usecase.impl.customer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.finto.domain.customer.CustomerAdditionalFields;
import io.finto.domain.customer.CustomerDetailsUpdate;
import io.finto.domain.customer.CustomerId;
import io.finto.exceptions.core.FintoApiException;
import io.finto.integration.fineract.converter.FineractCustomerMapper;
import io.finto.integration.fineract.usecase.impl.SdkFineractUseCaseContext;
import io.finto.usecase.customer.EnrichCustomerInfoUseCase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

import java.util.function.Function;

import static io.finto.fineract.sdk.CustomDatatableNames.CUSTOMER_ADDITIONAL_FIELDS;

@AllArgsConstructor
@Builder
public class SdkEnrichCustomerInfoUseCase implements EnrichCustomerInfoUseCase {

    @NonNull
    private final SdkFineractUseCaseContext context;
    @NonNull
    private final Function<CustomerId, CustomerAdditionalFields> getCustomerAdditionalFields;
    @NonNull
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private final ObjectMapper objectMapper;
    @NonNull
    private final FineractCustomerMapper customerMapper;

    public static class SdkEnrichCustomerInfoUseCaseBuilder {
        private FineractCustomerMapper customerMapper = FineractCustomerMapper.INSTANCE;
        private ObjectMapper objectMapper = JsonMapper.builder().findAndAddModules().build();
    }

    @Override
    public CustomerAdditionalFields saveAdditionalFields(CustomerId customerId,
                                                         CustomerDetailsUpdate request) {
        var dto = customerMapper.toCustomerDetailsUpdateDto(request);
        try {
            context.getResponseBody(
                    context.dataTablesApi()
                            .updateDatatableEntryOnetoOne(
                                    CUSTOMER_ADDITIONAL_FIELDS,
                                    customerId.getValue(),
                                    objectMapper.writeValueAsString(dto)
                            )
            );
        } catch (JsonProcessingException e) {
            throw new FintoApiException(e);
        }
        return getCustomerAdditionalFields.apply(customerId);
    }

}
