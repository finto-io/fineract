package io.finto.integration.fineract.usecase.impl.customer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.finto.domain.customer.Customer;
import io.finto.domain.customer.CustomerId;
import io.finto.exceptions.core.FintoApiException;
import io.finto.integration.fineract.converter.FineractCustomerMapper;
import io.finto.integration.fineract.dto.CustomerAdditionalFieldsDto;
import io.finto.integration.fineract.usecase.impl.SdkFineractUseCaseContext;
import io.finto.usecase.customer.FindCustomerUseCase;
import lombok.*;

import static io.finto.fineract.sdk.CustomDatatableNames.CUSTOMER_ADDITIONAL_FIELDS;

@AllArgsConstructor
@Builder
public class SdkFindCustomerUseCase implements FindCustomerUseCase {

    @NonNull
    private final SdkFineractUseCaseContext context;
    @NonNull
    private final FineractCustomerMapper customerMapper;
    @NonNull
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private final ObjectMapper objectMapper;

    public static class SdkFindCustomerUseCaseBuilder {
        private FineractCustomerMapper customerMapper = FineractCustomerMapper.INSTANCE;
        private ObjectMapper objectMapper = JsonMapper.builder().findAndAddModules().build();

    }

    @Override
    public Customer findCustomer(CustomerId customerId) {
        var client = context.getResponseBody(context.clientApi()
                .retrieveOneClient(customerId.getValue(), false, null));
        var addresses = context.getResponseBody(context.clientsAddressApi()
                .getClientAddresses(customerId.getValue(), null, null));
        var identifiers = context.getResponseBody(context.clientIdentifierApi().
                retrieveAllClientIdentifiers(customerId.getValue()));
        var additionalDetails = parseAdditionalFields(context.getResponseBody(context.dataTablesApi()
                .getDatatableByAppTableId(CUSTOMER_ADDITIONAL_FIELDS, customerId.getValue(), null)));
        return customerMapper.toDomain(client, addresses, identifiers, additionalDetails);
    }

    private CustomerAdditionalFieldsDto parseAdditionalFields(String content) {
        try {
            CustomerAdditionalFieldsDto[] customerAdditionalFieldsDtos = objectMapper.readValue(content, CustomerAdditionalFieldsDto[].class);
            return customerAdditionalFieldsDtos.length > 0 ? customerAdditionalFieldsDtos[0] : null;
        } catch (JsonProcessingException e) {
            throw new FintoApiException(e);
        }
    }

}
