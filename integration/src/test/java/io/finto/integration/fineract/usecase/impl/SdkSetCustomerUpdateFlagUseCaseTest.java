package io.finto.integration.fineract.usecase.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.finto.domain.customer.CustomerId;
import io.finto.fineract.sdk.api.DataTablesApi;
import io.finto.fineract.sdk.models.PutDataTablesAppTableIdResponse;
import io.finto.integration.fineract.converter.FineractCustomerMapper;
import io.finto.integration.fineract.dto.CustomerDetailsUpdateDto;
import io.finto.integration.fineract.usecase.impl.customer.SdkSetCustomerUpdateFlagUseCase;
import org.easymock.Capture;
import org.easymock.IMocksControl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Call;

import java.time.LocalDateTime;

import static io.finto.fineract.sdk.CustomDatatableNames.CUSTOMER_ADDITIONAL_FIELDS;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;

class SdkSetCustomerUpdateFlagUseCaseTest {

    private SdkFineractUseCaseContext context;
    private FineractCustomerMapper customerMapper;
    private SdkSetCustomerUpdateFlagUseCase useCase;
    private ObjectMapper objectMapper;
    private IMocksControl control;
    private DataTablesApi dataTablesApi;
    private Call<PutDataTablesAppTableIdResponse> apiCall;
    private CustomerDetailsUpdateDto dto;


    @BeforeEach
    void setUp() {
        control = createStrictControl();
        context = control.createMock(SdkFineractUseCaseContext.class);
        customerMapper = control.createMock(FineractCustomerMapper.class);
        dataTablesApi = control.createMock(DataTablesApi.class);
        apiCall = control.createMock(Call.class);
        dto = control.createMock(CustomerDetailsUpdateDto.class);
        objectMapper = control.createMock(ObjectMapper.class);

        useCase = SdkSetCustomerUpdateFlagUseCase.builder()
                .context(context)
                .customerMapper(customerMapper)
                .objectMapper(objectMapper)
                .build();
    }

    @Test
    void testSetUpdateFlag() throws JsonProcessingException {
        CustomerId customerId = CustomerId.of(123L);
        boolean flag = true;
        String clientIp = "127.0.0.1";
        long ttlMilliseconds = 6000L;
        var stringRequest = "stringRequest";

        Capture<LocalDateTime> timestamp = Capture.newInstance();
        Capture<LocalDateTime> ttl = Capture.newInstance();

        expect(context.dataTablesApi()).andReturn(dataTablesApi);
        expect(customerMapper.toUpdateFlagRequestDto(eq(flag), eq(clientIp), capture(timestamp), capture(ttl))).andReturn(dto);
        expect(objectMapper.writeValueAsString(dto)).andReturn(stringRequest);
        expect(dataTablesApi.updateDatatableEntryOnetoOne(CUSTOMER_ADDITIONAL_FIELDS, customerId.getValue(), stringRequest)).andReturn(apiCall);
        expect(context.getResponseBody(apiCall)).andReturn(null);

        control.replay();

        useCase.setUpdateFlag(customerId, flag, clientIp, ttlMilliseconds);

        control.verify();
    }

}