package io.finto.integration.fineract.usecase.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.finto.domain.customer.CustomerAdditionalFields;
import io.finto.domain.customer.CustomerDetailsUpdate;
import io.finto.domain.customer.CustomerId;
import io.finto.fineract.sdk.api.DataTablesApi;
import io.finto.fineract.sdk.models.PutDataTablesAppTableIdResponse;
import io.finto.integration.fineract.converter.FineractCustomerMapper;
import io.finto.integration.fineract.dto.CustomerDetailsUpdateDto;
import io.finto.integration.fineract.usecase.impl.customer.SdkEnrichCustomerInfoUseCase;
import org.easymock.IMocksControl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Call;

import java.util.function.Function;

import static io.finto.fineract.sdk.CustomDatatableNames.CUSTOMER_ADDITIONAL_FIELDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;

class SdkEnrichCustomerInfoUseCaseTest {

    private IMocksControl control;
    private SdkFineractUseCaseContext context;
    private ObjectMapper objectMapper;
    private FineractCustomerMapper customerMapper;
    private CustomerId customerId = CustomerId.of(10L);
    private CustomerAdditionalFields customerAdditionalFields;
    private CustomerDetailsUpdate request;
    private CustomerDetailsUpdateDto dto;
    private Function<CustomerId, CustomerAdditionalFields> customerFinder;

    private DataTablesApi dataTablesApi;

    private SdkEnrichCustomerInfoUseCase useCase;

    @BeforeEach
    void setUp() {
        control = createStrictControl();
        request = control.createMock(CustomerDetailsUpdate.class);
        dto = control.createMock(CustomerDetailsUpdateDto.class);
        customerAdditionalFields = control.createMock(CustomerAdditionalFields.class);
        customerFinder = control.createMock(Function.class);
        context = control.createMock(SdkFineractUseCaseContext.class);
        objectMapper = control.createMock(ObjectMapper.class);
        customerMapper = control.createMock(FineractCustomerMapper.class);
        useCase = SdkEnrichCustomerInfoUseCase.builder()
                .context(context)
                .objectMapper(objectMapper)
                .getCustomerAdditionalFields(customerFinder)
                .customerMapper(customerMapper)
                .build();

        dataTablesApi = control.createMock(DataTablesApi.class);
    }

    /**
     * Method under test: {@link SdkEnrichCustomerInfoUseCase#saveAdditionalFields(CustomerId, CustomerDetailsUpdate)}
     */
    @Test
    void test_saveAdditionalFields_success() throws JsonProcessingException {

        Call<PutDataTablesAppTableIdResponse> callDataTables = control.createMock(Call.class);

        expect(customerMapper.toCustomerDetailsUpdateDto(request)).andReturn(dto);
        expect(context.dataTablesApi()).andReturn(dataTablesApi);
        expect(objectMapper.writeValueAsString(dto)).andReturn("testBody");
        expect(dataTablesApi.updateDatatableEntryOnetoOne(CUSTOMER_ADDITIONAL_FIELDS, customerId.getValue(), "testBody"))
                .andReturn(callDataTables);
        expect(context.getResponseBody(callDataTables)).andReturn(null);
        expect(customerFinder.apply(customerId)).andReturn(customerAdditionalFields);
        control.replay();

        var actual = useCase.saveAdditionalFields(customerId, request);
        control.verify();

        assertThat(actual).isSameAs(customerAdditionalFields);
    }

}