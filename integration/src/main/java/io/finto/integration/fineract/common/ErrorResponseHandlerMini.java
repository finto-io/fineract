package io.finto.integration.fineract.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.finto.exceptions.core.FintoApiException;
import io.finto.exceptions.core.generic.BadRequestException;
import io.finto.exceptions.core.generic.EntityNotFoundException;
import io.finto.fineract.sdk.models.CommonErrorResponse;
import io.finto.fineract.sdk.models.ErrorDetails;
import retrofit2.Response;

import java.io.IOException;
import java.util.stream.Collectors;

public class ErrorResponseHandlerMini implements ErrorResponseHandler {

    private final ObjectMapper mapper = JsonMapper.builder().findAndAddModules().build();
    private final FineractBusinessErrorHandler businessErrorHandler = new FineractBusinessErrorHandler();

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
                String failureResponseBody = failureResponse.errorBody().string();
                try {
                    var error = mapper.readValue(failureResponseBody, CommonErrorResponse.class);
                    errorMessage = getErrorMessage(error);
                } catch(JsonProcessingException exception) {
                    var error = mapper.readValue(failureResponseBody, InternalServerErrorResponse.class);
                    errorMessage = error.toString();
                }

            }
            switch (failureResponse.code()) {
                case 400:
                    return new BadRequestException(BadRequestException.DEFAULT_ERROR_CODE, businessErrorHandler.convertMessage(errorMessage));
                case 403:
                    return new BadRequestException(BadRequestException.DEFAULT_ERROR_CODE, businessErrorHandler.convertMessage(errorMessage));
                case 404:
                    return new EntityNotFoundException(EntityNotFoundException.DEFAULT_ERROR_CODE, errorMessage);
                default:
                    return new FintoApiException(FintoApiException.DEFAULT_ERROR_CODE, new RuntimeException(errorMessage));
            }
        } catch (IOException ioException) {
            throw new FintoApiException(ioException);
        }
    }

}
