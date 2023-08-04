package io.finto.integration.fineract.usecase.impl.customer.identifier;

import io.finto.domain.customer.CustomerId;
import io.finto.domain.customer.Details;
import io.finto.domain.customer.IdentifierType;
import io.finto.domain.customer.UdfName;
import io.finto.domain.customer.UpdatingCustomer;
import io.finto.fineract.sdk.api.ClientIdentifierApi;
import io.finto.fineract.sdk.models.GetClientsClientIdIdentifiersResponse;
import io.finto.fineract.sdk.models.GetClientsDocumentType;
import io.finto.integration.fineract.usecase.impl.SdkFineractUseCaseContext;
import io.finto.usecase.customer.FindKeyValueDictionaryUseCase;
import io.finto.usecase.customer.identifier.CreateCustomerIdentifierUseCase;
import org.easymock.IMocksControl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Call;

import java.util.Collections;
import java.util.List;

import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;

public class SdkUpdateCustomerIdentifierUseCaseTest {

    private IMocksControl control;
    private SdkFineractUseCaseContext context;
    private SdkUpdateCustomerIdentifierUseCase useCase;
    private CreateCustomerIdentifierUseCase identifierUseCase;
    private FindKeyValueDictionaryUseCase dictionaryUseCase;
    private CustomerId customerId = CustomerId.of(10L);

    private ClientIdentifierApi clientIdentifierApi;
    private Call call;

    @BeforeEach
    void setUp() {
        control = createStrictControl();
        context = control.createMock(SdkFineractUseCaseContext.class);
        clientIdentifierApi = control.createMock(ClientIdentifierApi.class);
        identifierUseCase = control.createMock(CreateCustomerIdentifierUseCase.class);
        dictionaryUseCase = control.createMock(FindKeyValueDictionaryUseCase.class);
        useCase = SdkUpdateCustomerIdentifierUseCase.builder()
                .context(context)
                .identifierUseCase(identifierUseCase)
                .dictionaryUseCase(dictionaryUseCase)
                .build();

        call = control.createMock(Call.class);
    }

    private GetClientsClientIdIdentifiersResponse createIdentifier(Integer id, String key, String value) {
        return GetClientsClientIdIdentifiersResponse.builder()
                .id(id)
                .documentKey(value)
                .documentType(GetClientsDocumentType.builder()
                        .name(key)
                        .build())
                .build();
    }

    private UpdatingCustomer getUpdatingCustomer(String nationId, String passport, String driverId) {
        return UpdatingCustomer.builder()
                .customerId(customerId)
                .nationId(nationId)
                .uidValue(passport)
                .udfDetails(List.of(Details.builder().name(UdfName.DRIVER_ID.name()).value(driverId).build()))
                .build();
    }

    @Test
    void updateCustomerIdentifiers_AddNewIdentifierAndDeleteOldIdentifier() {
        var oldIdentifiers = List.of(createIdentifier(1, IdentifierType.NATION_ID.getValue(), "old_nation_id"));

        expect(context.clientIdentifierApi()).andStubReturn(clientIdentifierApi);
        expect(clientIdentifierApi.retrieveAllClientIdentifiers(customerId.getValue())).andReturn(call);
        expect(context.getResponseBody(call)).andReturn(oldIdentifiers);
        expect(identifierUseCase.createCustomerIdentifier(customerId, IdentifierType.NATION_ID.getValue(), "new_nation_id")).andReturn(null);
        expect(clientIdentifierApi.deleteClientIdentifier(customerId.getValue(), 1L)).andReturn(call);
        expect(context.getResponseBody(call)).andReturn(null);
        control.replay();

        useCase.updateCustomerIdentifiers(getUpdatingCustomer("new_nation_id", null, null));
        control.verify();

    }


    @Test
    void updateCustomerIdentifiers_theSameIdentifier() {
        var oldIdentifiers = List.of(createIdentifier(1, IdentifierType.NATION_ID.getValue(), "old_nation_id"),
                createIdentifier(2, IdentifierType.PASSPORT.getValue(), "old_passport"));

        expect(context.clientIdentifierApi()).andStubReturn(clientIdentifierApi);
        expect(clientIdentifierApi.retrieveAllClientIdentifiers(customerId.getValue())).andReturn(call);
        expect(context.getResponseBody(call)).andReturn(oldIdentifiers);
        expect(identifierUseCase.createCustomerIdentifier(customerId, IdentifierType.DRIVER_LICENSE.getValue(), "new_driver_id")).andReturn(null);
        expect(clientIdentifierApi.deleteClientIdentifier(customerId.getValue(), 2L)).andReturn(call);
        expect(context.getResponseBody(call)).andReturn(null);
        control.replay();

        useCase.updateCustomerIdentifiers(getUpdatingCustomer("old_nation_id", null, "new_driver_id"));
        control.verify();

    }

    @Test
    void updateCustomerIdentifiers_addAll() {
        var oldIdentifiers = Collections.emptyList();
        expect(context.clientIdentifierApi()).andStubReturn(clientIdentifierApi);
        expect(clientIdentifierApi.retrieveAllClientIdentifiers(customerId.getValue())).andReturn(call);
        expect(context.getResponseBody(call)).andReturn(oldIdentifiers);
        expect(identifierUseCase.createCustomerIdentifier(customerId, IdentifierType.NATION_ID.getValue(), "new_nation_id")).andReturn(null);
        expect(identifierUseCase.createCustomerIdentifier(customerId, IdentifierType.PASSPORT.getValue(), "new_passport")).andReturn(null);
        expect(identifierUseCase.createCustomerIdentifier(customerId, IdentifierType.DRIVER_LICENSE.getValue(), "new_driver_id")).andReturn(null);

        control.replay();

        useCase.updateCustomerIdentifiers(getUpdatingCustomer("new_nation_id", "new_passport", "new_driver_id"));
        control.verify();

    }

}
