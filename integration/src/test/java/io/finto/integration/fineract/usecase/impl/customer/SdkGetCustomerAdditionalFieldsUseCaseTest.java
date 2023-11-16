package io.finto.integration.fineract.usecase.impl.customer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.finto.domain.customer.Customer;
import io.finto.domain.customer.CustomerAdditionalFields;
import io.finto.domain.customer.CustomerId;
import io.finto.domain.customer.CustomerStatus;
import io.finto.fineract.sdk.api.ClientApi;
import io.finto.fineract.sdk.api.DataTablesApi;
import io.finto.fineract.sdk.models.GetClientsClientIdResponse;
import io.finto.integration.fineract.converter.ConverterUtils;
import io.finto.integration.fineract.converter.FineractCustomerMapper;
import io.finto.integration.fineract.dto.CustomerAdditionalFieldsDto;
import io.finto.integration.fineract.usecase.impl.SdkFineractUseCaseContext;
import org.easymock.IMocksControl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Call;

import static io.finto.fineract.sdk.CustomDatatableNames.CUSTOMER_ADDITIONAL_FIELDS;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.anyString;
import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SdkGetCustomerAdditionalFieldsUseCaseTest {

    private IMocksControl control;
    private SdkFineractUseCaseContext context;
    private FineractCustomerMapper customerMapper;
    private ObjectMapper objectMapper;

    private SdkGetCustomerAdditionalFieldsUseCase useCase;

    @BeforeEach
    void setUp() {
        control = createStrictControl();
        context = control.createMock(SdkFineractUseCaseContext.class);
        customerMapper = control.createMock(FineractCustomerMapper.class);
        objectMapper = control.createMock(ObjectMapper.class);
        useCase = SdkGetCustomerAdditionalFieldsUseCase.builder()
                .customerMapper(customerMapper)
                .context(context)
                .objectMapper(objectMapper)
                .build();

    }

    /**
     * Method under test: {@link SdkGetCustomerAdditionalFieldsUseCase#getCustomerAdditionalFields(CustomerId)}
     */
    @Test
    void test_getCustomerAdditionalFields() throws JsonProcessingException {
        CustomerId customerId = CustomerId.of(13L);
        DataTablesApi datatablesApiMock = control.createMock(DataTablesApi.class);
        expect(context.dataTablesApi())
                .andReturn(datatablesApiMock);
        Call<String> datatablesApiCallMock = control.createMock(Call.class);
        expect(datatablesApiMock.getDatatableByAppTableId("customer_fields", customerId.getValue(), null, null))
                .andReturn(datatablesApiCallMock);
        String datatablesApiResponse = "";
        expect(context.getResponseBody(datatablesApiCallMock))
                .andReturn(datatablesApiResponse);
        expect(objectMapper.readValue(anyString(), anyObject(Class.class)))
                .andReturn(new CustomerAdditionalFieldsDto[0]);
        CustomerAdditionalFields expected = CustomerAdditionalFields.builder().build();
        expect(customerMapper.toAdditionalFields(null))
                .andReturn(expected);

        control.replay();
        CustomerAdditionalFields actual = useCase.getCustomerAdditionalFields(customerId);
        control.verify();

        assertEquals(expected, actual);
    }
}