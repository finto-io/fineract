package io.finto.integration.fineract.common;

import retrofit2.Response;

public interface ErrorResponseHandler {
    <T> RuntimeException convert(Response<T> failureResponse);
}
