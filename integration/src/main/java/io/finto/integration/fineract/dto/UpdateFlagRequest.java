package io.finto.integration.fineract.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateFlagRequest {
    @JsonProperty("locale")
    private final String locale;
    @JsonProperty("dateFormat")
    private final String dateFormat;
    @JsonProperty("client_id")
    private final Long clientId;
    @JsonProperty("changed_by")
    private final String changedBy;
    @JsonProperty("changed_at")
    private final String changedAt;
    @JsonProperty("ttl")
    private final String ttl;
    @JsonProperty("active")
    private final Boolean active;
}