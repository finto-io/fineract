package io.finto.integration.fineract.common;

import io.finto.exceptions.core.FintoApiException;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;

@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class FineractResponseHandlerMini implements ResponseHandler {

    private final ErrorResponseHandler errorResponseHandler;

    @Override
    public <T> T getResponseBody(Call<T> call) {
        try {
            Response<T> response = call.execute();
            if (!response.isSuccessful()) {
                throw errorResponseHandler.convert(response);
            }
            return response.body();
        } catch (IOException ioException) {
            throw new FintoApiException(ioException);
        }
    }

}
