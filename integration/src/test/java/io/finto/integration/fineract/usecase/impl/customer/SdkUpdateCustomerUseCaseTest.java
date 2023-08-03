package io.finto.integration.fineract.usecase.impl.customer;

import io.finto.domain.customer.Customer;
import io.finto.domain.customer.CustomerDetailsUpdate;
import io.finto.domain.customer.CustomerId;
import io.finto.domain.customer.PersonalData;
import io.finto.domain.customer.UpdatingCustomer;
import io.finto.exceptions.core.generic.BadRequestException;
import io.finto.fineract.sdk.api.ClientApi;
import io.finto.fineract.sdk.models.PutClientsClientIdRequest;
import io.finto.integration.fineract.converter.FineractCustomerMapper;
import io.finto.integration.fineract.usecase.impl.SdkFineractUseCaseContext;
import io.finto.integration.fineract.utils.CustomerUtils;
import io.finto.usecase.customer.EnrichCustomerInfoUseCase;
import io.finto.usecase.customer.FindCustomerUseCase;
import io.finto.usecase.customer.FindKeyValueDictionaryUseCase;
import io.finto.usecase.customer.address.UpdateCustomerAddressUseCase;
import io.finto.usecase.customer.identifier.UpdateCustomerIdentifiersUseCase;
import org.easymock.IMocksControl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Call;

import java.util.Comparator;
import java.util.Optional;

