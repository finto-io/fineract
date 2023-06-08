package io.finto.integration.fineract.usecase.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.finto.domain.account.Account;
import io.finto.domain.account.AccountDetailsUpdate;
import io.finto.domain.account.AccountId;
import io.finto.exceptions.core.FintoApiException;
import io.finto.integration.fineract.converter.FineractAccountMapper;
import io.finto.usecase.account.EnrichAccountInfoUseCase;
import lombok.*;

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
    @NonNull
    private final FineractAccountMapper accountMapper;

    public static class SdkEnrichAccountInfoUseCaseBuilder {
        private FineractAccountMapper accountMapper = FineractAccountMapper.INSTANCE;
        private ObjectMapper objectMapper = JsonMapper.builder().findAndAddModules().build();
    }

    @Override
    public Account saveAdditionalFields(AccountId accountId,
                                        AccountDetailsUpdate request) {
        var dto = accountMapper.toAccountAdditionalFieldsDto(request);
        try {
            context.getResponseBody(
                    context.dataTablesApi()
                            .updateDatatableEntryOnetoOne(
                                    ACCOUNT_ADDITIONAL_FIELDS,
                                    accountId.getValue(),
                                    objectMapper.writeValueAsString(dto)
                            )
            );
        } catch (JsonProcessingException e) {
            throw new FintoApiException(e);
        }
        return findAccount.apply(accountId);
    }

}
