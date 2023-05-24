package io.finto.integration.fineract.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.finto.exceptions.core.FintoApiException;
import io.finto.exceptions.core.generic.BadRequestException;
import io.finto.exceptions.core.generic.EntityNotFoundException;
import io.finto.fineract.sdk.models.CommonErrorResponse;
import io.finto.fineract.sdk.models.ErrorDetails;
import retrofit2.Response;

import java.io.IOException;
import java.util.stream.Collectors;

public class ErrorResponseHandlerMini implements ErrorResponseHandler {

    private final ObjectMapper mapper = new ObjectMapper();


    private String getErrorMessage(CommonErrorResponse error){
        if (error.getErrors() == null || error.getErrors().isEmpty()) {
            return error.getDefaultUserMessage();
        }
        return error.getErrors().stream()
                .map(ErrorDetails::getDefaultUserMessage)
                .collect(Collectors.joining(";"));
    }


    @Override
    public <T> RuntimeException convert(Response<T> failureResponse) {
        try {
            String errorMessage = null;
            if (failureResponse.errorBody() != null) {
                var error = mapper.readValue(failureResponse.errorBody().string(), CommonErrorResponse.class);
                errorMessage = getErrorMessage(error);
            }
            switch (failureResponse.code()) {
                case 400:
                    return new BadRequestException(BadRequestException.DEFAULT_ERROR_CODE, errorMessage);
                case 404:
                    return new EntityNotFoundException(EntityNotFoundException.DEFAULT_ERROR_CODE, errorMessage);
                default:
                    return new FintoApiException(FintoApiException.DEFAULT_ERROR_CODE, errorMessage);
            }
        } catch (IOException ioException) {
            throw new FintoApiException(ioException);
        }
    }

}
