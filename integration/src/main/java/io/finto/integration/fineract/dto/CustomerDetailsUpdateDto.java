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

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerDetailsUpdateDto {
    @JsonProperty("external_customer_number")
    private String externalCustomerNumber;
    @JsonProperty("external_source")
    private Boolean externalSource;
    @JsonProperty("modified_at")
    private String updatedAt;
    @JsonProperty("modified_by")
    private String updatedBy;
    @JsonProperty("dateFormat")
    private String dateFormat;
    @JsonProperty("locale")
    private String locale;
    @JsonProperty("update_flag")
    private String updateFlag;

}
