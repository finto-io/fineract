package io.finto.integration.fineract.usecase.impl.customer;

import io.finto.domain.customer.Customer;
import io.finto.domain.customer.CustomerId;
import io.finto.exceptions.core.FintoApiException;
import io.finto.fineract.sdk.models.GetClientsResponse;
import io.finto.integration.fineract.usecase.impl.SdkFineractUseCaseContext;
import io.finto.usecase.customer.FindCustomersUseCase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
@Builder
public class SdkFindCustomersUseCase implements FindCustomersUseCase {

    @NonNull
    private final SdkFineractUseCaseContext context;
    @NonNull
    private final Function<CustomerId, Customer> getCustomer;

    @Override
    public List<Customer> findCustomers() {
        String sqlSearch = null;
        Long officeId = null;
        String externalId = null;
        String displayName = null;
        String firstName = null;
        String lastName = null;
        String status = null;
        String underHierarchy = null;
        Integer offset = null;
        Integer limit = null;
        String orderBy = "id";
        String sortOrder = "asc";
        Boolean orphansOnly = null;

        GetClientsResponse customersResponse = context.getResponseBody(
                context.clientApi().retrieveAllClients(
                        sqlSearch, officeId, externalId,
                        displayName, firstName, lastName,
                        status, underHierarchy, offset,
                        limit, orderBy, sortOrder, orphansOnly
                )
        );

        // TODO: throw proper exc
        if (customersResponse.getPageItems() == null) {
            throw new FintoApiException();
        }

        List<CustomerId> customerIds = customersResponse.getPageItems().stream()
                .map(c -> CustomerId.of(c.getId()))
                .collect(Collectors.toList());
        return customerIds.stream()
                .map(getCustomer)
                .collect(Collectors.toList());
    }
}
