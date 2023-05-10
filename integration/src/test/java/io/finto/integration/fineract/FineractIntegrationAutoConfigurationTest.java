package io.finto.integration.fineract;

import io.finto.fineract.sdk.auth.ApiKeyAuth;
import io.finto.fineract.sdk.auth.HttpBasicAuth;
import io.finto.fineract.sdk.util.FineractClient;
import io.finto.integration.fineract.test.Application;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = Application.class, properties = {
        "io.finto.fineract.client.base-url=https://test.fineract.finto.io/fineract-provider/api/v1/",
        "io.finto.fineract.client.tenant=testTenant",
        "io.finto.fineract.client.username=testUsername",
        "io.finto.fineract.client.password=testPassword"
})
public class FineractIntegrationAutoConfigurationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void testFineractClientCreation() {
        FineractClient actual = applicationContext.getBean(FineractClient.class);

        FineractClient expected = FineractClient.builder()
                .insecure(true)
                .baseURL("https://test.fineract.finto.io/fineract-provider/api/v1/")
                .tenant("testTenant")
                .basicAuth("testUsername", "testPassword")
                .build();

        assertThat(actual.getRetrofit().baseUrl().url().toString())
                .isEqualTo(expected.getRetrofit().baseUrl().url().toString());
        assertThat(actual.getOkHttpClient().interceptors())
                .anyMatch(actualInterceptor -> expected.getOkHttpClient().interceptors().stream()
                        .anyMatch(expectedInterceptor -> {
                            boolean isEquals = expectedInterceptor.getClass().equals(actualInterceptor.getClass());

                            if (actualInterceptor instanceof ApiKeyAuth) {
                                return isEquals && ((ApiKeyAuth) actualInterceptor).getApiKey().equals(((ApiKeyAuth) expectedInterceptor).getApiKey());
                            } else if (actualInterceptor instanceof HttpBasicAuth) {
                                HttpBasicAuth expectedBasicAuth = (HttpBasicAuth) expectedInterceptor;
                                HttpBasicAuth actualBasicAuth = (HttpBasicAuth) actualInterceptor;
                                return isEquals &&
                                        actualBasicAuth.getUsername().equals(expectedBasicAuth.getUsername()) &&
                                        actualBasicAuth.getPassword().equals(expectedBasicAuth.getPassword());

                            }
                            return isEquals;
                        }));
    }

}
