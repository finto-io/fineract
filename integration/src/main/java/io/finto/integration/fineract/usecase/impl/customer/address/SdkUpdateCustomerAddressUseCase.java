package io.finto.integration.fineract.usecase.impl.customer.address;

import io.finto.domain.customer.Address;
import io.finto.domain.customer.CustomerId;
import io.finto.domain.customer.UpdatingCustomer;
import io.finto.integration.fineract.converter.FineractAddressMapper;
import io.finto.integration.fineract.converter.FineractCustomerMapper;
import io.finto.integration.fineract.usecase.impl.SdkFineractUseCaseContext;
import io.finto.integration.fineract.utils.CustomerUtils;
import io.finto.usecase.customer.FindKeyValueDictionaryUseCase;
import io.finto.usecase.customer.address.CreateCustomerAddressUseCase;
import io.finto.usecase.customer.address.UpdateCustomerAddressUseCase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;

import static io.finto.fineract.sdk.Constants.ADDRESS_TYPE_DICTIONARY_ID;

@AllArgsConstructor
@Builder
public class SdkUpdateCustomerAddressUseCase implements UpdateCustomerAddressUseCase {

    @NonNull
    private final SdkFineractUseCaseContext context;
    @NonNull
    private final FineractCustomerMapper customerMapper;
    @NonNull
    private final FineractAddressMapper addressMapper;
    @NonNull
    private final CustomerUtils customerUtils;
    @NonNull
    private final FindKeyValueDictionaryUseCase dictionaryUseCase;
    @NonNull
    private final CreateCustomerAddressUseCase createAddressUseCase;

    public static class SdkUpdateCustomerAddressUseCaseBuilder {
        private FineractCustomerMapper customerMapper = FineractCustomerMapper.INSTANCE;
        private FineractAddressMapper addressMapper = FineractAddressMapper.INSTANCE;
        private CustomerUtils customerUtils = CustomerUtils.INSTANCE;
    }

    @Override
    public void updateAddresses(UpdatingCustomer updatingCustomer) {
        var newResidenceAddress = addressMapper.toResidenceAddressDomain(updatingCustomer);
        updateAddress(updatingCustomer.getCustomerId(), newResidenceAddress);

        var newWorkAddress = addressMapper.toWorkAddressDomain(updatingCustomer.getPersonalData().getProf());
        updateAddress(updatingCustomer.getCustomerId(), newWorkAddress);
    }

    @Override
    public void updateAddress(CustomerId customerId, Address newAddress){
        var addressTypeId = dictionaryUseCase.getOneKeyByValue(ADDRESS_TYPE_DICTIONARY_ID, newAddress.getType());
        var existedAddresses = context.getResponseBody(context.clientsAddressApi().getClientAddresses(customerId.getValue(), null, addressTypeId));

        var isAddressFound = false;
        for (var cur : existedAddresses){
            var oldAddress = customerMapper.toDomain(cur);
            if (customerUtils.getAddressComparator().compare(oldAddress, newAddress) == 0) {
                isAddressFound = true;
                break;
            }
        }
        if (!isAddressFound){
            createAddressUseCase.createAddress(customerId, newAddress);
        }
    }
}