import static io.finto.fineract.sdk.Constants.GENDER_DICTIONARY_ID;
import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SdkUpdateCustomerUseCaseTest {

    private IMocksControl control;
    private SdkFineractUseCaseContext context;
    private FineractCustomerMapper customerMapper;
    private CustomerUtils customerUtils;
    private FindCustomerUseCase findCustomerUseCase;
    private UpdateCustomerAddressUseCase updateCustomerAddressUseCase;
    private UpdateCustomerIdentifiersUseCase updateCustomerIdentifiersUseCase;
    private EnrichCustomerInfoUseCase enrichCustomerInfoUseCase;
    private FindKeyValueDictionaryUseCase dictionaryUseCase;
    private SdkUpdateCustomerUseCase useCase;

    private CustomerId customerId = CustomerId.of(10L);
    private Long genderId = 1L;
    private ClientApi clientApi;
    private Call call;
    private Customer existedCustomer;
    private Customer newCustomer;
    private Comparator comparator;
    private PutClientsClientIdRequest requestDto;
    private CustomerDetailsUpdate customerDetailsUpdate;
    @BeforeEach
    void setUp() {
        control = createStrictControl();
        context = control.createMock(SdkFineractUseCaseContext.class);
        customerMapper = control.createMock(FineractCustomerMapper.class);
        customerUtils = control.createMock(CustomerUtils.class);
        updateCustomerAddressUseCase = control.createMock(UpdateCustomerAddressUseCase.class);
        updateCustomerIdentifiersUseCase = control.createMock(UpdateCustomerIdentifiersUseCase.class);
        enrichCustomerInfoUseCase = control.createMock(EnrichCustomerInfoUseCase.class);
        dictionaryUseCase = control.createMock(FindKeyValueDictionaryUseCase.class);
        findCustomerUseCase = control.createMock(FindCustomerUseCase.class);
        useCase = new SdkUpdateCustomerUseCase(context, customerMapper, customerUtils, findCustomerUseCase, updateCustomerAddressUseCase,
                updateCustomerIdentifiersUseCase, enrichCustomerInfoUseCase, dictionaryUseCase);

        clientApi = control.createMock(ClientApi.class);
        call = control.createMock(Call.class);
        existedCustomer = control.createMock(Customer.class);
        newCustomer = control.createMock(Customer.class);
        comparator = control.createMock(Comparator.class);
        requestDto = control.createMock(PutClientsClientIdRequest.class);
        customerDetailsUpdate = control.createMock(CustomerDetailsUpdate.class);
    }


    @Test
    void updateCustomer() {
        var updatingCustomer = UpdatingCustomer.builder()
                .customerId(customerId)
                .personalData(PersonalData.builder()
                        .sex("M")
                        .build())
                .build();
        expect(findCustomerUseCase.findCustomer(customerId)).andReturn(existedCustomer);
        expect(customerMapper.toDomain(updatingCustomer)).andReturn(newCustomer);
        expect(customerUtils.getCustomerComparator()).andReturn(comparator);
        expect(comparator.compare(existedCustomer, newCustomer)).andReturn(1);
        expect(dictionaryUseCase.findOneKeyByValue(GENDER_DICTIONARY_ID, "M")).andReturn(Optional.of(genderId));
        expect(context.clientApi()).andReturn(clientApi);
        expect(customerMapper.toClientUpdateRequest(updatingCustomer, genderId)).andReturn(requestDto);
        expect(clientApi.updateClient(customerId.getValue(), requestDto)).andReturn(call);
        expect(context.getResponseBody(call)).andReturn(null);
        updateCustomerAddressUseCase.updateAddresses(updatingCustomer);
        expectLastCall();
        updateCustomerIdentifiersUseCase.updateCustomerIdentifiers(updatingCustomer);
        expectLastCall();
        expect(customerMapper.toCustomerDetailsUpdateDomain(updatingCustomer)).andReturn(customerDetailsUpdate);
        expect(enrichCustomerInfoUseCase.saveAdditionalFields(customerId, customerDetailsUpdate)).andReturn(null);
        control.replay();

        useCase.updateCustomer(updatingCustomer);
        control.verify();
    }

    @Test
    void updateCustomer_EmptyGender() {
        var updatingCustomer = UpdatingCustomer.builder()
                .customerId(customerId)
                .build();
        expect(findCustomerUseCase.findCustomer(customerId)).andReturn(existedCustomer);
        expect(customerMapper.toDomain(updatingCustomer)).andReturn(newCustomer);
        expect(customerUtils.getCustomerComparator()).andReturn(comparator);
        expect(comparator.compare(existedCustomer, newCustomer)).andReturn(1);
        expect(context.clientApi()).andReturn(clientApi);
        expect(customerMapper.toClientUpdateRequest(updatingCustomer, null)).andReturn(requestDto);
        expect(clientApi.updateClient(customerId.getValue(), requestDto)).andReturn(call);
        expect(context.getResponseBody(call)).andReturn(null);
        updateCustomerAddressUseCase.updateAddresses(updatingCustomer);
        expectLastCall();
        updateCustomerIdentifiersUseCase.updateCustomerIdentifiers(updatingCustomer);
        expectLastCall();
        expect(customerMapper.toCustomerDetailsUpdateDomain(updatingCustomer)).andReturn(customerDetailsUpdate);
        expect(enrichCustomerInfoUseCase.saveAdditionalFields(customerId, customerDetailsUpdate)).andReturn(null);
        control.replay();

        useCase.updateCustomer(updatingCustomer);
        control.verify();
    }

    @Test
    void updateCustomer_nothingForUpdate() {
        var updatingCustomer = UpdatingCustomer.builder()
                .customerId(customerId)
                .personalData(PersonalData.builder()
                        .sex("M")
                        .build())
                .build();
        expect(findCustomerUseCase.findCustomer(customerId)).andReturn(existedCustomer);
        expect(customerMapper.toDomain(updatingCustomer)).andReturn(newCustomer);
        expect(customerUtils.getCustomerComparator()).andReturn(comparator);
        expect(comparator.compare(existedCustomer, newCustomer)).andReturn(0);
        updateCustomerAddressUseCase.updateAddresses(updatingCustomer);
        expectLastCall();
        updateCustomerIdentifiersUseCase.updateCustomerIdentifiers(updatingCustomer);
        expectLastCall();
        expect(customerMapper.toCustomerDetailsUpdateDomain(updatingCustomer)).andReturn(customerDetailsUpdate);
        expect(enrichCustomerInfoUseCase.saveAdditionalFields(customerId, customerDetailsUpdate)).andReturn(null);
        control.replay();

        useCase.updateCustomer(updatingCustomer);
        control.verify();
    }

    @Test
    void updateCustomer_badGender() {
        var updatingCustomer = UpdatingCustomer.builder()
                .customerId(customerId)
                .personalData(PersonalData.builder()
                        .sex("them")
                        .build())
                .build();
        expect(findCustomerUseCase.findCustomer(customerId)).andReturn(existedCustomer);
        expect(customerMapper.toDomain(updatingCustomer)).andReturn(newCustomer);
        expect(customerUtils.getCustomerComparator()).andReturn(comparator);
        expect(comparator.compare(existedCustomer, newCustomer)).andReturn(1);
        expect(dictionaryUseCase.findOneKeyByValue(GENDER_DICTIONARY_ID, "them")).andReturn(Optional.empty());
        control.replay();

        assertThrows(BadRequestException.class, () -> useCase.updateCustomer(updatingCustomer));
        control.verify();
    }

}