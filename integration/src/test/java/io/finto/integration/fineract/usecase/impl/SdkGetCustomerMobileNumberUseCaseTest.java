package io.finto.integration.fineract.usecase.impl;

import io.finto.domain.customer.CustomerId;
import io.finto.domain.customer.CustomerMobileNumber;
import io.finto.fineract.sdk.api.ClientApi;
import io.finto.fineract.sdk.models.GetClientsClientIdResponse;
import io.finto.integration.fineract.converter.FineractCustomerMapper;
import io.finto.integration.fineract.usecase.impl.customer.SdkGetCustomerMobileNumberUseCase;
import org.easymock.IMocksControl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Call;

import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SdkGetCustomerMobileNumberUseCaseTest {

    private SdkGetCustomerMobileNumberUseCase useCase;
    private SdkFineractUseCaseContext context;
    private FineractCustomerMapper customerMapper;
    private Call<GetClientsClientIdResponse> apiCall;
    private IMocksControl control;
    private ClientApi clientApi;

    @BeforeEach
    void setUp() {
        control = createStrictControl();
        context = control.createMock(SdkFineractUseCaseContext.class);
        customerMapper = control.createMock(FineractCustomerMapper.class);
        clientApi = control.createMock(ClientApi.class);
        apiCall = control.createMock(Call.class);

        useCase = SdkGetCustomerMobileNumberUseCase.builder()
                .context(context)
                .customerMapper(customerMapper)
                .build();
    }

    @Test
    void testGetCustomerMobileNumber() {
        CustomerId customerId = CustomerId.of("123");
        String responseMobileNo = "987654321";
        GetClientsClientIdResponse responseFineract = new GetClientsClientIdResponse();
        responseFineract.setMobileNo(responseMobileNo);
        CustomerMobileNumber expectedMobileNumber = CustomerMobileNumber.builder()
                .mobileNumber(responseMobileNo)
                .build();

        expect(context.clientApi()).andReturn(clientApi);
        expect(clientApi.retrieveOneClient(customerId.getValue(), false, "mobileNo")).andReturn(apiCall);
        expect(context.getResponseBody(apiCall)).andReturn(responseFineract);
        expect(customerMapper.toCustomerMobileNumber(responseMobileNo)).andReturn(expectedMobileNumber);

        control.replay();

        CustomerMobileNumber result = useCase.getCustomerMobileNumber(customerId);

        assertEquals(expectedMobileNumber, result);

        control.verify();
    }
}