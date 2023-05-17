package io.finto.integration.fineract.common;

import io.finto.exceptions.core.FintoApiException;
import retrofit2.Call;

public interface ResponseHandler {
    <T> T getResponseBody(Call<T> call) throws FintoApiException;
}
