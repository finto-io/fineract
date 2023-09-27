package io.finto.integration.fineract.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateFlagResponse {
    @JsonProperty("client_id")
    private Long clientId;
    @JsonProperty("changed_by")
    private String changedBy;
    @JsonProperty("changed_at")
    private LocalDateTime changedAt;
    @JsonProperty("ttl")
    private LocalDateTime ttl;
    @JsonProperty("active")
    private Boolean active;
}