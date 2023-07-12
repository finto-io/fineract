package io.finto.integration.fineract.usecase.impl;

import io.finto.domain.customer.CustomerId;
import io.finto.fineract.sdk.models.PostClientsClientIdRequest;
import io.finto.usecase.customer.ChangeCustomerStatusUseCase;
import io.finto.usecase.customer.FindKeyValueDictionaryUseCase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;

import java.time.LocalDate;
import java.util.Objects;

import static io.finto.fineract.sdk.Constants.*;

@AllArgsConstructor
@Builder
public class SdkChangeCustomerStatusUseCase implements ChangeCustomerStatusUseCase {

    private static final String COMMAND_ACTIVATE = "activate";
    private static final String COMMAND_REACTIVATE = "reactivate";
    private static final String COMMAND_CLOSE = "close";

    @NonNull
    private final SdkFineractUseCaseContext context;
    @NonNull
    private final FindKeyValueDictionaryUseCase dictionaryUseCase;

    @Override
    public CustomerId activateCustomer(CustomerId customerId) {
        PostClientsClientIdRequest request = new PostClientsClientIdRequest();
        request.setDateFormat(DATE_FORMAT_PATTERN);
        request.activationDate(LocalDate.now().format(DEFAULT_DATE_FORMATTER));
        request.setLocale(LOCALE);
        return executeCommand(customerId, request, COMMAND_ACTIVATE);
    }


    @Override
    public CustomerId closeCustomer(CustomerId customerId) {
        var closeReasonId = dictionaryUseCase.getOneKeyByValue(CLIENT_CLOSURE_REASON_DICTIONARY_ID, CUSTOMER_REQUEST_CODE_NAME);
        PostClientsClientIdRequest request = new PostClientsClientIdRequest();
        request.setDateFormat(DATE_FORMAT_PATTERN);
        request.closureDate(LocalDate.now().format(DEFAULT_DATE_FORMATTER));
        request.setLocale(LOCALE);
        request.closureReasonId(closeReasonId.intValue());
        return executeCommand(customerId, request, COMMAND_CLOSE);

    }

    @Override
    public CustomerId reactivateCustomer(CustomerId customerId) {
        PostClientsClientIdRequest request = new PostClientsClientIdRequest();
        request.setDateFormat(DATE_FORMAT_PATTERN);
        request.reactivationDate(LocalDate.now().format(DEFAULT_DATE_FORMATTER));
        request.setLocale(LOCALE);
        return executeCommand(customerId, request, COMMAND_REACTIVATE);
    }

    public CustomerId executeCommand(CustomerId customerId, PostClientsClientIdRequest request, String command) {
        return CustomerId.of(
                Objects.requireNonNull(context
                                .getResponseBody(
                                        context.clientApi().activateClient(customerId.getValue(), request, command)
                                )
                                .getResourceId())
                        .longValue()
        );
    }

}
