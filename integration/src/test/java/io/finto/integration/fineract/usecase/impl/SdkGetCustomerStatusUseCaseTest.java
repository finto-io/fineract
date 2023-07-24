package io.finto.integration.fineract.usecase.impl;

import io.finto.domain.customer.CustomerId;
import io.finto.domain.customer.CustomerStatus;
import io.finto.fineract.sdk.api.ClientApi;
import io.finto.fineract.sdk.models.GetClientsClientIdResponse;
import io.finto.integration.fineract.converter.FineractCustomerMapper;
import io.finto.integration.fineract.usecase.impl.customer.SdkChangeCustomerStatusUseCase;
import io.finto.integration.fineract.usecase.impl.customer.SdkGetCustomerStatusUseCase;
import org.easymock.IMocksControl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Call;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;

class SdkGetCustomerStatusUseCaseTest {

    private IMocksControl control;
    private SdkFineractUseCaseContext context;
    private CustomerId customerId = CustomerId.of(10L);
    private FineractCustomerMapper customerMapper;

    private ClientApi clientApi;

    private SdkGetCustomerStatusUseCase useCase;

    @BeforeEach
    void setUp() {
        control = createStrictControl();
        context = control.createMock(SdkFineractUseCaseContext.class);
        customerMapper = control.createMock(FineractCustomerMapper.class);
        useCase = SdkGetCustomerStatusUseCase.builder()
                .customerMapper(customerMapper)
                .context(context)
                .build();

        clientApi = control.createMock(ClientApi.class);
    }


    /**
     * Method under test: {@link SdkChangeCustomerStatusUseCase#activateCustomer(CustomerId)}
     */
    @Test
    void test_getCustomerStatus_success() {
        Call<GetClientsClientIdResponse> executeCommandCall = control.createMock(Call.class);

        GetClientsClientIdResponse response = new GetClientsClientIdResponse();

        expect(context.clientApi()).andReturn(clientApi);
        expect(clientApi.retrieveOneClient(customerId.getValue(), false, "status")).andReturn(executeCommandCall);
        expect(context.getResponseBody(executeCommandCall)).andReturn(response);
        expect(customerMapper.toCustomerStatus(anyObject())).andReturn(CustomerStatus.PENDING);

        control.replay();

        CustomerStatus actual = useCase.getCustomerStatus(customerId);

        control.verify();

        Assertions.assertEquals(CustomerStatus.PENDING, actual);
    }
}