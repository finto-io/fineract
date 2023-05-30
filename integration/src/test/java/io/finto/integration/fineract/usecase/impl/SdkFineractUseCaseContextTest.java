package io.finto.integration.fineract.usecase.impl;

import io.finto.fineract.sdk.api.DataTablesApi;
import io.finto.fineract.sdk.api.SavingsAccountApi;
import io.finto.fineract.sdk.util.FineractClient;
import io.finto.integration.fineract.common.ResponseHandler;
import org.easymock.IMocksControl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Call;

import static org.assertj.core.api.Assertions.assertThat;
import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;

class SdkFineractUseCaseContextTest {

    private IMocksControl control;
    private FineractClient fineractClient;
    private ResponseHandler responseHandler;

    private SdkFineractUseCaseContext context;

    @BeforeEach
    void setup() {
        control = createStrictControl();
        fineractClient = control.createMock(FineractClient.class);
        responseHandler = control.createMock(ResponseHandler.class);

        context = SdkFineractUseCaseContext.builder().fineractClient(fineractClient).responseHandler(responseHandler).build();
    }

    @Test
    void test_fineract_retrieve() {
        assertThat(context.fineract()).isSameAs(fineractClient);
    }

    @Test
    void test_responseHandler_retrieve() {
        assertThat(context.responseHandler()).isSameAs(responseHandler);
    }

    @Test
    void test_savingsAccountApi_retrieve() {
        SavingsAccountApi savingsAccountApi = control.createMock(SavingsAccountApi.class);

        expect(fineractClient.getSavingsAccounts()).andReturn(savingsAccountApi);

        control.replay();

        assertThat(context.savingsAccountApi()).isSameAs(savingsAccountApi);

        control.verify();
    }

    @Test
    void test_dataTablesApi_retrieve() {
        DataTablesApi dataTablesApi = control.createMock(DataTablesApi.class);

        expect(fineractClient.getDataTables()).andReturn(dataTablesApi);

        control.replay();

        assertThat(context.dataTablesApi()).isSameAs(dataTablesApi);

        control.verify();
    }

    @Test
    void test_getResponseBody_retrieve() {
        Call<String> call = control.createMock(Call.class);
        String result = "1234";

        expect(responseHandler.getResponseBody(call)).andReturn(result);

        control.replay();

        assertThat(context.getResponseBody(call)).isSameAs(result);

        control.verify();
    }

}