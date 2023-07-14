package io.finto.integration.fineract.usecase.impl.customer;

import io.finto.domain.customer.CustomerId;
import io.finto.domain.customer.OpeningCustomer;
import io.finto.exceptions.core.generic.BadRequestException;
import io.finto.fineract.sdk.models.PostClientsRequest;
import io.finto.integration.fineract.converter.FineractCustomerMapper;
import io.finto.integration.fineract.usecase.impl.SdkFineractUseCaseContext;
import io.finto.usecase.customer.CreateCustomerUseCase;
import io.finto.usecase.customer.FindKeyValueDictionaryUseCase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;

import static io.finto.fineract.sdk.Constants.*;

@AllArgsConstructor
@Builder
public class SdkCreateCustomerUseCase implements CreateCustomerUseCase {

    @NonNull
    private final SdkFineractUseCaseContext context;
    @NonNull
    private final FineractCustomerMapper customerMapper;
    @NonNull
    private final FindKeyValueDictionaryUseCase dictionaryUseCase;

    public static class SdkCreateCustomerUseCaseBuilder {
        private FineractCustomerMapper customerMapper = FineractCustomerMapper.INSTANCE;
    }

    @Override
    public CustomerId createCustomer(OpeningCustomer openingCustomer) {
        var countryId = openingCustomer.getCountry() == null ? null : dictionaryUseCase.findOneKeyByValue(COUNTRY_DICTIONARY_ID, openingCustomer.getCountry())
                .orElseThrow(() -> new BadRequestException(String.format(BadRequestException.DEFAULT_ERROR_CODE, "Country with name=%s not found", openingCustomer.getCountry())));
        var profCountryId = openingCustomer.getPersonalData() == null || openingCustomer.getPersonalData().getProf() == null || openingCustomer.getPersonalData().getProf().getCountry() == null ? null :
                dictionaryUseCase.findOneKeyByValue(COUNTRY_DICTIONARY_ID, openingCustomer.getPersonalData().getProf().getCountry())
                        .orElseThrow(() -> new BadRequestException(String.format(BadRequestException.DEFAULT_ERROR_CODE, "Professional Address Country with name=%s not found", openingCustomer.getPersonalData().getProf().getCountry())));
        var postalCodeId = openingCustomer.getPostalCode() == null ? null : customerMapper.toPostalCodeId(openingCustomer.getPostalCode());
        var genderId = openingCustomer.getPersonalData() == null || openingCustomer.getPersonalData().getSex() == null ? null :
                dictionaryUseCase.findOneKeyByValue(GENDER_DICTIONARY_ID, openingCustomer.getPersonalData().getSex())
                        .orElseThrow(() -> new BadRequestException(String.format(BadRequestException.DEFAULT_ERROR_CODE, "Sex with name=%s not found", openingCustomer.getPersonalData().getSex())));
        var residenceAddressId = dictionaryUseCase.getOneKeyByValue(ADDRESS_TYPE_DICTIONARY_ID, RESIDENCE_ADDRESS_CODE_NAME);
        var workAddressId = dictionaryUseCase.getOneKeyByValue(ADDRESS_TYPE_DICTIONARY_ID, WORK_ADDRESS_CODE_NAME);

        PostClientsRequest postClientsRequest = customerMapper.toOpeningCustomerDto(openingCustomer, genderId, countryId, profCountryId, residenceAddressId, workAddressId, postalCodeId);
        var call = context.clientApi().createClient(postClientsRequest);
        return CustomerId.of(context.getResponseBody(call).getClientId());
    }
}
