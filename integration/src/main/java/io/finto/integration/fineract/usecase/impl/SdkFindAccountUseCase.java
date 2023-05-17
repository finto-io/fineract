package io.finto.integration.fineract.usecase.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.finto.exceptions.core.FintoApiException;
import io.finto.fineract.sdk.api.DataTablesApi;
import io.finto.fineract.sdk.api.SavingsAccountApi;
import io.finto.fineract.sdk.models.GetSavingsAccountsAccountIdResponse;
import io.finto.fineract.sdk.util.FineractClient;
import io.finto.integration.fineract.common.FineractResponseHandler;
import io.finto.integration.fineract.common.ResponseHandler;
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

@AllArgsConstructor
@Builder
public class SdkFindAccountUseCase implements FindAccountUseCase {

    @NonNull
    private final FineractClient fineractClient;
    @NonNull
    private final FineractAccountMapper accountMapper;
    @NonNull
    private final ResponseHandler responseHandler;
    @NonNull
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private final ObjectMapper objectMapper;
    @NonNull
    private final Supplier<BankSwift> bankSwiftInfo;
    @NonNull
    private final Supplier<BankName> bankNameInfo;

    public static FindAccountUseCase defaultInstance(FineractClient fineractClient,
                                              Supplier<BankSwift> bankSwiftInfo,
                                              Supplier<BankName> bankNameInfo) {
        return SdkFindAccountUseCase.builder()
                .fineractClient(fineractClient)
                .objectMapper(JsonMapper.builder().findAndAddModules().build())
                .accountMapper(FineractAccountMapper.INSTANCE)
                .responseHandler(FineractResponseHandler.getDefaultInstance())
                .bankNameInfo(bankNameInfo)
                .bankSwiftInfo(bankSwiftInfo)
                .build();
    }

    @Override
    public Account findAccount(AccountId id) {
        SavingsAccountApi api = fineractClient.getSavingsAccounts();
        Call<GetSavingsAccountsAccountIdResponse> initAccountCall = api.retrieveOneSavingsAccount(id.getValue(), null, null);

        GetSavingsAccountsAccountIdResponse savedAccount = responseHandler.getResponseBody(initAccountCall);
        DataTablesApi dataTablesApi = fineractClient.getDataTables();
        Call<String> callDataTables = dataTablesApi.getDatatableByAppTableId("account_fields", id.getValue(), null);

        String additionalDetailsContent = responseHandler.getResponseBody(callDataTables);
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
