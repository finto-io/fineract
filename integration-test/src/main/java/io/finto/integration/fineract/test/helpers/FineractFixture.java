package io.finto.integration.fineract.test.helpers;

import io.finto.fineract.sdk.util.FineractClient;
import io.finto.integration.fineract.test.helpers.account.AccountHelper;
import io.finto.integration.fineract.test.helpers.client.ClientHelper;
import io.finto.integration.fineract.test.helpers.transaction.TransactionHelper;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class FineractFixture {

    private FineractClient fineractClient;
    private AccountHelper accountHelper;
    private TransactionHelper transactionHelper;
    private ClientHelper clientHelper;

    public FineractClient getFineractClient(){
        if (fineractClient == null) {
            fineractClient = FineractClient.builder()
                    .insecure(true)
                    .baseURL(System.getProperty("FINERACT_BASE_PATH"))
                    .tenant(System.getProperty("FINERACT_TENANT"))
                    .basicAuth(System.getProperty("FINERACT_USERNAME"), System.getProperty("FINERACT_PASSWORD"))
                    .build();
        }
        return fineractClient;
    }

    public AccountHelper getAccountHelper(){
        if (accountHelper == null){
            accountHelper = new AccountHelper(getFineractClient());
        }
        return accountHelper;
    }

    public TransactionHelper getTransactionHelper(){
        if (transactionHelper == null){
            transactionHelper = new TransactionHelper(getFineractClient());
        }
        return transactionHelper;
    }
    public ClientHelper getClientHelper(){
        if (clientHelper == null){
            clientHelper = new ClientHelper(getFineractClient());
        }
        return clientHelper;
    }

}
