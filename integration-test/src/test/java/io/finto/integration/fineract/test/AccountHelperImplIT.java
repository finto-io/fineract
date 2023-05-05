package io.finto.integration.fineract.test;

import io.finto.fineract.sdk.api.DataTablesApi;
import io.finto.fineract.sdk.api.SavingsAccountApi;
import io.finto.fineract.sdk.util.Calls;
import io.finto.integration.fineract.test.containers.ContainerHolder;
import io.finto.integration.fineract.test.helpers.FineractFixture;
import io.finto.integration.fineract.test.helpers.account.AccountHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith({ContainerHolder.class})
public class AccountHelperImplIT {

    private FineractFixture fineract;
    private SavingsAccountApi client;
    private DataTablesApi dataTablesApi;
    private AccountHelper helper;

    @BeforeEach
    public void setUp(){
        fineract = new FineractFixture();
        client = fineract.getFineractClient().getSavingsAccounts();
        dataTablesApi = fineract.getFineractClient().dataTables;
        helper = fineract.getAccountHelper();
    }

    @AfterEach
    public void setDown(){
        fineract.getAccountHelper().getSavingAccountRepository().getSavingAccountIDs()
                .forEach(x -> fineract.getFineractClient().savingsAccounts.deleteSavingsAccount(Long.valueOf(x)));
        fineract.getAccountHelper().clearAll();
    }

    @Test
    public void testCreateAccount() {
        var id = helper.buildSavingAccount().withRandomParams().withIban("iban1").create(1,1).getLastSavingAccountId();
        var res = client.deleteSavingsAccount(id.longValue());
        System.out.println(res);
        //
//        var account = Calls.ok(client.retrieveOneSavingsAccount(Long.valueOf(id), null, null));
//        var additionalFields = Calls.ok(dataTablesApi.getDatatableByAppTableId("account_fields", 1L, null));
//        assertEquals(account.getClientId(), 1);
//        assertEquals(account.getSavingsProductId(), 1);
//        assertTrue(additionalFields.contains("\"iban\": \"iban1\""));
    }

}
