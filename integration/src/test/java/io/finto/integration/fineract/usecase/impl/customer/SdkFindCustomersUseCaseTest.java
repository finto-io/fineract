package io.finto.integration.fineract.usecase.impl.customer;

import io.finto.domain.customer.Customer;
import io.finto.domain.customer.CustomerId;
import io.finto.domain.customer.CustomerStatus;
import io.finto.fineract.sdk.api.ClientApi;
import io.finto.fineract.sdk.models.GetClientsPageItemsResponse;
import io.finto.fineract.sdk.models.GetClientsResponse;
import io.finto.integration.fineract.converter.FineractCustomerMapper;
import io.finto.integration.fineract.usecase.impl.SdkFineractUseCaseContext;
import org.easymock.IMocksControl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Call;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;

class SdkFindCustomersUseCaseTest {

    private IMocksControl control;
    private SdkFineractUseCaseContext context;
    private Function<CustomerId, Customer> getCustomer;
    private Call<GetClientsResponse> executeCommandCall;
    private ClientApi clientApi;

    private SdkFindCustomersUseCase useCase;

    // params
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


    @BeforeEach
    void setUp() {
        control = createStrictControl();
        context = control.createMock(SdkFineractUseCaseContext.class);
        getCustomer = control.createMock(Function.class);
        executeCommandCall = control.createMock(Call.class);
        useCase = SdkFindCustomersUseCase.builder()
                .context(context)
                .getCustomer(getCustomer)
                .build();

        clientApi = control.createMock(ClientApi.class);
    }


    /**
     * Method under test: {@link SdkFindCustomersUseCase#findCustomers()}
     */
    @Test
    void test_findCustomers_emptyResponse() {
        GetClientsResponse response = GetClientsResponse.builder()
                .pageItems(new HashSet<>())
                .build();

        expect(context.clientApi()).andReturn(clientApi);
        expect(clientApi.retrieveAllClients(
                sqlSearch, officeId, externalId,
                displayName, firstName, lastName,
                status, underHierarchy, offset,
                limit, orderBy, sortOrder, orphansOnly
        )).andReturn(executeCommandCall);
        expect(context.getResponseBody(executeCommandCall)).andReturn(response);

        control.replay();

        List<Customer> actual = useCase.findCustomers();

        control.verify();

        Assertions.assertEquals(List.of(), actual);
    }

    /**
     * Method under test: {@link SdkFindCustomersUseCase#findCustomers()}
     */
    @Test
    void test_findCustomers_success() {
        Customer customer = Customer.builder().build();
        GetClientsPageItemsResponse clientsPageItemsResponse = GetClientsPageItemsResponse.builder()
                .id(1L)
                .build();
        Set<GetClientsPageItemsResponse> responseContent = new HashSet<>();
        responseContent.add(clientsPageItemsResponse);
        GetClientsResponse response = GetClientsResponse.builder()
                .pageItems(responseContent)
                .build();

        expect(context.clientApi()).andReturn(clientApi);
        expect(clientApi.retrieveAllClients(
                sqlSearch, officeId, externalId,
                displayName, firstName, lastName,
                status, underHierarchy, offset,
                limit, orderBy, sortOrder, orphansOnly
        )).andReturn(executeCommandCall);
        expect(context.getResponseBody(executeCommandCall)).andReturn(response);
        expect(getCustomer.apply(CustomerId.of(1L))).andReturn(customer);
        control.replay();

        List<Customer> actual = useCase.findCustomers();

        control.verify();

        Assertions.assertEquals(List.of(customer), actual);
    }
}