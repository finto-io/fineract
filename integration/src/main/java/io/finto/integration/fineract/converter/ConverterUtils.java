package io.finto.integration.fineract.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.finto.exceptions.core.FintoApiException;
import io.finto.integration.fineract.dto.CustomerAdditionalFieldsDto;

public class ConverterUtils {
    public static CustomerAdditionalFieldsDto parseAdditionalFields(ObjectMapper objectMapper, String content) {
        try {
            CustomerAdditionalFieldsDto[] customerAdditionalFieldsDtos = objectMapper.readValue(content, CustomerAdditionalFieldsDto[].class);
            return customerAdditionalFieldsDtos.length > 0 ? customerAdditionalFieldsDtos[0] : null;
        } catch (JsonProcessingException e) {
            throw new FintoApiException(e);
        }
    }
}
