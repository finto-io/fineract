package io.finto.integration.fineract.common;

import io.finto.exceptions.core.FintoApiException;
import io.finto.exceptions.core.generic.BadRequestException;
import io.finto.exceptions.core.generic.EntityNotFoundException;
import retrofit2.Response;

import java.io.IOException;

public class ErrorResponseHandlerMini implements ErrorResponseHandler {

    @Override
    public <T> RuntimeException convert(Response<T> failureResponse) {
        try {
            String errorMessage = null;
            if (failureResponse.errorBody() != null) {
                errorMessage = failureResponse.errorBody().string();
            }
            switch (failureResponse.code()) {
                case 400:
                    return new BadRequestException(BadRequestException.DEFAULT_ERROR_CODE, errorMessage);
                case 404:
                    return new EntityNotFoundException(BadRequestException.DEFAULT_ERROR_CODE, errorMessage);
                default:
                    return new FintoApiException(new IllegalArgumentException(errorMessage));
            }
        } catch (IOException ioException) {
            throw new FintoApiException(ioException);
        }
    }

}
