package io.finto.integration.fineract.usecase.impl.loanproduct;

import io.finto.domain.bnpl.loan.Loan;
import io.finto.domain.id.CustomerInternalId;
import io.finto.domain.id.fineract.LoanId;
import io.finto.fineract.sdk.api.ClientApi;
import io.finto.fineract.sdk.models.GetClientsClientIdAccountsResponse;
import io.finto.fineract.sdk.models.GetClientsLoanAccounts;
import io.finto.integration.fineract.usecase.impl.SdkFineractUseCaseContext;
import org.easymock.IMocksControl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Call;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;

class SdkFindLoansUseCaseTest {
    private IMocksControl control;
    private SdkFineractUseCaseContext context;
    private SdkFindLoansUseCase useCase;
    private Loan loan;
    private final BiFunction<LoanId, Integer, Loan> loanResolver = (id, digitsAfterDecimal) -> loan;

    @BeforeEach
    void setUp() {
        control = createStrictControl();
        context = control.createMock(SdkFineractUseCaseContext.class);
        loan = control.createMock(Loan.class);
        useCase = SdkFindLoansUseCase.builder()
                .context(context)
                .findLoan(loanResolver)
                .build();
    }

    /**
     * Method under test: {@link SdkFindLoansUseCase#findLoans(CustomerInternalId, Integer)}
     */
    @Test
    void test_findLoans() {
        CustomerInternalId customerInternalId = control.createMock(CustomerInternalId.class);
        ClientApi clientApi = control.createMock(ClientApi.class);
        Call<GetClientsClientIdAccountsResponse> responseGetAccounts = control.createMock(Call.class);
        GetClientsClientIdAccountsResponse bodyGetAccounts = control.createMock(GetClientsClientIdAccountsResponse.class);
        Set<GetClientsLoanAccounts> loanAccounts = Set.of(GetClientsLoanAccounts.builder().id(2L).build());

        expect(context.clientApi()).andReturn(clientApi);
        expect(customerInternalId.getAsLong()).andReturn(123L);
        expect(clientApi.retrieveAssociatedAccounts(123L, "loanAccounts")).andReturn(responseGetAccounts);
        expect(context.getResponseBody(responseGetAccounts)).andReturn(bodyGetAccounts);
        expect(bodyGetAccounts.getLoanAccounts()).andReturn(loanAccounts);
        control.replay();

        var actual = useCase.findLoans(customerInternalId, 3);

        control.verify();

        assertThat(actual).isEqualTo(List.of(loan));
    }

    /**
     * Method under test: {@link SdkFindLoansUseCase#findLoans(CustomerInternalId, Integer)}
     */
    @Test
    void test_findLoans_emptyList() {
        CustomerInternalId customerInternalId = control.createMock(CustomerInternalId.class);
        ClientApi clientApi = control.createMock(ClientApi.class);
        Call<GetClientsClientIdAccountsResponse> responseGetAccounts = control.createMock(Call.class);
        GetClientsClientIdAccountsResponse bodyGetAccounts = control.createMock(GetClientsClientIdAccountsResponse.class);

        expect(context.clientApi()).andReturn(clientApi);
        expect(customerInternalId.getAsLong()).andReturn(123L);
        expect(clientApi.retrieveAssociatedAccounts(123L, "loanAccounts")).andReturn(responseGetAccounts);
        expect(context.getResponseBody(responseGetAccounts)).andReturn(bodyGetAccounts);
        expect(bodyGetAccounts.getLoanAccounts()).andReturn(Collections.emptySet());
        control.replay();

        var actual = useCase.findLoans(customerInternalId, 3);

        control.verify();

        assertThat(actual).isEmpty();
    }

}