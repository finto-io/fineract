package io.finto.integration.fineract.test.helpers.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.finto.fineract.sdk.api.ClientApi;
import io.finto.fineract.sdk.api.ClientIdentifierApi;
import io.finto.fineract.sdk.models.PostClientsClientIdIdentifiersRequest;
import io.finto.fineract.sdk.util.Calls;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.util.Objects;

@Getter
@Builder(toBuilder = true)
public class TestClientIssuerFineract implements TestClientRepositoryImpl.TestClientIssuer {
    @NonNull private final ClientApi clientApi;
    @NonNull private final ClientIdentifierApi clientIdentifierApi;
    private final ObjectMapper mapper = new ObjectMapper();

    private void createIdentifier(Integer customerId, Integer documentTypeId, String documentValue){
        var request = PostClientsClientIdIdentifiersRequest.builder()
                .documentTypeId(documentTypeId)
                .documentKey(documentValue)
                .status("ACTIVE").build();
        Calls.ok(clientIdentifierApi.createClientIdentifier(Long.valueOf(customerId), request));
    }

    @Override
    public Integer submitClient(TestClient client) {
        var response = Calls.ok(this.clientApi.createClient(client.toClientRequest()));
        var customerId = Objects.requireNonNull(response.getClientId()).intValue();
        if (client.getPassportNumber() != null){
            createIdentifier(customerId, 1, client.getPassportNumber());
        }
        if (client.getNationId() != null){
            createIdentifier(customerId, 2, client.getNationId());
        }
        if (client.getDriverId() != null){
            createIdentifier(customerId, 3, client.getDriverId());
        }
        return customerId;
    }

    @Override
    public void updateStatus(Integer accountId, TestClient account){
        Calls.ok(clientApi.activateClient(Long.valueOf(accountId), account.toStatusRequest(), account.getStatus().getCommand()));
    }

}
