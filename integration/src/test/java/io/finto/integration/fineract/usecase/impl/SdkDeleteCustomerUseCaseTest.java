package io.finto.integration.fineract.usecase.impl;

import io.finto.domain.customer.CustomerId;
import io.finto.fineract.sdk.api.ClientApi;
import io.finto.fineract.sdk.models.DeleteClientsClientIdResponse;
import org.easymock.IMocksControl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Call;

import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class SdkDeleteCustomerUseCaseTest {

    private IMocksControl control;
    private SdkFineractUseCaseContext context;
    private final CustomerId customerId = CustomerId.of(18L);
    private SdkDeleteCustomerUseCase useCase;
    private ClientApi clientApi;

    @BeforeEach
    void setUp() {
        control = createStrictControl();
        context = control.createMock(SdkFineractUseCaseContext.class);
        useCase = SdkDeleteCustomerUseCase.builder().context(context).build();

        clientApi = control.createMock(ClientApi.class);
    }

    /**
     * Method under test: {@link SdkDeleteCustomerUseCase#deleteCustomer(CustomerId)}
     */
    @Test
    void test_deleteCustomer_success() {
        Call<DeleteClientsClientIdResponse> removingCall = control.createMock(Call.class);

        expect(context.clientApi()).andReturn(clientApi);
        expect(clientApi.deleteClient(customerId.getValue())).andReturn(removingCall);
        expect(context.getResponseBody(removingCall)).andReturn(null);

        control.replay();

        assertDoesNotThrow(() -> useCase.deleteCustomer(customerId));

        control.verify();

    }

}