package io.finto.integration.fineract.common;

import io.finto.exceptions.core.FintoApiException;
import io.finto.exceptions.core.generic.BadRequestException;
import io.finto.exceptions.core.generic.EntityNotFoundException;
import io.finto.integration.fineract.test.DummyResponseBody;
import okhttp3.ResponseBody;
import org.easymock.IMocksControl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.easymock.EasyMock.createStrictControl;

class ErrorResponseHandlerMiniTest {

    private IMocksControl control;
    private ErrorResponseHandlerMini handlerMini;

    @BeforeEach
    void setUp() {
        control = createStrictControl();
        handlerMini = new ErrorResponseHandlerMini();
    }

    @Test
    void test_convert_400() {
        ResponseBody responseBody = new DummyResponseBody();
        Response<String> response = Response.error(400, responseBody);

        RuntimeException actual = handlerMini.convert(response);
        assertThat(actual).isInstanceOf(BadRequestException.class);
    }

    @Test
    void test_convert_404() {
        ResponseBody responseBody = new DummyResponseBody();
        Response<String> response = Response.error(404, responseBody);

        RuntimeException actual = handlerMini.convert(response);
        assertThat(actual).isInstanceOf(EntityNotFoundException.class);
    }


    @Test
    void test_convert_500() {
        ResponseBody responseBody = new DummyResponseBody();
        Response<String> response = Response.error(500, responseBody);

        RuntimeException actual = handlerMini.convert(response);
        assertThat(actual).isInstanceOf(FintoApiException.class);
    }

}