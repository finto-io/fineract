package io.finto.integration.fineract.usecase.impl.account;

import io.finto.domain.account.Account;
import io.finto.domain.account.AccountId;
import io.finto.domain.customer.CustomerId;
import io.finto.domain.product.ProductId;
import io.finto.fineract.sdk.api.ClientApi;
import io.finto.fineract.sdk.models.GetClientsClientIdAccountsResponse;
import io.finto.fineract.sdk.models.GetClientsSavingsAccounts;
import io.finto.integration.fineract.usecase.impl.SdkFineractUseCaseContext;
import io.finto.integration.fineract.usecase.impl.account.SdkFindAccountUseCase;
import io.finto.integration.fineract.usecase.impl.account.SdkFindAccountsUseCase;
import io.finto.usecase.account.FindAccountUseCase;
import org.easymock.IMocksControl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import retrofit2.Call;

import java.util.List;

import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;

class SdkFindAccountsUseCaseTest {

    private IMocksControl control;
    private SdkFineractUseCaseContext context;
    private FindAccountUseCase findAccountUseCase;
    private Call<GetClientsClientIdAccountsResponse> apiCall;
    private ClientApi clientApi;
    private SdkFindAccountsUseCase useCase;

    @BeforeEach
    void setUp() {
        control = createStrictControl();
        context = control.createMock(SdkFineractUseCaseContext.class);
        findAccountUseCase = control.createMock(SdkFindAccountUseCase.class);
        clientApi = control.createMock(ClientApi.class);
        apiCall = control.createMock(Call.class);

        useCase = SdkFindAccountsUseCase.builder()
                .context(context)
                .findAccountUseCase(findAccountUseCase)
                .build();
    }

    /**
     * Method under test: {@link SdkFindAccountsUseCase#findAccounts(CustomerId)}
     */
    @DisplayName(value = "Get customer's accounts - empty accounts list")
    @Test
    void test_findAccounts_emptyAccounts() {
        CustomerId customerId = CustomerId.of(32L);

        expect(context.clientApi()).andReturn(clientApi);
        expect(clientApi.retrieveAssociatedAccounts(customerId.getValue(), null)).andReturn(apiCall);
        expect(context.getResponseBody(apiCall)).andReturn(new GetClientsClientIdAccountsResponse());
        control.replay();

        List<Account> actual = useCase.findAccounts(customerId);
        control.verify();

        Assertions.assertNotNull(actual);
        Assertions.assertTrue(actual.isEmpty());
    }

    /**
     * Method under test: {@link SdkFindAccountsUseCase#findAccounts(CustomerId)}
     */
    @DisplayName(value = "Get customer's accounts")
    @Test
    void test_findAccounts_success() {
        CustomerId customerId = CustomerId.of(31L);
        GetClientsClientIdAccountsResponse apiResponse = new GetClientsClientIdAccountsResponse();
        GetClientsSavingsAccounts account = new GetClientsSavingsAccounts();
        account.setId(23);
        apiResponse.addSavingsAccountsItem(account);

        expect(context.clientApi()).andReturn(clientApi);
        expect(clientApi.retrieveAssociatedAccounts(customerId.getValue(), null)).andReturn(apiCall);
        expect(context.getResponseBody(apiCall)).andReturn(apiResponse);
        expect(findAccountUseCase.findAccount(AccountId.of(account.getId()))).andReturn(Account.builder().id(AccountId.of(account.getId())).productId(ProductId.of(1)).build());
        control.replay();

        List<Account> actual = useCase.findAccounts(customerId);
        control.verify();

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(1, actual.size());
    }


}