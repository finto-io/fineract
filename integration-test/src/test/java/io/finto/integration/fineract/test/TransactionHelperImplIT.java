package io.finto.integration.fineract.test;

import io.finto.integration.fineract.test.containers.ContainerHolder;
import io.finto.integration.fineract.test.helpers.FineractFixture;
import io.finto.integration.fineract.test.helpers.account.AccountHelper;
import io.finto.integration.fineract.test.helpers.transaction.TransactionHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({ContainerHolder.class})
public class TransactionHelperImplIT {

    private FineractFixture fineract;
    private TransactionHelper transactionHelper;
    private AccountHelper accountHelper;

    @BeforeEach
    public void setUp(){
        fineract = FineractFixture.builderSimplified().withContainer(ContainerHolder.getFineract()).build();
        transactionHelper = fineract.getTransactionHelper();
        accountHelper = fineract.getAccountHelper();
    }

    @AfterEach
    public void setDown(){
        fineract.getTransactionHelper().getTransactionRepository().getTransactionIDs()
                .forEach(x -> fineract.getFineractClient().savingsAccounts.deleteSavingsAccount(Long.valueOf(x)));
        fineract.getTransactionHelper().clearAll();
    }

    @Test
    public void testCreateAccount() {
        var id = accountHelper.createRandomSavingAccount()
                .approveLastAccount()
                .activateLastAccount()
                .getLastSavingAccountId();
        transactionHelper.createRandomTransaction((long)id);
    }

}
