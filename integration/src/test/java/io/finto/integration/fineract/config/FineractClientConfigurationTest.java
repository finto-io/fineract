package io.finto.integration.fineract.config;

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
class FineractClientConfigurationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void testConfigFullfill() {
        FineractClientConfiguration actual = applicationContext.getBean(FineractClientConfiguration.class);

        FineractClientConfiguration expected = FineractClientConfiguration.builder()
                .baseUrl("https://test.fineract.finto.io/fineract-provider/api/v1/")
                .tenant("testTenant")
                .username("testUsername")
                .password("testPassword")
                .isSecure(true)
                .build();

        assertThat(actual).isEqualTo(expected);
    }

}
