package io.finto.integration.fineract.usecase.impl.customer;

import io.finto.domain.customer.CustomerId;
import io.finto.domain.customer.IdentifierId;
import io.finto.integration.fineract.converter.FineractCustomerMapper;
import io.finto.integration.fineract.usecase.impl.SdkFineractUseCaseContext;
import io.finto.usecase.customer.CreateCustomerIdentifierUseCase;
import io.finto.usecase.customer.FindKeyValueDictionaryUseCase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;

import static io.finto.fineract.sdk.Constants.*;

@AllArgsConstructor
@Builder
public class SdkCreateCustomerIdentifierUseCase implements CreateCustomerIdentifierUseCase {

    @NonNull
    private final SdkFineractUseCaseContext context;
    @NonNull
    private final FineractCustomerMapper customerMapper;
    @NonNull
    private final FindKeyValueDictionaryUseCase dictionaryUseCase;

    public static class SdkCreateCustomerIdentifierUseCaseBuilder {
        private FineractCustomerMapper customerMapper = FineractCustomerMapper.INSTANCE;
    }

    @Override
    public IdentifierId createCustomerIdentifier(CustomerId customerId, String documentType, String documentValue) {
        var documentTypeId = dictionaryUseCase.getOneKeyByValue(DOCUMENT_TYPE_DICTIONARY_ID, documentType);
        var request = customerMapper.toIdentifierRequestDto(documentTypeId, documentValue);
        var call = context.clientIdentifierApi().createClientIdentifier(customerId.getValue(), request);
        return IdentifierId.of(context.getResponseBody(call).getResourceId());
    }

    @Override
    public IdentifierId createPassportIdentifier(CustomerId customerId, String documentValue) {
        return createCustomerIdentifier(customerId, PASSPORT_CODE_NAME, documentValue);
    }

    @Override
    public IdentifierId createNationIdIdentifier(CustomerId customerId, String documentValue) {
        return createCustomerIdentifier(customerId, NATION_ID_CODE_NAME, documentValue);
    }

    @Override
    public IdentifierId createDriverIdIdentifier(CustomerId customerId, String documentValue) {
        return createCustomerIdentifier(customerId, DRIVER_ID_CODE_NAME, documentValue);
    }

}
