package io.finto.integration.fineract.usecase.impl.loan.product;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.finto.domain.id.fineract.LoanProductId;
import io.finto.domain.loanproduct.LoanProduct;
import io.finto.fineract.sdk.api.DataTablesApi;
import io.finto.fineract.sdk.api.LoanProductsApi;
import io.finto.fineract.sdk.models.GetLoanProductsProductIdResponse;
import io.finto.integration.fineract.converter.ConverterUtils;
import io.finto.integration.fineract.converter.FineractLoanProductMapper;
import io.finto.integration.fineract.dto.LoanProductDetailsDto;
import io.finto.integration.fineract.usecase.impl.SdkFineractUseCaseContext;
import io.finto.integration.fineract.usecase.impl.loan.product.SdkFindLoanProductUseCase;
import org.easymock.IMocksControl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Call;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

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
        expect(dataTablesApi.getDatatableByAppTableId(LOAN_PRODUCT_FIELDS, loanProductId.getValue(), null, null))
                .andReturn(responseDataTable);
        expect(context.getResponseBody(responseDataTable)).andReturn(responseBodyDataTable);
        expect(mapper.readValue(responseBodyDataTable, LoanProductDetailsDto[].class)).andReturn(array);
        expect(loanProductMapper.toDomain(responseBody, additionalFields)).andReturn(loanProduct);
        control.replay();

        LoanProduct actual = useCase.findLoanProduct(loanProductId);

        control.verify();

        assertThat(actual).isSameAs(loanProduct);
    }

    @Test
    void test_parseLoanProductAdditionalFields() {
        var useCase = SdkFindLoanProductUseCase.builder()
                .context(context)
                .loanProductMapper(loanProductMapper)
                .build();

        var actual = useCase.parseLoanProductAdditionalFields("[\n" +
                "\t{\n" +
                "\t\t\"product_loan_id\": 1181,\n" +
                "\t\t\"partner_id\": \"1dba4c82-e122-5c81-00e4-c1d3d2f5cbe9\",\n" +
                "\t\t\"partner_name\": \"1dba4c82-e122-5c81-00e4-c1d3d2f5cbe9\",\n" +
                "\t\t\"external_id\": \"9c568f99-1117-4bd9-9c8f-7d1eda3cf72c\",\n" +
                "\t\t\"late_payment_block_user\": \"true\",\n" +
                "\t\t\"early_settlement_allowed\": \"true\",\n" +
                "\t\t\"loaded_at\":  [\n" +
                "            2023,\n" +
                "            12,\n" +
                "            4,\n" +
                "            5,\n" +
                "            7,\n" +
                "            0,\n" +
                "            0\n" +
                "        ],\n" +
                "\t\t\"loaded_by\": \"mifos\",\n" +
                "\t\t\"closed_at\": null,\n" +
                "\t\t\"closed_by\": null,\n" +
                "\t\t\"modified_at\": null,\n" +
                "\t\t\"modified_by\": null\n" +
                "\t}\n" +
                "]");

        assertThat(actual).isEqualTo(LoanProductDetailsDto.builder()
                .productLoanId(1181L)
                .partnerId("1dba4c82-e122-5c81-00e4-c1d3d2f5cbe9")
                .partnerName("1dba4c82-e122-5c81-00e4-c1d3d2f5cbe9")
                .externalId("9c568f99-1117-4bd9-9c8f-7d1eda3cf72c")
                .latePaymentBlockUser(true)
                .earlySettlementAllowed(true)
                .loadedAt(LocalDateTime.parse("2023-12-04 05:07:00.0", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S")))
                .loadedBy("mifos")
                .closedAt(null)
                .closedBy(null)
                .modifiedAt(null)
                .modifiedBy(null)
                .build());
    }

}