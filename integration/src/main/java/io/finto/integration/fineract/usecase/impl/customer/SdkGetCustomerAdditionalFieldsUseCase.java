package io.finto.integration.fineract.usecase.impl.customer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.finto.domain.customer.CustomerAdditionalFields;
import io.finto.domain.customer.CustomerId;
import io.finto.integration.fineract.converter.ConverterUtils;
import io.finto.integration.fineract.converter.FineractCustomerMapper;
import io.finto.integration.fineract.dto.CustomerAdditionalFieldsDto;
import io.finto.integration.fineract.usecase.impl.SdkFineractUseCaseContext;
import io.finto.usecase.customer.GetCustomerAdditionalFieldsUseCase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

import static io.finto.fineract.sdk.CustomDatatableNames.CUSTOMER_ADDITIONAL_FIELDS;

@AllArgsConstructor
@Builder
public class SdkGetCustomerAdditionalFieldsUseCase implements GetCustomerAdditionalFieldsUseCase {

    @NonNull
    private final SdkFineractUseCaseContext context;
    @NonNull
    private final FineractCustomerMapper customerMapper;
    @NonNull
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private final ObjectMapper objectMapper;

    public static class SdkGetCustomerAdditionalFieldsUseCaseBuilder {
        private FineractCustomerMapper customerMapper = FineractCustomerMapper.INSTANCE;
        private ObjectMapper objectMapper = JsonMapper.builder().findAndAddModules().build();

    }

    @Override
    public CustomerAdditionalFields getCustomerAdditionalFields(CustomerId customerId) {
        var additionalDetails = ConverterUtils.parseAdditionalFields(objectMapper, context.getResponseBody(context.dataTablesApi()
                .getDatatableByAppTableId(CUSTOMER_ADDITIONAL_FIELDS, customerId.getValue(), null)), CustomerAdditionalFieldsDto[].class);
        return customerMapper.toAdditionalFields(additionalDetails);
    }

}
