package io.finto.integration.fineract.usecase.impl.customer.address;

import io.finto.domain.customer.*;
import io.finto.fineract.sdk.api.ClientsAddressApi;
import io.finto.fineract.sdk.models.GetClientClientIdAddressesResponse;
import io.finto.integration.fineract.converter.FineractAddressMapper;
import io.finto.integration.fineract.converter.FineractCustomerMapper;
import io.finto.integration.fineract.usecase.impl.SdkFineractUseCaseContext;
import io.finto.integration.fineract.utils.CustomerUtils;
import io.finto.usecase.customer.FindKeyValueDictionaryUseCase;
import io.finto.usecase.customer.address.CreateCustomerAddressUseCase;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Call;

import java.util.Comparator;
import java.util.List;

import static io.finto.fineract.sdk.Constants.ADDRESS_TYPE_DICTIONARY_ID;
import static org.easymock.EasyMock.*;

class SdkUpdateCustomerAddressUseCaseTest {

    private IMocksControl control;
    private SdkFineractUseCaseContext context;
    private SdkUpdateCustomerAddressUseCase useCase;
    private FineractCustomerMapper customerMapper;
    private FineractAddressMapper addressMapper;
    private CustomerUtils customerUtils;
    private FindKeyValueDictionaryUseCase dictionaryUseCase;
    private CreateCustomerAddressUseCase createAddressUseCase;
    private CustomerId customerId = CustomerId.of(10L);
    private ClientsAddressApi clientsAddressApi;
    private Call call;
    private Comparator comparator;
    private Address address;
    private Address oldAddress;
    private GetClientClientIdAddressesResponse response;

    @BeforeEach
    void setUp() {
        control = createStrictControl();
        context = control.createMock(SdkFineractUseCaseContext.class);
        clientsAddressApi = control.createMock(ClientsAddressApi.class);
        dictionaryUseCase = control.createMock(FindKeyValueDictionaryUseCase.class);
        customerMapper = control.createMock(FineractCustomerMapper.class);
        addressMapper = control.createMock(FineractAddressMapper.class);
        customerUtils = control.createMock(CustomerUtils.class);
        createAddressUseCase = control.createMock(CreateCustomerAddressUseCase.class);
        useCase = SdkUpdateCustomerAddressUseCase.builder()
                .context(context)
                .customerMapper(customerMapper)
                .addressMapper(addressMapper)
                .customerUtils(customerUtils)
                .dictionaryUseCase(dictionaryUseCase)
                .createAddressUseCase(createAddressUseCase)
                .build();
        call = control.createMock(Call.class);
        comparator = control.createMock(Comparator.class);
        address = control.createMock(Address.class);
        oldAddress = control.createMock(Address.class);
        response = control.createMock(GetClientClientIdAddressesResponse.class);
    }

    @Test
    void updateAddresses() {
        SdkUpdateCustomerAddressUseCase useCase = EasyMock.createMockBuilder(SdkUpdateCustomerAddressUseCase.class)
                .addMockedMethod("updateAddress")
                .withConstructor(context, customerMapper, addressMapper, customerUtils, dictionaryUseCase, createAddressUseCase)
                .createMock();
        UpdatingCustomer updatingCustomer = control.createMock(UpdatingCustomer.class);
        PersonalData personalData = control.createMock(PersonalData.class);
        Profession prof = control.createMock(Profession.class);
        Address newResidenceAddress = control.createMock(Address.class);
        Address newWorkAddress = control.createMock(Address.class);

        expect(updatingCustomer.getCustomerId()).andStubReturn(customerId);
        expect(addressMapper.toResidenceAddressDomain(updatingCustomer)).andReturn(newResidenceAddress);
        useCase.updateAddress(customerId, newResidenceAddress);
        expectLastCall();

        expect(updatingCustomer.getPersonalData()).andReturn(personalData);
        expect(personalData.getProf()).andReturn(prof);
        expect(addressMapper.toWorkAddressDomain(prof)).andReturn(newWorkAddress);
        useCase.updateAddress(customerId, newWorkAddress);
        control.replay();

        useCase.updateAddresses(updatingCustomer);
        control.verify();
    }

    @Test
    void updateAddress_noUpdate() {
        Long addressTypeId = 1L;
        expect(address.getType()).andStubReturn("type");
        expect(dictionaryUseCase.getOneKeyByValue(ADDRESS_TYPE_DICTIONARY_ID, "type")).andReturn(addressTypeId);
        expect(context.clientsAddressApi()).andReturn(clientsAddressApi);
        expect(clientsAddressApi.getClientAddresses(customerId.getValue(), null, addressTypeId)).andReturn(call);
        expect(context.getResponseBody(call)).andReturn(List.of(response));
        expect(customerMapper.toDomain(response)).andReturn(oldAddress);
        expect(customerUtils.getAddressComparator()).andReturn(comparator);
        expect(comparator.compare(oldAddress, address)).andReturn(0);
        control.replay();

        useCase.updateAddress(customerId, address);
        control.verify();

    }

    @Test
    void updateAddress_NeedUpdate() {
        Long addressTypeId = 1L;
        expect(address.getType()).andStubReturn("type");
        expect(dictionaryUseCase.getOneKeyByValue(ADDRESS_TYPE_DICTIONARY_ID, "type")).andReturn(addressTypeId);
        expect(context.clientsAddressApi()).andReturn(clientsAddressApi);
        expect(clientsAddressApi.getClientAddresses(customerId.getValue(), null, addressTypeId)).andReturn(call);
        expect(context.getResponseBody(call)).andReturn(List.of(response));
        expect(customerMapper.toDomain(response)).andReturn(oldAddress);
        expect(customerUtils.getAddressComparator()).andReturn(comparator);
        expect(comparator.compare(oldAddress, address)).andReturn(1);
        createAddressUseCase.createAddress(customerId, address);
        expectLastCall();
        control.replay();

        useCase.updateAddress(customerId, address);
        control.verify();

    }
}