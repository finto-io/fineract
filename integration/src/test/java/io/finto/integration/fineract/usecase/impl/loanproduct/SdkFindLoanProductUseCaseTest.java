package io.finto.integration.fineract.usecase.impl.loanproduct;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.finto.domain.id.fineract.LoanProductId;
import io.finto.domain.loanproduct.LoanProduct;
import io.finto.fineract.sdk.api.DataTablesApi;
import io.finto.fineract.sdk.api.LoanProductsApi;
import io.finto.fineract.sdk.models.GetLoanProductsProductIdResponse;
import io.finto.integration.fineract.converter.FineractLoanProductMapper;
import io.finto.integration.fineract.dto.LoanProductDetailsDto;
import io.finto.integration.fineract.usecase.impl.SdkFineractUseCaseContext;
import org.easymock.IMocksControl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Call;

import static io.finto.fineract.sdk.CustomDatatableNames.LOAN_PRODUCT_FIELDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;

class SdkFindLoanProductUseCaseTest {
    private IMocksControl control;
    private SdkFineractUseCaseContext context;
    private FineractLoanProductMapper loanProductMapper;
    private SdkFindLoanProductUseCase useCase;
    private ObjectMapper mapper = JsonMapper.builder().findAndAddModules().build();

    @BeforeEach
    void setUp() {
        control = createStrictControl();
        context = control.createMock(SdkFineractUseCaseContext.class);
        loanProductMapper = control.createMock(FineractLoanProductMapper.class);
        mapper = control.createMock(ObjectMapper.class);
        useCase = SdkFindLoanProductUseCase.builder()
                .context(context)
                .loanProductMapper(loanProductMapper)
                .objectMapper(mapper)
                .build();
    }

    /**
     * Method under test: {@link SdkFindLoanProductUseCase#findLoanProduct(LoanProductId)}
     */
    @Test
    void test_findLoanProduct() throws JsonProcessingException {
        LoanProductId loanProductId = LoanProductId.of(1L);
        LoanProductsApi loanProductsApi = control.createMock(LoanProductsApi.class);
        Call<GetLoanProductsProductIdResponse> response = control.createMock(Call.class);
        GetLoanProductsProductIdResponse responseBody = control.createMock(
                GetLoanProductsProductIdResponse.class);
        LoanProductDetailsDto additionalFields = control.createMock(LoanProductDetailsDto.class);
        DataTablesApi dataTablesApi = control.createMock(DataTablesApi.class);
        Call<String> responseDataTable = control.createMock(Call.class);
        String responseBodyDataTable = "string";
        LoanProduct loanProduct = control.createMock(LoanProduct.class);
        LoanProductDetailsDto[] array = {additionalFields};

        expect(context.loanProductApi()).andReturn(loanProductsApi);
        expect(loanProductsApi.retrieveLoanProductDetails(loanProductId.getValue())).andReturn(response);
        expect(context.getResponseBody(response)).andReturn(responseBody);
        expect(context.dataTablesApi()).andReturn(dataTablesApi);
        expect(dataTablesApi.getDatatableByAppTableId(LOAN_PRODUCT_FIELDS, loanProductId.getValue(), null))
                .andReturn(responseDataTable);
        expect(context.getResponseBody(responseDataTable)).andReturn(responseBodyDataTable);
        expect(mapper.readValue(responseBodyDataTable, LoanProductDetailsDto[].class)).andReturn(array);
        expect(loanProductMapper.toDomain(responseBody, additionalFields)).andReturn(loanProduct);
        control.replay();

        LoanProduct actual = useCase.findLoanProduct(loanProductId);

        control.verify();

        assertThat(actual).isSameAs(loanProduct);
    }

}