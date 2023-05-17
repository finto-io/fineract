package io.finto.integration.fineract.common;

import io.finto.exceptions.core.FintoApiException;
import io.finto.integration.fineract.test.DummyResponseBody;
import okhttp3.ResponseBody;
import org.easymock.IMocksControl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;

class FineractResponseHandlerMiniTest {

    private IMocksControl control;
    private ErrorResponseHandler errorResponseHandler;
    private Call<String> callMock;

    private FineractResponseHandlerMini responseHandler;

    @BeforeEach
    void setUp() {
        control =  createStrictControl();
        errorResponseHandler = control.createMock(ErrorResponseHandler.class);
        callMock = control.createMock(Call.class);

        responseHandler = new FineractResponseHandlerMini(errorResponseHandler);
    }

    @Test
    void test_getResponseBody_failureResponse() throws IOException {
        ResponseBody responseBody = new DummyResponseBody();
        Response<String> response = Response.error(400, responseBody);
        expect(callMock.execute()).andReturn(response);
        expect(errorResponseHandler.convert(response)).andReturn(new FintoApiException());

        control.replay();

        assertThatThrownBy(() -> responseHandler.getResponseBody(callMock)).isInstanceOf(FintoApiException.class);

        control.verify();
    }

    @Test
    void test_getResponseBody_IOException() throws IOException {
        Call<String> callMock = control.createMock(Call.class);
        expect(callMock.execute()).andThrow(new IOException());

        control.replay();

        assertThatThrownBy(() -> responseHandler.getResponseBody(callMock))
                .isInstanceOf(FintoApiException.class)
                .hasCauseInstanceOf(IOException.class);

        control.verify();
    }

    @Test
    void test_getResponseBody_successResponse() throws IOException {
        Call<String> callMock = control.createMock(Call.class);
        expect(callMock.execute()).andReturn(Response.success("test"));

        control.replay();

        assertThat(responseHandler.getResponseBody(callMock)).isEqualTo("test");

        control.verify();
    }

}