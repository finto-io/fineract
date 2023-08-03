package io.finto.integration.fineract.usecase.impl.customer.identifier;

import io.finto.domain.customer.CustomerId;
import io.finto.domain.customer.IdentifierId;
import io.finto.domain.customer.IdentifierType;
import io.finto.integration.fineract.converter.FineractCustomerMapper;
import io.finto.integration.fineract.usecase.impl.SdkFineractUseCaseContext;
import io.finto.usecase.customer.FindKeyValueDictionaryUseCase;
import io.finto.usecase.customer.identifier.CreateCustomerIdentifierUseCase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;

import static io.finto.fineract.sdk.Constants.DOCUMENT_TYPE_DICTIONARY_ID;

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
        return createCustomerIdentifier(customerId, IdentifierType.PASSPORT.name(), documentValue);
    }

    @Override
    public IdentifierId createNationIdIdentifier(CustomerId customerId, String documentValue) {
        return createCustomerIdentifier(customerId, IdentifierType.NATION_ID.name(), documentValue);
    }

    @Override
    public IdentifierId createDriverIdIdentifier(CustomerId customerId, String documentValue) {
        return createCustomerIdentifier(customerId, IdentifierType.DRIVER_LICENSE.name(), documentValue);
    }

}
