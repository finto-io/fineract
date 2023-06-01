package io.finto.integration.fineract.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.finto.exceptions.core.FintoApiException;
import io.finto.exceptions.core.generic.BadRequestException;
import io.finto.exceptions.core.generic.EntityNotFoundException;
import io.finto.fineract.sdk.models.CommonErrorResponse;
import io.finto.fineract.sdk.models.ErrorDetails;
import okhttp3.ResponseBody;
import org.easymock.IMocksControl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.easymock.EasyMock.createStrictControl;

class ErrorResponseHandlerMiniTest {

    private IMocksControl control;
    private ObjectMapper writer;
    private ErrorResponseHandlerMini handlerMini;

    @BeforeEach
    void setUp() {
        control = createStrictControl();
        writer = JsonMapper.builder().findAndAddModules().build();
        handlerMini = new ErrorResponseHandlerMini();
    }

    private String commonErrorResponse(String defaultMessage,
                                       List<String> errorDetailsMessage) throws JsonProcessingException {
        return writer.writeValueAsString(CommonErrorResponse.builder()
                .defaultUserMessage(defaultMessage)
                .errors(errorDetailsMessage == null ? Collections.emptyList() : errorDetailsMessage.stream()
                        .map(x -> ErrorDetails.builder()
                                .defaultUserMessage(x)
                                .build())
                        .collect(Collectors.toList()))
                .build());
    }

    private String specificErrorResponse(String defaultMessage) throws JsonProcessingException {
        return writer.writeValueAsString(InternalServerErrorResponse.builder()
                .path("/test")
                .status(500)
                .error(defaultMessage)
                .timestamp(OffsetDateTime.now())
                .build());
    }

    @Test
    void test_convert_400() throws Exception {
        ResponseBody responseBody = ResponseBody.create(null, commonErrorResponse(null, List.of("error1", "error2")));
        Response<String> response = Response.error(400, responseBody);

        RuntimeException actual = handlerMini.convert(response);
        assertThat(actual).isInstanceOf(BadRequestException.class);
        assertThat(actual.getMessage()).isEqualTo("error1;error2");
    }

    @Test
    void test_convert_404() throws Exception {
        ResponseBody responseBody = ResponseBody.create(null, commonErrorResponse(null, List.of("not found")));
        Response<String> response = Response.error(404, responseBody);

        RuntimeException actual = handlerMini.convert(response);
        assertThat(actual).isInstanceOf(EntityNotFoundException.class);
        assertThat(actual.getMessage()).isEqualTo("not found");
    }


    @Test
    void test_convert_500() throws Exception {
        ResponseBody responseBody = ResponseBody.create(null, commonErrorResponse("something wrong", null));
        Response<String> response = Response.error(500, responseBody);

        assertThatThrownBy(() -> {
            throw handlerMini.convert(response);
        })
        .isInstanceOf(FintoApiException.class)
        .hasCause(new RuntimeException("something wrong"));
    }

    @Test
    void test_convert_specific_500() throws Exception {
        ResponseBody responseBody = ResponseBody.create(null, specificErrorResponse("something wrong"));
        Response<String> response = Response.error(500, responseBody);

        assertThatThrownBy(() -> {
            throw handlerMini.convert(response);
        })
        .isInstanceOf(FintoApiException.class)
        .hasCauseInstanceOf(RuntimeException.class);
    }

}