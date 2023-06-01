package io.finto.integration.fineract.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AccountAdditionalFieldsTest {

    private final ObjectMapper mapper = JsonMapper.builder().findAndAddModules().build();

    @Test
    void test_serialization() throws JsonProcessingException {
        AccountAdditionalFields source = AccountAdditionalFields.builder().externalAccountNumber("123123132123").build();
        String actual = mapper.writeValueAsString(source);
        String expected = "{\"external_account_number\":\"123123132123\"}";

        assertThat(actual).isEqualTo(expected);
    }

}