package io.finto.integration.fineract.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AccountAdditionalFields {
    private Long savingsAccountId;
    private String iban;
    private Boolean externalIntegrationSuccess;
    private String externalSource;
    private String integrationFailureType;
    private String swift;
    private String externalAccountNumber;
    private String externalAccountName;
    private String externalBranch;
}
