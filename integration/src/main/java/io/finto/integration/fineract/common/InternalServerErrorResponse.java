package io.finto.integration.fineract.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class InternalServerErrorResponse {

    private OffsetDateTime timestamp;
    private Integer status;
    private String error;
    private String path;

    @Override
    public String toString() {
        return timestamp + ". " + error + " for path [" + path + "] with status [" + status + "]";
    }

}
