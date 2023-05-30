package io.finto.integration.fineract.usecase.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.finto.exceptions.core.FintoApiException;
import io.finto.integration.fineract.domain.Account;
import io.finto.integration.fineract.domain.AccountAdditionalFields;
import io.finto.integration.fineract.domain.AccountId;
import io.finto.integration.fineract.usecase.EnrichAccountInfoUseCase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

import java.util.function.Function;

import static io.finto.fineract.sdk.CustomDatatableNames.ACCOUNT_ADDITIONAL_FIELDS;

@AllArgsConstructor
@Builder
public class SdkEnrichAccountInfoUseCase implements EnrichAccountInfoUseCase {

    @NonNull
    private final SdkFineractUseCaseContext context;
    @NonNull
    private final Function<AccountId, Account> findAccount;
    @NonNull
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private final ObjectMapper objectMapper;

    public static class SdkEnrichAccountInfoUseCaseBuilder {
        private ObjectMapper objectMapper = JsonMapper.builder().findAndAddModules().build();
    }

    @Override
    public Account saveAdditionalFields(AccountId accountId,
                                        AccountAdditionalFields additionalFields) {
        try {
            context.getResponseBody(
                    context.dataTablesApi()
                            .updateDatatableEntryOnetoOne(
                                    ACCOUNT_ADDITIONAL_FIELDS,
                                    accountId.getValue(),
                                    objectMapper.writeValueAsString(additionalFields)
                            )
            );
        } catch (JsonProcessingException e) {
            throw new FintoApiException(e);
        }
        return findAccount.apply(accountId);
    }

}
