package io.finto.integration.fineract.usecase.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.finto.exceptions.core.FintoApiException;
import io.finto.fineract.sdk.api.DataTablesApi;
import io.finto.fineract.sdk.api.SavingsAccountApi;
import io.finto.fineract.sdk.models.GetSavingsAccountsAccountIdResponse;
import io.finto.integration.fineract.converter.FineractAccountMapper;
import io.finto.integration.fineract.domain.Account;
import io.finto.integration.fineract.domain.AccountAdditionalFields;
import io.finto.integration.fineract.domain.AccountId;
import io.finto.integration.fineract.domain.BankName;
import io.finto.integration.fineract.domain.BankSwift;
import io.finto.integration.fineract.usecase.FindAccountUseCase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import retrofit2.Call;

import java.util.function.Supplier;

import static io.finto.fineract.sdk.CustomDatatableNames.ACCOUNT_ADDITIONAL_FIELDS;

@AllArgsConstructor
@Builder
public class SdkFindAccountUseCase implements FindAccountUseCase {

    @NonNull
    private final SdkFineractUseCaseContext context;
    @NonNull
    private final FineractAccountMapper accountMapper;
    @NonNull
    private final Supplier<BankSwift> bankSwiftInfo;
    @NonNull
    private final Supplier<BankName> bankNameInfo;
    @NonNull
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private final ObjectMapper objectMapper;

    public static class SdkFindAccountUseCaseBuilder {
        private FineractAccountMapper accountMapper = FineractAccountMapper.INSTANCE;
        private ObjectMapper objectMapper = JsonMapper.builder().findAndAddModules().build();
    }

    @Override
    public Account findAccount(AccountId id) {
        SavingsAccountApi api = context.savingsAccountApi();
        Call<GetSavingsAccountsAccountIdResponse> initAccountCall = api.retrieveOneSavingsAccount(id.getValue(), null, null, null);

        GetSavingsAccountsAccountIdResponse savedAccount = context.getResponseBody(initAccountCall);
        DataTablesApi dataTablesApi = context.dataTablesApi();
        Call<String> callDataTables = dataTablesApi.getDatatableByAppTableId(ACCOUNT_ADDITIONAL_FIELDS, id.getValue(), null);

        String additionalDetailsContent = context.getResponseBody(callDataTables);
        AccountAdditionalFields accountAdditionalFields = parseAdditionalFields(additionalDetailsContent);
        return accountMapper.toAccount(savedAccount, accountAdditionalFields, bankSwiftInfo.get(), bankNameInfo.get());
    }

    private AccountAdditionalFields parseAdditionalFields(String content) {
        try {
            AccountAdditionalFields[] accountAdditionalFieldsResponse = objectMapper.readValue(content, AccountAdditionalFields[].class);
            return accountAdditionalFieldsResponse.length > 0 ? accountAdditionalFieldsResponse[0] : null;
        } catch (JsonProcessingException e) {
            throw new FintoApiException(e);
        }
    }

}
