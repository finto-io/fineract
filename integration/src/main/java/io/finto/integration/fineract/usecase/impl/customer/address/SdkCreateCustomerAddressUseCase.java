package io.finto.integration.fineract.usecase.impl.customer.address;

import io.finto.domain.customer.Address;
import io.finto.domain.customer.CustomerId;
import io.finto.exceptions.core.generic.BadRequestException;
import io.finto.integration.fineract.converter.FineractAddressMapper;
import io.finto.integration.fineract.converter.FineractCustomerMapper;
import io.finto.integration.fineract.usecase.impl.SdkFineractUseCaseContext;
import io.finto.usecase.customer.FindKeyValueDictionaryUseCase;
import io.finto.usecase.customer.address.CreateCustomerAddressUseCase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;

import static io.finto.fineract.sdk.Constants.ADDRESS_TYPE_DICTIONARY_ID;
import static io.finto.fineract.sdk.Constants.COUNTRY_DICTIONARY_ID;

@AllArgsConstructor
@Builder
public class SdkCreateCustomerAddressUseCase implements CreateCustomerAddressUseCase {

    @NonNull
    private final SdkFineractUseCaseContext context;
    @NonNull
    private final FineractCustomerMapper customerMapper;
    @NonNull
    private final FineractAddressMapper addressMapper;
    @NonNull
    private final FindKeyValueDictionaryUseCase dictionaryUseCase;

    public static class SdkCreateCustomerAddressUseCaseBuilder {
        private FineractCustomerMapper customerMapper = FineractCustomerMapper.INSTANCE;
        private FineractAddressMapper addressMapper = FineractAddressMapper.INSTANCE;
    }

    @Override
    public void createAddress(CustomerId customerId, Address address) {
        var addressTypeId = dictionaryUseCase.getOneKeyByValue(ADDRESS_TYPE_DICTIONARY_ID, address.getType());
        var countryId = address.getCountry() == null ? null : dictionaryUseCase.findOneKeyByValue(COUNTRY_DICTIONARY_ID, address.getCountry())
                .orElseThrow(() -> new BadRequestException(BadRequestException.DEFAULT_ERROR_CODE, String.format("Country with name=%s not found", address.getCountry())));
        var postalCodeId = address.getPostalCode() == null ? null : customerMapper.toPostalCodeId(address.getPostalCode());
        var request = addressMapper.toCreateAddressDto(address, countryId, postalCodeId);
        context.getResponseBody(context.clientsAddressApi().addClientAddress(customerId.getValue(), request, addressTypeId));
    }
}
