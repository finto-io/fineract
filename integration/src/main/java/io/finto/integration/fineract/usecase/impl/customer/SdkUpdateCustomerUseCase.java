package io.finto.integration.fineract.usecase.impl.customer;

import io.finto.domain.customer.UpdatingCustomer;
import io.finto.exceptions.core.generic.BadRequestException;
import io.finto.integration.fineract.converter.FineractCustomerMapper;
import io.finto.integration.fineract.usecase.impl.SdkFineractUseCaseContext;
import io.finto.integration.fineract.utils.CustomerUtils;
import io.finto.usecase.customer.EnrichCustomerInfoUseCase;
import io.finto.usecase.customer.FindCustomerUseCase;
import io.finto.usecase.customer.FindKeyValueDictionaryUseCase;
import io.finto.usecase.customer.UpdateCustomerUseCase;
import io.finto.usecase.customer.address.UpdateCustomerAddressUseCase;
import io.finto.usecase.customer.identifier.UpdateCustomerIdentifiersUseCase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;

import static io.finto.fineract.sdk.Constants.GENDER_DICTIONARY_ID;

@AllArgsConstructor
@Builder
public class SdkUpdateCustomerUseCase implements UpdateCustomerUseCase {

    @NonNull
    private final SdkFineractUseCaseContext context;
    @NonNull
    private final FineractCustomerMapper customerMapper;
    @NonNull
    private final CustomerUtils customerUtils;
    @NonNull
    private final FindCustomerUseCase findCustomerUseCase;
    @NonNull
    private final UpdateCustomerAddressUseCase updateCustomerAddressUseCase;
    @NonNull
    private final UpdateCustomerIdentifiersUseCase updateCustomerIdentifiersUseCase;
    @NonNull
    private final EnrichCustomerInfoUseCase enrichCustomerInfoUseCase;
    @NonNull
    private final FindKeyValueDictionaryUseCase dictionaryUseCase;

    public static class SdkUpdateCustomerUseCaseBuilder {
        private FineractCustomerMapper customerMapper = FineractCustomerMapper.INSTANCE;
        private CustomerUtils customerUtils = CustomerUtils.INSTANCE;
    }

    @Override
    public void updateCustomer(UpdatingCustomer updatingCustomer) {
        var customerId = updatingCustomer.getCustomerId().getValue();
        var existedCustomer = findCustomerUseCase.findCustomer(updatingCustomer.getCustomerId());
        var newCustomer = customerMapper.toDomain(updatingCustomer);
        if (customerUtils.getCustomerComparator().compare(existedCustomer, newCustomer) != 0){
            var genderId = updatingCustomer.getPersonalData() == null || updatingCustomer.getPersonalData().getSex() == null ? null :
                    dictionaryUseCase.findOneKeyByValue(GENDER_DICTIONARY_ID, updatingCustomer.getPersonalData().getSex())
                            .orElseThrow(() -> new BadRequestException(BadRequestException.DEFAULT_ERROR_CODE, String.format("Sex with name=%s not found", updatingCustomer.getPersonalData().getSex())));
            context.getResponseBody(context.clientApi().updateClient(customerId, customerMapper.toClientUpdateRequest(updatingCustomer, genderId)));
        }
        updateCustomerAddressUseCase.updateAddresses(updatingCustomer);
        updateCustomerIdentifiersUseCase.updateCustomerIdentifiers(updatingCustomer);
        enrichCustomerInfoUseCase.saveAdditionalFields(updatingCustomer.getCustomerId(), customerMapper.toCustomerDetailsUpdateDomain(updatingCustomer));
    }
}
