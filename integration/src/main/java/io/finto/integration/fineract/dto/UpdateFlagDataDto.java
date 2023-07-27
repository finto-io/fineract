package io.finto.integration.fineract.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
public class UpdateFlagDataDto {
    private String changedBy;
    private String changedAt;
    private String ttl;
    private boolean active;
}