package io.finto.integration.fineract.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.finto.fineract.sdk.models.CommonErrorResponse;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class InternalServerErrorResponseTest {

    private final ObjectMapper mapper = JsonMapper.builder().findAndAddModules().build();

    @Test
    void testDeserialization() throws JsonProcessingException {
        String source = "{\"timestamp\":\"2023-06-01T10:23:00.688+00:00\",\"status\":500,\"error\":\"Internal Server Error\",\"path\":\"/fineract-provider/api/v1/savingsaccounts/176\"}";
        InternalServerErrorResponse actual = mapper.readValue(source, InternalServerErrorResponse.class);
        InternalServerErrorResponse expected = InternalServerErrorResponse.builder()
                .error("Internal Server Error")
                .timestamp(OffsetDateTime.parse("2023-06-01T10:23:00.688+00:00"))
                .status(500)
                .path("/fineract-provider/api/v1/savingsaccounts/176")
                .build();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testAsFailback() throws JsonProcessingException {
        String source = "{\"timestamp\":\"2023-06-01T10:23:00.688+00:00\",\"status\":500,\"error\":\"Internal Server Error\",\"path\":\"/fineract-provider/api/v1/savingsaccounts/176\"}";
        try {
            mapper.readValue(source, CommonErrorResponse.class);
        } catch (JsonProcessingException exception) {
            InternalServerErrorResponse actual = mapper.readValue(source, InternalServerErrorResponse.class);
            InternalServerErrorResponse expected = InternalServerErrorResponse.builder()
                    .error("Internal Server Error")
                    .timestamp(OffsetDateTime.parse("2023-06-01T10:23:00.688+00:00"))
                    .status(500)
                    .path("/fineract-provider/api/v1/savingsaccounts/176")
                    .build();

            assertThat(actual).isEqualTo(expected);
        }
    }

}