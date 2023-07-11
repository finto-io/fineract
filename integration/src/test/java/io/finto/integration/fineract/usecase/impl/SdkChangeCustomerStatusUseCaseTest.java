package io.finto.integration.fineract.usecase.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.finto.domain.customer.CustomerId;
import io.finto.fineract.sdk.api.ClientApi;
import io.finto.fineract.sdk.models.PostClientsClientIdRequest;
import io.finto.fineract.sdk.models.PostClientsClientIdResponse;
import org.easymock.IMocksControl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Call;

import java.time.LocalDate;

import static io.finto.fineract.sdk.Constants.DATE_FORMAT_PATTERN;
import static io.finto.fineract.sdk.Constants.DEFAULT_DATE_FORMATTER;
import static io.finto.integration.fineract.usecase.impl.SdkChangeCustomerStatusUseCase.CUSTOMER_CLOSURE_REASON_ID;
import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;

class SdkChangeCustomerStatusUseCaseTest {

    private IMocksControl control;
    private SdkFineractUseCaseContext context;
    private CustomerId customerId = CustomerId.of(10L);

    private ClientApi clientApi;

    private SdkChangeCustomerStatusUseCase useCase;

    @BeforeEach
    void setUp() {
        control = createStrictControl();
        context = control.createMock(SdkFineractUseCaseContext.class);
        useCase = SdkChangeCustomerStatusUseCase.builder()
                .context(context)
                .build();

        clientApi = control.createMock(ClientApi.class);
    }


    /**
     * Method under test: {@link SdkChangeCustomerStatusUseCase#activateCustomer(CustomerId)}
     */
    @Test
    void test_activateCustomer_success() {
        Call<PostClientsClientIdResponse> executeCommandCall = control.createMock(Call.class);
        PostClientsClientIdRequest fineractRequest = new PostClientsClientIdRequest();
        fineractRequest.setDateFormat(DATE_FORMAT_PATTERN);
        fineractRequest.setActivationDate(LocalDate.now().format(DEFAULT_DATE_FORMATTER));
        fineractRequest.setLocale("en");

        PostClientsClientIdResponse response = new PostClientsClientIdResponse();
        response.setResourceId(customerId.getValue().intValue());

        expect(context.clientApi()).andReturn(clientApi);
        expect(clientApi.activateClient(customerId.getValue(), fineractRequest, "activate")).andReturn(executeCommandCall);
        expect(context.getResponseBody(executeCommandCall)).andReturn(response);

        control.replay();

        CustomerId actual = useCase.activateCustomer(customerId);

        control.verify();

        Assertions.assertEquals(customerId, actual);
    }

    /**
     * Method under test: {@link SdkChangeCustomerStatusUseCase#activateCustomer(CustomerId)}
     */
    @Test
    void test_closeCustomer_success() {
        Call<PostClientsClientIdResponse> executeCommandCall = control.createMock(Call.class);
        PostClientsClientIdRequest fineractRequest = new PostClientsClientIdRequest();
        fineractRequest.setDateFormat(DATE_FORMAT_PATTERN);
        fineractRequest.setClosureDate(LocalDate.now().format(DEFAULT_DATE_FORMATTER));
        fineractRequest.setClosureReasonId(CUSTOMER_CLOSURE_REASON_ID);
        fineractRequest.setLocale("en");

        PostClientsClientIdResponse response = new PostClientsClientIdResponse();
        response.setResourceId(customerId.getValue().intValue());

        expect(context.clientApi()).andReturn(clientApi);
        expect(clientApi.activateClient(customerId.getValue(), fineractRequest, "close")).andReturn(executeCommandCall);
        expect(context.getResponseBody(executeCommandCall)).andReturn(response);

        control.replay();

        CustomerId actual = useCase.closeCustomer(customerId);

        control.verify();

        Assertions.assertEquals(customerId, actual);
    }

    /**
     * Method under test: {@link SdkChangeCustomerStatusUseCase#activateCustomer(CustomerId)}
     */
    @Test
    void test_reactivateCustomer_success() {
        Call<PostClientsClientIdResponse> executeCommandCall = control.createMock(Call.class);
        PostClientsClientIdRequest fineractRequest = new PostClientsClientIdRequest();
        fineractRequest.setDateFormat(DATE_FORMAT_PATTERN);
        fineractRequest.reactivationDate(LocalDate.now().format(DEFAULT_DATE_FORMATTER));
        fineractRequest.setLocale("en");

        PostClientsClientIdResponse response = new PostClientsClientIdResponse();
        response.setResourceId(customerId.getValue().intValue());

        expect(context.clientApi()).andReturn(clientApi);
        expect(clientApi.activateClient(customerId.getValue(), fineractRequest, "reactivate")).andReturn(executeCommandCall);
        expect(context.getResponseBody(executeCommandCall)).andReturn(response);

        control.replay();

        CustomerId actual = useCase.reactivateCustomer(customerId);

        control.verify();

        Assertions.assertEquals(customerId, actual);
    }

}