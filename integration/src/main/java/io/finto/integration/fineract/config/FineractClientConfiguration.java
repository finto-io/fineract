package io.finto.integration.fineract.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@ConfigurationProperties("io.finto.fineract.client")
@Validated
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FineractClientConfiguration {

    @NotBlank
    private String baseUrl;
    @NotBlank
    private String tenant;
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    private Boolean isSecure = true;

}
