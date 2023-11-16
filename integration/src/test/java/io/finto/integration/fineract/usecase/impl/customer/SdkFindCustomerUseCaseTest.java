package io.finto.integration.fineract.usecase.impl.customer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.finto.domain.customer.Customer;
import io.finto.domain.customer.CustomerId;
import io.finto.fineract.sdk.api.ClientApi;
import io.finto.fineract.sdk.api.ClientIdentifierApi;
import io.finto.fineract.sdk.api.ClientsAddressApi;
import io.finto.fineract.sdk.api.DataTablesApi;
import io.finto.fineract.sdk.models.GetClientClientIdAddressesResponse;
import io.finto.fineract.sdk.models.GetClientsClientIdIdentifiersResponse;
import io.finto.fineract.sdk.models.GetClientsClientIdResponse;
import io.finto.integration.fineract.converter.FineractCustomerMapper;
import io.finto.integration.fineract.dto.CustomerAdditionalFieldsDto;
import io.finto.integration.fineract.usecase.impl.SdkFineractUseCaseContext;
import org.easymock.IMocksControl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Call;

import java.util.List;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.anyString;
import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SdkFindCustomerUseCaseTest {

    private IMocksControl control;
    private SdkFineractUseCaseContext context;
    private FineractCustomerMapper customerMapper;
    private ObjectMapper objectMapper;
    private SdkFindCustomerUseCase useCase;

    @BeforeEach
    void setUp() {
        control = createStrictControl();
        context = control.createMock(SdkFineractUseCaseContext.class);
        customerMapper = control.createMock(FineractCustomerMapper.class);
        objectMapper = control.createMock(ObjectMapper.class);
        useCase = SdkFindCustomerUseCase.builder()
                .context(context)
                .customerMapper(customerMapper)
                .objectMapper(objectMapper)
                .build();
    }


    /**
     * Method under test: {@link SdkFindCustomerUseCase#findCustomer(CustomerId)}
     */
    @Test
    void test_findCustomer() throws JsonProcessingException {
        CustomerId customerId = CustomerId.of(13L);
        // client
        ClientApi clientApiMock = control.createMock(ClientApi.class);
        expect(context.clientApi())
                .andReturn(clientApiMock);
        Call<GetClientsClientIdResponse> clientCallMock = control.createMock(Call.class);
        expect(clientApiMock.retrieveOneClient(customerId.getValue(), false, null))
                .andReturn(clientCallMock);
        GetClientsClientIdResponse clientResponse = control.createMock(GetClientsClientIdResponse.class);
        expect(context.getResponseBody(clientCallMock))
                .andReturn(clientResponse);

        // address
        ClientsAddressApi clientsAddressApiMock = control.createMock(ClientsAddressApi.class);
        expect(context.clientsAddressApi())
                .andReturn(clientsAddressApiMock);
        Call<List<GetClientClientIdAddressesResponse>> clientAddressCallMock = control.createMock(Call.class);
        expect(clientsAddressApiMock.getClientAddresses(customerId.getValue(), null, null))
                .andReturn(clientAddressCallMock);
        List<GetClientClientIdAddressesResponse> clientAddressResponse = control.createMock(List.class);
        expect(context.getResponseBody(clientAddressCallMock))
                .andReturn(clientAddressResponse);

        //identifiers
        ClientIdentifierApi clientsIdentifiersApiMock = control.createMock(ClientIdentifierApi.class);
        expect(context.clientIdentifierApi())
                .andReturn(clientsIdentifiersApiMock);
        Call<List<GetClientsClientIdIdentifiersResponse>> clientIdentifiersCallMock = control.createMock(Call.class);
        expect(clientsIdentifiersApiMock.retrieveAllClientIdentifiers(customerId.getValue()))
                .andReturn(clientIdentifiersCallMock);
        List<GetClientsClientIdIdentifiersResponse> clientIdentifiersResponse = control.createMock(List.class);
        expect(context.getResponseBody(clientIdentifiersCallMock))
                .andReturn(clientIdentifiersResponse);

        //additional details
        DataTablesApi datatablesApiMock = control.createMock(DataTablesApi.class);
        expect(context.dataTablesApi())
                .andReturn(datatablesApiMock);
        Call<String> datatablesApiCallMock = control.createMock(Call.class);
        expect(datatablesApiMock.getDatatableByAppTableId("customer_fields", customerId.getValue(), null, null))
                .andReturn(datatablesApiCallMock);
        String datatablesApiResponse = "";
        expect(context.getResponseBody(datatablesApiCallMock))
                .andReturn(datatablesApiResponse);
        expect(objectMapper.readValue(anyString(), anyObject(Class.class)))
                .andReturn(new CustomerAdditionalFieldsDto[0]);
        Customer expected = Customer.builder().build();
        expect(customerMapper.toDomain(clientResponse, clientAddressResponse, clientIdentifiersResponse, null))
                .andReturn(expected);


        control.replay();
        var actual = useCase.findCustomer(customerId);
        control.verify();

        assertEquals(expected, actual);


    }
}