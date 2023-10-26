package io.finto.integration.fineract.test;

import io.finto.fineract.sdk.api.DataTablesApi;
import io.finto.fineract.sdk.api.SavingsAccountApi;
import io.finto.fineract.sdk.util.Calls;
import io.finto.integration.fineract.test.containers.ContainerHolder;
import io.finto.integration.fineract.test.helpers.FineractFixture;
import io.finto.integration.fineract.test.helpers.account.AccountHelper;
import io.finto.integration.fineract.test.helpers.client.ClientHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@ExtendWith({ContainerHolder.class})
public class AccountHelperImplIT {

    private final Integer PRODUCT_ID = 1;
    private FineractFixture fineract;
    private SavingsAccountApi client;
    private DataTablesApi dataTablesApi;
    private AccountHelper helper;
    private ClientHelper clientHelper;

    @BeforeEach
    public void setUp() {
        fineract = new FineractFixture();
        client = fineract.getFineractClient().getSavingsAccounts();
        dataTablesApi = fineract.getFineractClient().dataTables;
        helper = fineract.getAccountHelper();
        clientHelper = fineract.getClientHelper();
    }

    @AfterEach
    public void setDown() {
        fineract.getAccountHelper().getSavingAccountRepository().getSavingAccountIDs()
                .forEach(x -> fineract.getFineractClient().savingsAccounts.deleteSavingsAccount(Long.valueOf(x)));
        fineract.getAccountHelper().clearAll();
        fineract.getClientHelper().getClientRepository().getClientIDs()
                .forEach(x -> fineract.getFineractClient().clients.deleteClient(Long.valueOf(x)));
        fineract.getClientHelper().clearAll();
    }

    @Test
    public void testCreateAccount() {
        var clientId = clientHelper.createRandomClient()
                .activateLastClient()
                .getLastClientId();
        String iban = "iban1";
        var id = helper.buildSavingAccount().withRandomParams().withIban(iban).create(clientId, PRODUCT_ID).getLastSavingAccountId();

        var account = Calls.ok(client.retrieveOneSavingsAccount(Long.valueOf(id), null, null, null));
        var additionalFields = Calls.ok(dataTablesApi.getDatatableByAppTableId("account_fields", 1L, null, null));
        Assertions.assertEquals(clientId, account.getClientId());
        Assertions.assertEquals(PRODUCT_ID, account.getSavingsProductId());
        Assertions.assertTrue(additionalFields.contains("\"iban\": \"" + iban + "\""));
    }

}
