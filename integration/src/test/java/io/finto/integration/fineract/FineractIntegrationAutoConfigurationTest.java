package io.finto.integration.fineract;

import io.finto.fineract.sdk.auth.ApiKeyAuth;
import io.finto.fineract.sdk.auth.HttpBasicAuth;
import io.finto.fineract.sdk.util.FineractClient;
import io.finto.integration.fineract.config.FineractClientConfiguration;
import org.easymock.IMocksControl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.zalando.logbook.Logbook;

import static org.assertj.core.api.Assertions.assertThat;
import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;

public class FineractIntegrationAutoConfigurationTest {

    private IMocksControl control;
    private FineractClientConfiguration clientConfiguration;
    private Logbook logbook;
    @BeforeEach
    void setUp() {
        control = createStrictControl();
        clientConfiguration = control.createMock(FineractClientConfiguration.class);
        logbook = control.createMock(Logbook.class);
    }


    @Test
    void testFineractClientCreation() {

        expect(clientConfiguration.getIsSecure()).andStubReturn(true);
        expect(clientConfiguration.getBaseUrl()).andStubReturn("https://test.fineract.finto.io/fineract-provider/api/v1/");
        expect(clientConfiguration.getTenant()).andStubReturn("testTenant");
        expect(clientConfiguration.getUsername()).andStubReturn("testUsername");
        expect(clientConfiguration.getPassword()).andStubReturn("testPassword");
        control.replay();

        var autoConfig = new FineractIntegrationAutoConfiguration();
        var actual = autoConfig.fineractClient(clientConfiguration, logbook);
        control.verify();

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
