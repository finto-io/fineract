package io.finto.integration.fineract.usecase.impl.customer.identifier;

import io.finto.domain.customer.CustomerId;
import io.finto.domain.customer.IdentifierId;
import io.finto.domain.customer.IdentifierType;
import io.finto.fineract.sdk.api.ClientIdentifierApi;
import io.finto.fineract.sdk.models.PostClientsClientIdIdentifiersRequest;
import io.finto.fineract.sdk.models.PostClientsClientIdIdentifiersResponse;
import io.finto.integration.fineract.converter.FineractCustomerMapper;
import io.finto.integration.fineract.usecase.impl.SdkFineractUseCaseContext;
import io.finto.usecase.customer.FindKeyValueDictionaryUseCase;
import org.easymock.IMocksControl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Call;

import static io.finto.fineract.sdk.Constants.*;
import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SdkCreateCustomerIdentifierUseCaseTest {

    private IMocksControl control;
    private SdkFineractUseCaseContext context;
    private FineractCustomerMapper customerMapper;
    private SdkCreateCustomerIdentifierUseCase useCase;
    private FindKeyValueDictionaryUseCase dictionaryUseCase;
    private CustomerId customerId = CustomerId.of(10L);
    private ClientIdentifierApi clientIdentifierApi;
    private Call call;
    private IdentifierId identifierId;
    Long documentTypeId = 1L;
    Integer resourceId = 2;
    PostClientsClientIdIdentifiersRequest request;
    PostClientsClientIdIdentifiersResponse response;

    @BeforeEach
    void setUp() {
        control = createStrictControl();
        context = control.createMock(SdkFineractUseCaseContext.class);
        clientIdentifierApi = control.createMock(ClientIdentifierApi.class);
        dictionaryUseCase = control.createMock(FindKeyValueDictionaryUseCase.class);
        customerMapper = control.createMock(FineractCustomerMapper.class);
        useCase = new SdkCreateCustomerIdentifierUseCase(context, customerMapper, dictionaryUseCase);

        call = control.createMock(Call.class);
        identifierId = control.createMock(IdentifierId.class);
        request = control.createMock(PostClientsClientIdIdentifiersRequest.class);
        response = control.createMock(PostClientsClientIdIdentifiersResponse.class);
    }


    @Test
    void createCustomerIdentifier() {
        expect(dictionaryUseCase.getOneKeyByValue(DOCUMENT_TYPE_DICTIONARY_ID, "documentType")).andReturn(documentTypeId);
        expect(customerMapper.toIdentifierRequestDto(documentTypeId, "documentValue")).andReturn(request);
        expect(context.clientIdentifierApi()).andReturn(clientIdentifierApi);
        expect(clientIdentifierApi.createClientIdentifier(customerId.getValue(), request)).andReturn(call);
        expect(context.getResponseBody(call)).andReturn(response);
        expect(response.getResourceId()).andReturn(resourceId);
        control.replay();

        var actual = useCase.createCustomerIdentifier(customerId, "documentType", "documentValue");
        control.verify();

        assertEquals(IdentifierId.of(resourceId), actual);
    }

    @Test
    void createPassportIdentifier() {
        expect(dictionaryUseCase.getOneKeyByValue(DOCUMENT_TYPE_DICTIONARY_ID, IdentifierType.PASSPORT.getValue())).andReturn(documentTypeId);
        expect(customerMapper.toIdentifierRequestDto(documentTypeId, "documentValue")).andReturn(request);
        expect(context.clientIdentifierApi()).andReturn(clientIdentifierApi);
        expect(clientIdentifierApi.createClientIdentifier(customerId.getValue(), request)).andReturn(call);
        expect(context.getResponseBody(call)).andReturn(response);
        expect(response.getResourceId()).andReturn(resourceId);
        control.replay();

        var actual = useCase.createPassportIdentifier(customerId, "documentValue");
        control.verify();

        assertEquals(IdentifierId.of(resourceId), actual);
    }

    @Test
    void createNationIdIdentifier() {
        expect(dictionaryUseCase.getOneKeyByValue(DOCUMENT_TYPE_DICTIONARY_ID, IdentifierType.NATION_ID.getValue())).andReturn(documentTypeId);
        expect(customerMapper.toIdentifierRequestDto(documentTypeId, "documentValue")).andReturn(request);
        expect(context.clientIdentifierApi()).andReturn(clientIdentifierApi);
        expect(clientIdentifierApi.createClientIdentifier(customerId.getValue(), request)).andReturn(call);
        expect(context.getResponseBody(call)).andReturn(response);
        expect(response.getResourceId()).andReturn(resourceId);
        control.replay();

        var actual = useCase.createNationIdIdentifier(customerId, "documentValue");
        control.verify();

        assertEquals(IdentifierId.of(resourceId), actual);
    }

    @Test
    void createDriverIdIdentifier() {
        expect(dictionaryUseCase.getOneKeyByValue(DOCUMENT_TYPE_DICTIONARY_ID, IdentifierType.DRIVER_LICENSE.getValue())).andReturn(documentTypeId);
        expect(customerMapper.toIdentifierRequestDto(documentTypeId, "documentValue")).andReturn(request);
        expect(context.clientIdentifierApi()).andReturn(clientIdentifierApi);
        expect(clientIdentifierApi.createClientIdentifier(customerId.getValue(), request)).andReturn(call);
        expect(context.getResponseBody(call)).andReturn(response);
        expect(response.getResourceId()).andReturn(resourceId);
        control.replay();

        var actual = useCase.createDriverIdIdentifier(customerId, "documentValue");
        control.verify();

        assertEquals(IdentifierId.of(resourceId), actual);
    }
}