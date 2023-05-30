package io.finto.integration.fineract.test.helpers.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.finto.fineract.sdk.api.ClientApi;
import io.finto.fineract.sdk.util.Calls;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.util.Objects;

@Getter
@Builder(toBuilder = true)
public class TestClientIssuerFineract implements TestClientRepositoryImpl.TestClientIssuer {
    @NonNull private final ClientApi clientApi;
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Integer submitClient(TestClient client) {
        var response = Calls.ok(this.clientApi.createClient(client.toClientRequest()));
        return Objects.requireNonNull(response.getClientId()).intValue();
    }

    @Override
    public void updateStatus(Integer accountId, TestClient account){
        Calls.ok(clientApi.activateClient(Long.valueOf(accountId), account.toStatusRequest(), account.getStatus().getCommand()));
    }

}
