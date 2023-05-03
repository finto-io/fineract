package io.finto.integration.fineract.test.helpers.account;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.finto.fineract.sdk.api.SavingsAccountApi;
import io.finto.fineract.sdk.util.Calls;
import io.finto.fineract.sdk.util.FineractClient;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import retrofit2.Call;

import java.util.Objects;

@Getter
@Builder(toBuilder = true)
public class TestSavingAccountIssuerFineract implements TestSavingAccountRepositoryImpl.TestSavingAccountIssuer {
    @NonNull private final SavingsAccountApi client;
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Integer submitSavingAccount(TestSavingAccount account) {
        var response = Calls.ok(client.submitSavingsAccountsApplication(account.toAccountRequest()));
        return Objects.requireNonNull(response.getSavingsId());
    }

    @Override
    public void updateStatus(Integer accountId, TestSavingAccount account){
        Calls.ok(client.handleSavingsAccountsCommands(Long.valueOf(accountId), account.toStatusRequest(), account.getStatus().getCommand()));
    }

}
