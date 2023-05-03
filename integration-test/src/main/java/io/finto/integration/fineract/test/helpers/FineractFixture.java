package io.finto.integration.fineract.test.helpers;

import io.finto.fineract.sdk.util.FineractClient;
import io.finto.integration.fineract.test.helpers.account.AccountHelper;
import io.finto.integration.fineract.test.helpers.transaction.TransactionHelper;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.testcontainers.containers.DockerComposeContainer;

@Builder
public class FineractFixture {

    public static final String FINERACT_DEFAULT_SERVICE_NAME = "fineract-server";
    public static final String FINERACT_DEFAULT_BASE_PATH = "fineract-provider/api/v1/";
    public static final String FINERACT_DEFAULT_TENANT = "default";
    public static final String FINERACT_DEFAULT_USERNAME = "mifos";
    public static final String FINERACT_DEFAULT_PASSWORD = "password";

    @NonNull @Getter private DockerComposeContainer<?> fineract;
    private FineractClient fineractClient;
    private AccountHelper accountHelper;
    private TransactionHelper transactionHelper;

    public static class FineractFixtureBuilderSimplified {

        private DockerComposeContainer<?> fineract;

        public FineractFixtureBuilderSimplified(){}

        public FineractFixtureBuilderSimplified withContainer(DockerComposeContainer<?> fineract){
            this.fineract = fineract;
            return this;
        }

        public FineractFixture build(){
            return FineractFixture.builder()
                    .fineract(fineract)
                    .build();
        }

    }

    public static FineractFixtureBuilderSimplified builderSimplified() {
        return new FineractFixtureBuilderSimplified();
    }

    public FineractClient getFineractClient(){
        if (fineractClient == null) {
            var container = fineract.getContainerByServiceName(FINERACT_DEFAULT_SERVICE_NAME);
            if (container.isEmpty()) {
                throw new RuntimeException(String.format("Service with name %s is not found", FINERACT_DEFAULT_SERVICE_NAME));
            }
            var host = container.get().getHost();
            var port = container.get().getFirstMappedPort();
            fineractClient = FineractClient.builder()
                    .insecure(true)
                    .baseURL(String.format("https://%s:%s/%s", host, port, FINERACT_DEFAULT_BASE_PATH))
                    .tenant(FINERACT_DEFAULT_TENANT)
                    .basicAuth(FINERACT_DEFAULT_USERNAME, FINERACT_DEFAULT_PASSWORD)
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

}
