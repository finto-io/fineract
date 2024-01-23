package io.finto.integration.fineract.usecase.impl.account;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.finto.domain.account.Account;
import io.finto.domain.account.AccountId;
import io.finto.domain.account.BankName;
import io.finto.domain.account.BankSwift;
import io.finto.domain.account.PocketAccount;
import io.finto.exceptions.core.FintoApiException;
import io.finto.fineract.sdk.api.DataTablesApi;
import io.finto.fineract.sdk.api.SavingsAccountApi;
import io.finto.fineract.sdk.models.GetSavingsAccountsAccountIdResponse;
import io.finto.integration.fineract.converter.FineractAccountMapper;
import io.finto.integration.fineract.dto.AccountAdditionalFieldsDto;
import io.finto.integration.fineract.dto.PocketAccountAdditionalFieldsDto;
import io.finto.integration.fineract.usecase.impl.SdkFineractUseCaseContext;
import io.finto.usecase.account.FindAccountUseCase;
import lombok.*;
import retrofit2.Call;

import java.util.function.Supplier;

import static io.finto.fineract.sdk.CustomDatatableNames.ACCOUNT_ADDITIONAL_FIELDS;
import static io.finto.fineract.sdk.CustomDatatableNames.CARD_ADDITIONAL_FIELDS;

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
        GetSavingsAccountsAccountIdResponse savedAccount = retrieveSavingsAccount(id);
        String additionalDetailsContent = retrieveAdditionalDetailsContent(context.dataTablesApi(), ACCOUNT_ADDITIONAL_FIELDS, id);

        AccountAdditionalFieldsDto accountAdditionalFields = parseAdditionalFields(additionalDetailsContent);
        return accountMapper.toAccount(savedAccount, accountAdditionalFields, bankSwiftInfo.get(), bankNameInfo.get());
    }

    @Override
    public PocketAccount findPocketAccount(AccountId id) {
        GetSavingsAccountsAccountIdResponse savedAccount = retrieveSavingsAccount(id);
        String additionalDetailsContent = retrieveAdditionalDetailsContent(context.dataTablesApi(), CARD_ADDITIONAL_FIELDS, id);

        PocketAccountAdditionalFieldsDto accountAdditionalFields = parsePocketAdditionalFields(additionalDetailsContent);
        return accountMapper.toPocketAccount(savedAccount, accountAdditionalFields, bankSwiftInfo.get(), bankNameInfo.get());
    }

    private AccountAdditionalFieldsDto parseAdditionalFields(String content) {
        try {
            AccountAdditionalFieldsDto[] accountAdditionalFieldsResponse = objectMapper.readValue(content, AccountAdditionalFieldsDto[].class);
            return accountAdditionalFieldsResponse.length > 0 ? accountAdditionalFieldsResponse[0] : null;
        } catch (JsonProcessingException e) {
            throw new FintoApiException(e);
        }
    }

    private PocketAccountAdditionalFieldsDto parsePocketAdditionalFields(String content) {
        try {
            PocketAccountAdditionalFieldsDto[] accountAdditionalFieldsResponse = objectMapper.readValue(content, PocketAccountAdditionalFieldsDto[].class);
            return accountAdditionalFieldsResponse.length > 0 ? accountAdditionalFieldsResponse[0] : null;
        } catch (JsonProcessingException e) {
            throw new FintoApiException(e);
        }
    }

    private GetSavingsAccountsAccountIdResponse retrieveSavingsAccount(AccountId id) {
        SavingsAccountApi api = context.savingsAccountApi();
        Call<GetSavingsAccountsAccountIdResponse> initAccountCall = api.retrieveOneSavingsAccount(id.getValue(), null, null, null);
        return context.getResponseBody(initAccountCall);
    }

    private String retrieveAdditionalDetailsContent(DataTablesApi dataTablesApi, String customDatatableName, AccountId id) {
        Call<String> callDataTables = dataTablesApi.getDatatableByAppTableId(customDatatableName, id.getValue(), null, null);
        return context.getResponseBody(callDataTables);
    }


}
