package io.finto.integration.fineract.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerAdditionalFieldsDto {
    @JsonProperty("kyc_id")
    private String kycId;
    @JsonProperty("external_customer_number")
    private String externalCustomerNumber;
    @JsonProperty("nationality")
    private String nationality;
    @JsonProperty("residency_flag")
    private String residenceFlag;
    @JsonProperty("deceased")
    private Boolean deceased;
    @JsonProperty("dormant")
    private Boolean dormant;
    @JsonProperty("dormant_since_date")
    private String dormantSinceDate;
    @JsonProperty("is_customer_restricted")
    private Boolean isCustomerRestricted;
    @JsonProperty("email")
    private String email;
    @JsonProperty("external_source")
    private Boolean externalSource;
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
    @JsonProperty("updated_by")
    private String updatedBy;
    @JsonProperty("user_id")
    private String userId;
    @JsonProperty("partner_id")
    private String partnerId;
    @JsonProperty("partner_name")
    private String partnerName;
    @JsonProperty("update_flag")
    private String updateFlag;
}
