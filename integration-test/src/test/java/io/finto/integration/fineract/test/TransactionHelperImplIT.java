package io.finto.integration.fineract.test;

import io.finto.domain.account.AccountId;
import io.finto.integration.fineract.test.containers.ContainerHolder;
import io.finto.integration.fineract.test.helpers.FineractFixture;
import io.finto.integration.fineract.test.helpers.account.AccountHelper;
import io.finto.integration.fineract.test.helpers.client.ClientHelper;
import io.finto.integration.fineract.test.helpers.transaction.TransactionHelper;
import io.finto.integration.fineract.usecase.impl.account.SdkFindAccountTransactionsUseCase;
import io.finto.integration.fineract.usecase.impl.SdkFineractUseCaseContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({ContainerHolder.class})
public class TransactionHelperImplIT {

    private final Integer PRODUCT_ID = 1;
    private FineractFixture fineract;
    private TransactionHelper transactionHelper;
    private AccountHelper accountHelper;
    private ClientHelper clientHelper;


    @BeforeEach
    public void setUp(){
        fineract = new FineractFixture();
        transactionHelper = fineract.getTransactionHelper();
        accountHelper = fineract.getAccountHelper();
        clientHelper = fineract.getClientHelper();

    }

    @AfterEach
    public void setDown(){
        fineract.getTransactionHelper().getTransactionRepository().getTransactionIDs()
                .forEach(x -> fineract.getFineractClient().savingsAccounts.deleteSavingsAccount(Long.valueOf(x)));
        fineract.getTransactionHelper().clearAll();
        fineract.getAccountHelper().getSavingAccountRepository().getSavingAccountIDs()
                .forEach(x -> fineract.getFineractClient().savingsAccounts.deleteSavingsAccount(Long.valueOf(x)));
        fineract.getAccountHelper().clearAll();
        fineract.getClientHelper().getClientRepository().getClientIDs()
                .forEach(x -> fineract.getFineractClient().clients.deleteClient(Long.valueOf(x)));
        fineract.getClientHelper().clearAll();
    }

    @Test
    public void testCreateTransaction() {
        var clientId = clientHelper.createRandomClient()
                .activateLastClient()
                .getLastClientId();
        var id = accountHelper.createRandomSavingAccount(clientId, PRODUCT_ID)
                .approveLastAccount()
                .activateLastAccount()
                .getLastSavingAccountId();
        transactionHelper.createRandomTransaction((long)id);
    }

    @Test
    public void testTransactionList(){
        var clientId = clientHelper.createRandomClient()
                .activateLastClient()
                .getLastClientId();
        var id = accountHelper.createRandomSavingAccount(clientId, PRODUCT_ID)
                .approveLastAccount()
                .activateLastAccount()
                .getLastSavingAccountId();
        transactionHelper.createRandomTransaction((long)id);
        var useCase = SdkFindAccountTransactionsUseCase.builder()
                .context(SdkFineractUseCaseContext.builder()
                        .fineractClient(fineract.getFineractClient())
                        .build())
                .build();
        var transactions = useCase.findTransactionList(AccountId.of(Long.valueOf(id)));
        Assertions.assertEquals(1, transactions.size());
    }

}
