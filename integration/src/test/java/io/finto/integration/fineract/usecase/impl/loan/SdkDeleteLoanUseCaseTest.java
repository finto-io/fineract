package io.finto.integration.fineract.usecase.impl.loan;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.finto.domain.id.fineract.LoanId;
import io.finto.fineract.sdk.api.LoansApi;
import io.finto.fineract.sdk.models.DeleteLoansLoanIdResponse;
import io.finto.integration.fineract.converter.FineractLoanProductMapper;
import io.finto.integration.fineract.usecase.impl.SdkFineractUseCaseContext;
import org.easymock.IMocksControl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Call;

import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SdkDeleteLoanUseCaseTest {
    private IMocksControl control;
    private SdkFineractUseCaseContext context;
    private FineractLoanProductMapper loanProductMapper;
    private SdkDeleteLoanUseCase useCase;
    private ObjectMapper mapper = JsonMapper.builder().findAndAddModules().build();

    @BeforeEach
    void setUp() {
        control = createStrictControl();
        context = control.createMock(SdkFineractUseCaseContext.class);
        loanProductMapper = control.createMock(FineractLoanProductMapper.class);
        mapper = control.createMock(ObjectMapper.class);
        useCase = SdkDeleteLoanUseCase.builder()
                .context(context)
                .build();
    }

    /**
     * Method under test: {@link SdkDeleteLoanUseCase#deleteLoan(LoanId)}
     */
    @Test
    void test_deleteLoan() {
        LoanId loanId = LoanId.of(13L);

        LoansApi loansApi = control.createMock(LoansApi.class);
        expect(context.loanApi())
                .andReturn(loansApi);
        Call<DeleteLoansLoanIdResponse> apiCall = control.createMock(Call.class);
        expect(loansApi.deleteLoanApplication(loanId.getValue()))
                .andReturn(apiCall);
        DeleteLoansLoanIdResponse apiResponse = control.createMock(DeleteLoansLoanIdResponse.class);
        expect(context.getResponseBody(apiCall)).
                andReturn(apiResponse);
        expect(apiResponse.getLoanId())
                .andReturn(loanId.getValue())
                .times(2);

        control.replay();
        LoanId actual = useCase.deleteLoan(loanId);
        control.verify();

        assertEquals(loanId, actual);
    }


}