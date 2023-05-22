package io.finto.integration.fineract.test;

import io.finto.integration.fineract.domain.AccountId;
import io.finto.integration.fineract.test.containers.ContainerHolder;
import io.finto.integration.fineract.test.helpers.FineractFixture;
import io.finto.integration.fineract.test.helpers.account.AccountHelper;
import io.finto.integration.fineract.test.helpers.transaction.TransactionHelper;
import io.finto.integration.fineract.usecase.impl.SdkFindAccountTransactionsUseCase;
import io.finto.integration.fineract.usecase.impl.SdkFineractUseCaseContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;

@ExtendWith({ContainerHolder.class})
public class TransactionHelperImplIT {

    private FineractFixture fineract;
    private TransactionHelper transactionHelper;
    private AccountHelper accountHelper;

    @BeforeEach
    public void setUp(){
        fineract = new FineractFixture();
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
    public void testCreateTransaction() {
        var id = accountHelper.createRandomSavingAccount()
                .approveLastAccount()
                .activateLastAccount()
                .getLastSavingAccountId();
        transactionHelper.createRandomTransaction((long)id);
    }

    @Test
    public void testTransactionList() throws IOException {
        var id = accountHelper.createRandomSavingAccount()
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
