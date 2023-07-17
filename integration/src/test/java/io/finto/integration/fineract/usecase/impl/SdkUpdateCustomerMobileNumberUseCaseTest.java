package io.finto.integration.fineract.usecase.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.finto.fineract.sdk.api.ClientApi;
import io.finto.fineract.sdk.api.DataTablesApi;
import io.finto.fineract.sdk.models.PutClientsClientIdRequest;
import io.finto.fineract.sdk.models.PutClientsClientIdResponse;
import io.finto.fineract.sdk.models.PutDataTablesAppTableIdResponse;
import io.finto.integration.fineract.converter.FineractCustomerMapper;
import io.finto.integration.fineract.dto.CustomerAdditionalFieldsDto;
import io.finto.integration.fineract.usecase.impl.customer.SdkUpdateCustomerMobileNumberUseCase;
import org.easymock.IMocksControl;
import org.junit.jupiter.api.BeforeEach;
import retrofit2.Call;

import static org.easymock.EasyMock.createStrictControl;

class SdkUpdateCustomerMobileNumberUseCaseTest {

    private SdkUpdateCustomerMobileNumberUseCase useCase;
    private SdkFineractUseCaseContext context;
    private FineractCustomerMapper customerMapper;
    private ObjectMapper objectMapper;
    private PutClientsClientIdRequest request;
    private CustomerAdditionalFieldsDto request2;
    private Call<PutClientsClientIdResponse> apiCall;
    private Call<PutDataTablesAppTableIdResponse> apiCall2;
    private IMocksControl control;
    private ClientApi clientApi;
    private DataTablesApi dataTablesApi;

    @BeforeEach
    void setUp() {
        control = createStrictControl();
        context = control.createMock(SdkFineractUseCaseContext.class);
        customerMapper = control.createMock(FineractCustomerMapper.class);
        objectMapper = control.createMock(ObjectMapper.class);
        clientApi = control.createMock(ClientApi.class);
        dataTablesApi = control.createMock(DataTablesApi.class);
        apiCall = control.createMock(Call.class);
        apiCall2 = control.createMock(Call.class);
        request = control.createMock(PutClientsClientIdRequest.class);
        request2 = control.createMock(CustomerAdditionalFieldsDto.class);

        useCase = SdkUpdateCustomerMobileNumberUseCase.builder()
                .context(context)
                .customerMapper(customerMapper)
                .objectMapper(objectMapper)
                .build();
    }

//    @Test
//    void testGetCustomerMobileNumber() throws Exception{
//        var customerId = CustomerId.of("123");
//        var newMobileNumber = "newMobileNumber";
//        var stringRequest = "stringRequest";
//
//        expect(context.clientApi()).andReturn(clientApi);
//        expect(customerMapper.toUpdateMobileNumberRequest(newMobileNumber)).andReturn(request);
//        expect(clientApi.updateClient(customerId.getValue(), request)).andReturn(apiCall);
//        expect(context.getResponseBody(apiCall)).andReturn(null);
//
//        expect(context.dataTablesApi()).andReturn(dataTablesApi);
//        expect(customerMapper.toUpdateTimeRequest()).andReturn(request2);
//        expect(objectMapper.writeValueAsString(request2)).andReturn(stringRequest);
//        expect(dataTablesApi.updateDatatableEntryOnetoOne(CUSTOMER_ADDITIONAL_FIELDS, customerId.getValue(), stringRequest)).andReturn(apiCall2);
//        expect(context.getResponseBody(apiCall2)).andReturn(null);
//
//        control.replay();
//
//        useCase.updateMobileNumber(customerId, newMobileNumber);
//        control.verify();
//    }
}