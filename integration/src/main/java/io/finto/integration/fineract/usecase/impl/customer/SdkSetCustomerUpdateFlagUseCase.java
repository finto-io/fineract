package io.finto.integration.fineract.usecase.impl.customer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.finto.domain.customer.CustomerId;
import io.finto.domain.customer.CustomerUpdateFlag;
import io.finto.exceptions.core.FintoApiException;
import io.finto.integration.fineract.converter.ConverterUtils;
import io.finto.integration.fineract.converter.FineractCustomerMapper;
import io.finto.integration.fineract.dto.UpdateFlagRequest;
import io.finto.integration.fineract.dto.UpdateFlagResponse;
import io.finto.integration.fineract.usecase.impl.SdkFineractUseCaseContext;
import io.finto.usecase.customer.SetCustomerUpdateFlagUseCase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

import java.time.LocalDateTime;

import static io.finto.fineract.sdk.CustomDatatableNames.CUSTOMER_UPDATE_FLAG;

@AllArgsConstructor
@Builder
public class SdkSetCustomerUpdateFlagUseCase implements SetCustomerUpdateFlagUseCase {

    @NonNull
    private final SdkFineractUseCaseContext context;
    @NonNull
    private final FineractCustomerMapper customerMapper;

    @NonNull
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private final ObjectMapper objectMapper;

    public static class SdkSetCustomerUpdateFlagUseCaseBuilder {
        private FineractCustomerMapper customerMapper = FineractCustomerMapper.INSTANCE;
        private ObjectMapper objectMapper = JsonMapper.builder().findAndAddModules().build();
    }


    @Override
    public void setUpdateFlag(CustomerId customerId, boolean flag, String clientIp, LocalDateTime timestamp, LocalDateTime ttl) {
        try {
            context.getResponseBody(
                    context.dataTablesApi()
                            .updateDatatableEntryOnetoOne(
                                    CUSTOMER_UPDATE_FLAG,
                                    customerId.getValue(),
                                    objectMapper.writeValueAsString(customerMapper.toUpdateFlagRequestDto(customerId.getValue(), flag, clientIp, timestamp, ttl)))
            );
        } catch (JsonProcessingException e) {
            throw new FintoApiException(e);
        }
    }

    @Override
    public CustomerUpdateFlag getUpdateFlag(CustomerId customerId) {
        var additionalDetails = ConverterUtils.parseAdditionalFields(objectMapper, context.getResponseBody(context.dataTablesApi()
                .getDatatableByAppTableId(CUSTOMER_UPDATE_FLAG, customerId.getValue(), null)), UpdateFlagResponse[].class);
        return customerMapper.toCustomerUpdateFlagEntity(additionalDetails);
    }
}
