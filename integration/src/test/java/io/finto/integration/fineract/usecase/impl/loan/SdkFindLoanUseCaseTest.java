package io.finto.integration.fineract.usecase.impl.loan;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.finto.domain.bnpl.loan.Loan;
import io.finto.domain.bnpl.loan.LoanShortInfo;
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
import static org.junit.jupiter.api.Assertions.assertEquals;

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

    @Test
    void test_findLoanShortInfo_success_3args() {
        LoanId loanId = LoanId.of(13L);
        String arg1 = "arg1";
        String arg2 = "arg2";
        String arg3 = "arg3";

        LoansApi loansApiMock = control.createMock(LoansApi.class);
        Call<GetLoansLoanIdResponse> apiCallMock = control.createMock(Call.class);
        GetLoansLoanIdResponse loanResponseMock = control.createMock(GetLoansLoanIdResponse.class);
        LoanShortInfo expected = control.createMock(LoanShortInfo.class);

        expect(context.loanApi())
                .andReturn(loansApiMock);
        expect(loansApiMock.retrieveLoan(loanId.getValue(), false, null, null, "arg1,arg2,arg3"))
                .andReturn(apiCallMock);
        expect(context.getResponseBody(apiCallMock)).andReturn(loanResponseMock);
        expect(loanProductMapper.toLoanShortInfo(loanResponseMock))
                .andReturn(expected);
        control.replay();

        LoanShortInfo actual = useCase.getLoanShortInfo(loanId, arg1, arg2, arg3);
        control.verify();

        assertEquals(expected, actual);
    }

    @Test
    void test_findLoanShortInfo_success_1arg() {
        LoanId loanId = LoanId.of(13L);
        String arg1 = "arg1";

        LoansApi loansApiMock = control.createMock(LoansApi.class);
        Call<GetLoansLoanIdResponse> apiCallMock = control.createMock(Call.class);
        GetLoansLoanIdResponse loanResponseMock = control.createMock(GetLoansLoanIdResponse.class);
        LoanShortInfo expected = control.createMock(LoanShortInfo.class);

        expect(context.loanApi())
                .andReturn(loansApiMock);
        expect(loansApiMock.retrieveLoan(loanId.getValue(), false, null, null, "arg1"))
                .andReturn(apiCallMock);
        expect(context.getResponseBody(apiCallMock)).andReturn(loanResponseMock);
        expect(loanProductMapper.toLoanShortInfo(loanResponseMock))
                .andReturn(expected);
        control.replay();

        LoanShortInfo actual = useCase.getLoanShortInfo(loanId, arg1);
        control.verify();

        assertEquals(expected, actual);
    }

    @Test
    void test_findLoanShortInfo_success_noArgs() {
        LoanId loanId = LoanId.of(13L);

        LoansApi loansApiMock = control.createMock(LoansApi.class);
        Call<GetLoansLoanIdResponse> apiCallMock = control.createMock(Call.class);
        GetLoansLoanIdResponse loanResponseMock = control.createMock(GetLoansLoanIdResponse.class);
        LoanShortInfo expected = control.createMock(LoanShortInfo.class);

        expect(context.loanApi())
                .andReturn(loansApiMock);
        expect(loansApiMock.retrieveLoan(loanId.getValue(), false, null, null, ""))
                .andReturn(apiCallMock);
        expect(context.getResponseBody(apiCallMock)).andReturn(loanResponseMock);
        expect(loanProductMapper.toLoanShortInfo(loanResponseMock))
                .andReturn(expected);
        control.replay();

        LoanShortInfo actual = useCase.getLoanShortInfo(loanId);
        control.verify();

        assertEquals(expected, actual);
    }

}