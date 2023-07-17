package io.finto.integration.fineract.usecase.impl;

import io.finto.domain.customer.CustomerId;
import io.finto.fineract.sdk.api.ClientApi;
import io.finto.fineract.sdk.models.PostClientsClientIdRequest;
import io.finto.fineract.sdk.models.PostClientsClientIdResponse;
import io.finto.integration.fineract.usecase.impl.customer.SdkChangeCustomerStatusUseCase;
import io.finto.usecase.customer.FindKeyValueDictionaryUseCase;
import org.easymock.IMocksControl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Call;

import java.time.LocalDate;

import static io.finto.fineract.sdk.Constants.*;
import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;

class SdkChangeCustomerStatusUseCaseTest {

    private IMocksControl control;
    private SdkFineractUseCaseContext context;
    private FindKeyValueDictionaryUseCase dictionaryUseCase;
    private CustomerId customerId = CustomerId.of(10L);

    private ClientApi clientApi;

    private SdkChangeCustomerStatusUseCase useCase;

    @BeforeEach
    void setUp() {
        control = createStrictControl();
        context = control.createMock(SdkFineractUseCaseContext.class);
        dictionaryUseCase = control.createMock(FindKeyValueDictionaryUseCase.class);
        useCase = SdkChangeCustomerStatusUseCase.builder()
                .context(context)
                .dictionaryUseCase(dictionaryUseCase)
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
        Long reasonId = 14L;
        Call<PostClientsClientIdResponse> executeCommandCall = control.createMock(Call.class);
        PostClientsClientIdRequest fineractRequest = new PostClientsClientIdRequest();
        fineractRequest.setDateFormat(DATE_FORMAT_PATTERN);
        fineractRequest.setClosureDate(LocalDate.now().format(DEFAULT_DATE_FORMATTER));
        fineractRequest.setClosureReasonId(reasonId.intValue());
        fineractRequest.setLocale("en");

        PostClientsClientIdResponse response = new PostClientsClientIdResponse();
        response.setResourceId(customerId.getValue().intValue());

        expect(dictionaryUseCase.getOneKeyByValue(CLIENT_CLOSURE_REASON_DICTIONARY_ID, CUSTOMER_REQUEST_CODE_NAME)).andReturn(reasonId);
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