package io.finto.integration.fineract;

import io.finto.fineract.sdk.util.FineractClient;
import io.finto.integration.fineract.annotation.FintoFinerectClientQualifier;
import io.finto.integration.fineract.config.FineractClientConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
@EnableConfigurationProperties({FineractClientConfiguration.class})
public class FineractIntegrationAutoConfiguration {

    @Lazy
    @Bean
    @FintoFinerectClientQualifier
    @ConditionalOnMissingBean({FineractClient.class})
    public FineractClient fineractClient(FineractClientConfiguration clientConfiguration) {
        return FineractClient.builder()
                .insecure(clientConfiguration.getIsSecure())
                .baseURL(clientConfiguration.getBaseUrl())
                .tenant(clientConfiguration.getTenant())
                .basicAuth(clientConfiguration.getUsername(), clientConfiguration.getPassword())
                .build();
    }

}
