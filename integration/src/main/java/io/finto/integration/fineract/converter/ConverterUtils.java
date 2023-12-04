package io.finto.integration.fineract.converter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.finto.exceptions.core.FintoApiException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ConverterUtils {
    public static <T> T parseAdditionalFields(ObjectMapper objectMapper, String content, Class<T[]> valueType) {
        try {
            T[] customerAdditionalFieldsDtos = objectMapper.readValue(content, valueType);
            return customerAdditionalFieldsDtos.length > 0 ? customerAdditionalFieldsDtos[0] : null;
        } catch (JsonProcessingException e) {
            throw new FintoApiException(e);
        }
    }

    public static class ZonedDateTimeDeserializer extends JsonDeserializer<ZonedDateTime> {
        @Override
        public ZonedDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            int[] dateTimeValues = jsonParser.readValueAs(int[].class);

            int year = dateTimeValues[0];
            int month = dateTimeValues[1];
            int day = dateTimeValues[2];
            int hour = dateTimeValues[3];
            int minute = dateTimeValues[4];
            int second = dateTimeValues[5];
            int nanosecond = dateTimeValues[6];

            ZonedDateTime zonedDateTime = ZonedDateTime.of(year, month, day, hour, minute, second, nanosecond, ZoneId.systemDefault());

            return zonedDateTime.withZoneSameInstant(ZoneId.systemDefault());
        }
    }

    public static class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
        @Override
        public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            return LocalDateTime.parse(jsonParser.readValueAs(String.class), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S"));
        }
    }
}
