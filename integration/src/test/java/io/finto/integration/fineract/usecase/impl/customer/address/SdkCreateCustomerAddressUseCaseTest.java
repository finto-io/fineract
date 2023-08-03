package io.finto.integration.fineract.usecase.impl.customer.address;

import io.finto.domain.customer.Address;
import io.finto.domain.customer.CustomerId;
import io.finto.exceptions.core.generic.BadRequestException;
import io.finto.fineract.sdk.api.ClientsAddressApi;
import io.finto.fineract.sdk.models.PostClientClientIdAddressesRequest;
import io.finto.integration.fineract.converter.FineractAddressMapper;
import io.finto.integration.fineract.converter.FineractCustomerMapper;
import io.finto.integration.fineract.usecase.impl.SdkFineractUseCaseContext;
import io.finto.usecase.customer.FindKeyValueDictionaryUseCase;
import org.easymock.IMocksControl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Call;

import java.util.Optional;

import static io.finto.fineract.sdk.Constants.ADDRESS_TYPE_DICTIONARY_ID;
import static io.finto.fineract.sdk.Constants.COUNTRY_DICTIONARY_ID;
import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SdkCreateCustomerAddressUseCaseTest {

    private IMocksControl control;
    private SdkFineractUseCaseContext context;
    private SdkCreateCustomerAddressUseCase useCase;
    private FineractCustomerMapper customerMapper;
    private FineractAddressMapper addressMapper;
    private FindKeyValueDictionaryUseCase dictionaryUseCase;
    private CustomerId customerId = CustomerId.of(10L);
    private ClientsAddressApi clientsAddressApi;
    private Call call;
    private PostClientClientIdAddressesRequest request;

    @BeforeEach
    void setUp() {
        control = createStrictControl();
        context = control.createMock(SdkFineractUseCaseContext.class);
        clientsAddressApi = control.createMock(ClientsAddressApi.class);
        dictionaryUseCase = control.createMock(FindKeyValueDictionaryUseCase.class);
        customerMapper = control.createMock(FineractCustomerMapper.class);
        addressMapper = control.createMock(FineractAddressMapper.class);
        useCase = SdkCreateCustomerAddressUseCase.builder()
                .context(context)
                .customerMapper(customerMapper)
                .addressMapper(addressMapper)
                .dictionaryUseCase(dictionaryUseCase)
                .build();
        call = control.createMock(Call.class);
        request = control.createMock(PostClientClientIdAddressesRequest.class);
    }


    @Test
    void createAddress() {
        var countryId = 1L;
        var postalCodeId = 2L;
        var addressTypeId = 17L;
        var address = Address.builder()
                .type("type")
                .country("country")
                .postalCode("postalCode")
                .build();
        expect(dictionaryUseCase.getOneKeyByValue(ADDRESS_TYPE_DICTIONARY_ID, "type")).andReturn(addressTypeId);
        expect(dictionaryUseCase.findOneKeyByValue(COUNTRY_DICTIONARY_ID, "country")).andReturn(Optional.of(countryId));
        expect(customerMapper.toPostalCodeId("postalCode")).andReturn(postalCodeId);
        expect(addressMapper.toCreateAddressDto(address, countryId, postalCodeId)).andReturn(request);
        expect(context.clientsAddressApi()).andReturn(clientsAddressApi);
        expect(clientsAddressApi.addClientAddress(customerId.getValue(), request, addressTypeId)).andReturn(call);
        expect(context.getResponseBody(call)).andReturn(null);
        control.replay();

        useCase.createAddress(customerId, address);
        control.verify();
    }

    @Test
    void createAddress_emptyAddress() {
        var addressTypeId = 17L;
        var address = Address.builder()
                .type("type")
                .build();
        expect(dictionaryUseCase.getOneKeyByValue(ADDRESS_TYPE_DICTIONARY_ID, "type")).andReturn(addressTypeId);
        expect(addressMapper.toCreateAddressDto(address, null, null)).andReturn(request);
        expect(context.clientsAddressApi()).andReturn(clientsAddressApi);
        expect(clientsAddressApi.addClientAddress(customerId.getValue(), request, addressTypeId)).andReturn(call);
        expect(context.getResponseBody(call)).andReturn(null);
        control.replay();

        useCase.createAddress(customerId, address);
        control.verify();
    }

    @Test
    void createAddress_countryNotFound() {
        var addressTypeId = 17L;
        var address = Address.builder().type("type").country("country").build();
        expect(dictionaryUseCase.getOneKeyByValue(ADDRESS_TYPE_DICTIONARY_ID, "type")).andReturn(addressTypeId);
        expect(dictionaryUseCase.findOneKeyByValue(COUNTRY_DICTIONARY_ID, "country")).andReturn(Optional.empty());
        control.replay();

        assertThrows(BadRequestException.class, () -> useCase.createAddress(customerId, address));
        control.verify();
    }

}