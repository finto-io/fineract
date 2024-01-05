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
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoanProductDetailsDto {
    @JsonProperty("product_loan_id")
    private Long productLoanId;
    @JsonProperty("dateFormat")
    private String dateFormat;
    @JsonProperty("locale")
    private String locale;
    @JsonProperty("partner_id")
    private String partnerId;
    @JsonProperty("partner_name")
    private String partnerName;
    @JsonProperty("external_id")
    private String externalId;
    @JsonProperty("late_payment_block_user")
    private Boolean latePaymentBlockUser;
    @JsonProperty("early_settlement_allowed")
    private Boolean earlySettlementAllowed;
    @JsonProperty("loaded_at")
    private LocalDateTime loadedAt;
    @JsonProperty("loaded_by")
    private String loadedBy;
    @JsonProperty("closed_at")
    private LocalDateTime closedAt;
    @JsonProperty("closed_by")
    private String closedBy;
    @JsonProperty("modified_at")
    private LocalDateTime modifiedAt;
    @JsonProperty("modified_by")
    private String modifiedBy;
    @JsonProperty("first_payment_day")
    private Integer firstPaymentDay;
}
