package io.finto.integration.fineract.usecase.impl.customer.identifier;

import io.finto.domain.customer.CustomerId;
import io.finto.domain.customer.Details;
import io.finto.domain.customer.IdentifierType;
import io.finto.domain.customer.UdfName;
import io.finto.domain.customer.UpdatingCustomer;
import io.finto.fineract.sdk.models.GetClientsClientIdIdentifiersResponse;
import io.finto.integration.fineract.converter.FineractCustomerMapper;
import io.finto.integration.fineract.usecase.impl.SdkFineractUseCaseContext;
import io.finto.usecase.customer.FindKeyValueDictionaryUseCase;
import io.finto.usecase.customer.identifier.CreateCustomerIdentifierUseCase;
import io.finto.usecase.customer.identifier.UpdateCustomerIdentifiersUseCase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Builder
public class SdkUpdateCustomerIdentifierUseCase implements UpdateCustomerIdentifiersUseCase {

    @NonNull
    private final SdkFineractUseCaseContext context;
    @NonNull
    private final FineractCustomerMapper customerMapper;
    @NonNull
    private final CreateCustomerIdentifierUseCase identifierUseCase;
    @NonNull
    private final FindKeyValueDictionaryUseCase dictionaryUseCase;

    public static class SdkUpdateCustomerIdentifierUseCaseBuilder {
        private FineractCustomerMapper customerMapper = FineractCustomerMapper.INSTANCE;
    }

    @Override
    public void updateCustomerIdentifiers(UpdatingCustomer updatingCustomer) {
        var customerId = updatingCustomer.getCustomerId();
        var driverId = updatingCustomer.getUdfDetails().stream()
                .filter(x -> x.getName().equals(UdfName.DRIVER_ID.name()))
                .findFirst().orElse(Details.builder().build())
                .getValue();
        var identifiers = context.getResponseBody(context
                        .clientIdentifierApi()
                        .retrieveAllClientIdentifiers(customerId.getValue()))
                .stream()
                .collect(Collectors.toMap(x -> x.getDocumentType().getName(), x -> x));

        updateCustomerIdentifier(customerId, IdentifierType.NATION_ID.getValue(), updatingCustomer.getNationId(), identifiers);
        updateCustomerIdentifier(customerId, IdentifierType.PASSPORT.getValue(), updatingCustomer.getUidValue(), identifiers);
        updateCustomerIdentifier(customerId, IdentifierType.DRIVER_LICENSE.getValue(), driverId, identifiers);
        identifiers.forEach((key, value) ->
                context.getResponseBody(context.clientIdentifierApi().deleteClientIdentifier(customerId.getValue(), Long.valueOf(value.getId()))));
    }

    private void updateCustomerIdentifier(CustomerId customerId, String identifierKey, String identifierValue, Map<String, GetClientsClientIdIdentifiersResponse> oldIdentifiers) {
        var odlIdentifierResponse = oldIdentifiers.get(identifierKey);
        if (isNeedToCreateIdentifier(identifierValue, odlIdentifierResponse)) {
            identifierUseCase.createCustomerIdentifier(customerId, identifierKey, identifierValue);
        }
        if (!isNeedToRemoveOldIdentifier(identifierValue, odlIdentifierResponse)) {
            oldIdentifiers.remove(identifierKey);
        }
    }

    private boolean isNeedToCreateIdentifier(String identifierValue, GetClientsClientIdIdentifiersResponse odlIdentifierResponse) {
        return StringUtils.isNoneEmpty(identifierValue) &&
                (odlIdentifierResponse == null || !identifierValue.equals(odlIdentifierResponse.getDocumentKey()));
    }


    private boolean isNeedToRemoveOldIdentifier(String identifierValue, GetClientsClientIdIdentifiersResponse odlIdentifierResponse) {
        return odlIdentifierResponse != null && StringUtils.isNoneEmpty(odlIdentifierResponse.getDocumentKey())
                && !odlIdentifierResponse.getDocumentKey().equals(identifierValue);
    }

}
