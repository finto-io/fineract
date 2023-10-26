package io.finto.integration.fineract.usecase.impl.loanproduct;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.finto.domain.bnpl.loan.Loan;
import io.finto.domain.id.fineract.LoanId;
import io.finto.fineract.sdk.api.DataTablesApi;
import io.finto.fineract.sdk.api.LoansApi;
import io.finto.fineract.sdk.models.GetLoansLoanIdResponse;
import io.finto.fineract.sdk.models.RunReportsResponse;
import io.finto.integration.fineract.converter.FineractLoanProductMapper;
import io.finto.integration.fineract.usecase.impl.SdkFineractUseCaseContext;
import org.easymock.IMocksControl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Call;

import static org.assertj.core.api.Assertions.assertThat;
import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;

class SdkFindLoanUseCaseTest {
    private IMocksControl control;
    private SdkFineractUseCaseContext context;
    private FineractLoanProductMapper loanProductMapper;
    private SdkFindLoanUseCase useCase;
    private ObjectMapper mapper = JsonMapper.builder().findAndAddModules().build();

    @BeforeEach
    void setUp() {
        control = createStrictControl();
        context = control.createMock(SdkFineractUseCaseContext.class);
        loanProductMapper = control.createMock(FineractLoanProductMapper.class);
        mapper = control.createMock(ObjectMapper.class);
        useCase = SdkFindLoanUseCase.builder()
                .context(context)
                .loanProductMapper(loanProductMapper)
                .objectMapper(mapper)
                .build();
    }

    /**
     * Method under test: {@link SdkFindLoanUseCase#findLoan(LoanId, Integer)}
     */
    @Test
    void test_findLoan() throws JsonProcessingException {
        LoanId loanId = control.createMock(LoanId.class);
        LoansApi loansApi = control.createMock(LoansApi.class);
        Call<GetLoansLoanIdResponse> responseGetLoan = control.createMock(Call.class);
        GetLoansLoanIdResponse bodyGetLoan = control.createMock(GetLoansLoanIdResponse.class);
        DataTablesApi dataTablesApi = control.createMock(DataTablesApi.class);
        Call<String> responseDataTable = control.createMock(Call.class);
        String bodyDataTable = "string";
        RunReportsResponse reportsResponse = control.createMock(RunReportsResponse.class);
        Loan loan = control.createMock(Loan.class);


        expect(loanId.getValue()).andReturn(1L);
        expect(context.loanApi()).andReturn(loansApi);
        expect(loansApi.retrieveLoan(1L, false, "all", null, null))
                .andReturn(responseGetLoan);
        expect(context.getResponseBody(responseGetLoan)).andReturn(bodyGetLoan);
        expect(context.dataTablesApi()).andReturn(dataTablesApi);
        expect(dataTablesApi.getDatatableByAppTableId("loan_fields", 1L, null, true))
                .andReturn(responseDataTable);
        expect(context.getResponseBody(responseDataTable)).andReturn(bodyDataTable);
        expect(mapper.readValue(bodyDataTable, RunReportsResponse.class)).andReturn(reportsResponse);
        expect(loanProductMapper.toDomain(bodyGetLoan, reportsResponse, 4)).andReturn(loan);
        control.replay();

        Loan actual = useCase.findLoan(loanId, 4);

        control.verify();

        assertThat(actual).isSameAs(loan);
    }

}