package io.finto.integration.fineract.test;

import io.finto.fineract.sdk.api.ClientApi;
import io.finto.fineract.sdk.util.Calls;
import io.finto.integration.fineract.test.containers.ContainerHolder;
import io.finto.integration.fineract.test.helpers.FineractFixture;
import io.finto.integration.fineract.test.helpers.client.ClientHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({ContainerHolder.class})
class ClientHelperImplIT {

    private FineractFixture fineract;
    private ClientApi clientApi;
    private ClientHelper helper;

    @BeforeEach
    public void setUp() {
        fineract = new FineractFixture();
        clientApi = fineract.getFineractClient().getClients();
        helper = fineract.getClientHelper();
    }

    @AfterEach
    public void setDown() {
        fineract.getClientHelper().getClientRepository().getClientIDs()
                .forEach(x -> fineract.getFineractClient().clients.deleteClient(Long.valueOf(x)));
        fineract.getClientHelper().clearAll();
    }

    @Test
    void testCreateClients_success_evenAfterRemove() {
        var expectedClientId_1 = helper.buildClient().withRandomParams().create().getLastClientId();
        var expectedClientId_2 = helper.buildClient().withRandomParams().create().getLastClientId();

        var actual_1 = Calls.ok(clientApi.retrieveOneClient(Long.valueOf(expectedClientId_1), false, null));
        var actual_2 = Calls.ok(clientApi.retrieveOneClient(Long.valueOf(expectedClientId_2), false, null));

        Assertions.assertEquals(expectedClientId_1, actual_1.getId());
        Assertions.assertEquals(expectedClientId_2, actual_2.getId());

        setDown();

        var expectedClientId_3 = helper.buildClient().withRandomParams().create().getLastClientId();
        var expectedClientId_4 = helper.buildClient().withRandomParams().create().getLastClientId();

        var actual_3 = Calls.ok(clientApi.retrieveOneClient(Long.valueOf(expectedClientId_3), false, null));
        var actual_4 = Calls.ok(clientApi.retrieveOneClient(Long.valueOf(expectedClientId_4), false, null));

        Assertions.assertEquals(expectedClientId_3, actual_3.getId());
        Assertions.assertEquals(expectedClientId_4, actual_4.getId());


    }

}
