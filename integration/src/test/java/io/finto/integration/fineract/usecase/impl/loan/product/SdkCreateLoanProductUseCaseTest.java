package io.finto.integration.fineract.usecase.impl.loan.product;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.finto.domain.charge.ChargeCreate;
import io.finto.domain.id.fineract.ChargeId;
import io.finto.domain.id.fineract.LoanProductId;
import io.finto.domain.loanproduct.FeeCreate;
import io.finto.domain.loanproduct.LoanProductCreate;
import io.finto.exceptions.core.generic.BadRequestException;
import io.finto.fineract.sdk.api.DataTablesApi;
import io.finto.fineract.sdk.api.LoanProductsApi;
import io.finto.fineract.sdk.models.PostDataTablesAppTableIdResponse;
import io.finto.fineract.sdk.models.PostLoanProductsRequest;
import io.finto.fineract.sdk.models.PostLoanProductsResponse;
import io.finto.integration.fineract.converter.FineractLoanProductMapper;
import io.finto.integration.fineract.dto.LoanProductDetailsCreateDto;
import io.finto.integration.fineract.usecase.impl.SdkFineractUseCaseContext;
import io.finto.integration.fineract.usecase.impl.loan.product.SdkCreateLoanProductUseCase;
import org.easymock.IMocksControl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Call;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

import static io.finto.fineract.sdk.CustomDatatableNames.LOAN_PRODUCT_FIELDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SdkCreateLoanProductUseCaseTest {
    private IMocksControl control;
    private SdkFineractUseCaseContext context;
    private FineractLoanProductMapper loanProductMapper;
    private SdkCreateLoanProductUseCase useCase;
    private final ChargeId chargeId = ChargeId.of(123L);
    private final Function<ChargeCreate, ChargeId> chargeCreateResolver = (chargeCreate) -> chargeId;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        control = createStrictControl();
        context = control.createMock(SdkFineractUseCaseContext.class);
        loanProductMapper = control.createMock(FineractLoanProductMapper.class);
        objectMapper = control.createMock(ObjectMapper.class);
        useCase = createMockBuilder(SdkCreateLoanProductUseCase.class)
                .withConstructor(context, loanProductMapper, objectMapper, chargeCreateResolver)
                .addMockedMethods("getCurrentDateTime")
                .createMock(control);
    }

    /**
     * Method under test: {@link SdkCreateLoanProductUseCase#createLoanProduct(LoanProductCreate)}
     */
    @Test
    void test_createLoanProduct() throws JsonProcessingException {
        LoanProductCreate request = control.createMock(LoanProductCreate.class);
        FeeCreate feeCreate = control.createMock(FeeCreate.class);
        ChargeCreate chargeCreate = control.createMock(ChargeCreate.class);
        PostLoanProductsRequest fineractRequest = control.createMock(PostLoanProductsRequest.class);
        LoanProductsApi loanProductsApi = control.createMock(LoanProductsApi.class);
        Call<PostLoanProductsResponse> response = control.createMock(Call.class);
        PostLoanProductsResponse responseBody = control.createMock(PostLoanProductsResponse.class);
        DataTablesApi dataTablesApi = control.createMock(DataTablesApi.class);
        LoanProductDetailsCreateDto loanProductDetailsCreateDto = control.createMock(LoanProductDetailsCreateDto.class);
        Call<PostDataTablesAppTableIdResponse> responseDataTable = control.createMock(Call.class);
        LocalDateTime localDateTime = LocalDateTime.now();
        PostDataTablesAppTableIdResponse postDataTablesAppTableIdResponse = control.createMock(PostDataTablesAppTableIdResponse.class);

        expect(request.getFees()).andReturn(List.of(feeCreate));
        expect(feeCreate.getFeeName()).andReturn("feeName");
        expect(request.getShortName()).andReturn("shortName");
        expect(loanProductMapper.toChargeCreate(feeCreate, "shortName")).andReturn(chargeCreate);
        expect(loanProductMapper.loanProductCreationFineractRequest(request, List.of(chargeId))).andReturn(fineractRequest);
        expect(fineractRequest.getGraceOnPrincipalPayment()).andReturn(2);
        expect(fineractRequest.getNumberOfRepayments()).andReturn(3);
        expect(context.loanProductApi()).andReturn(loanProductsApi);
        expect(loanProductsApi.createLoanProduct(fineractRequest)).andReturn(response);
        expect(context.getResponseBody(response)).andReturn(responseBody);
        expect(responseBody.getResourceId()).andReturn(10L);
        expect(context.dataTablesApi()).andReturn(dataTablesApi);
        expect(useCase.getCurrentDateTime()).andReturn(localDateTime);
        expect(loanProductMapper.toLoanProductDetailsCreateDto(request, localDateTime))
                .andReturn(loanProductDetailsCreateDto);
        expect(objectMapper.writeValueAsString(loanProductDetailsCreateDto))
                .andReturn("asd");
        expect(dataTablesApi.createDatatableEntry(LOAN_PRODUCT_FIELDS, 10L, "asd")).andReturn(responseDataTable);
        expect(context.getResponseBody(responseDataTable)).andReturn(postDataTablesAppTableIdResponse);
        control.replay();

        LoanProductId actual = useCase.createLoanProduct(request);

        control.verify();

        assertThat(actual).isEqualTo(LoanProductId.of(10L));
    }

    /**
     * Method under test: {@link SdkCreateLoanProductUseCase#createLoanProduct(LoanProductCreate)}
     */
    @Test
    void test_createLoanProduct_invalidFee() {
        LoanProductCreate request = control.createMock(LoanProductCreate.class);
        FeeCreate feeCreate1 = control.createMock(FeeCreate.class);
        FeeCreate feeCreate2 = control.createMock(FeeCreate.class);

        expect(request.getFees()).andReturn(List.of(feeCreate1, feeCreate2));
        expect(feeCreate1.getFeeName()).andReturn("feeName");
        expect(feeCreate2.getFeeName()).andReturn("feeName");
        control.replay();

        assertThatThrownBy(() -> useCase.createLoanProduct(request)).isInstanceOf(BadRequestException.class);

        control.verify();
    }

    /**
     * Method under test: {@link SdkCreateLoanProductUseCase#createLoanProduct(LoanProductCreate)}
     */
    @Test
    void test_createLoanProduct_invalidGraceOnPrincipalPayment() {
        LoanProductCreate request = control.createMock(LoanProductCreate.class);
        FeeCreate feeCreate = control.createMock(FeeCreate.class);
        ChargeCreate chargeCreate = control.createMock(ChargeCreate.class);
        PostLoanProductsRequest fineractRequest = control.createMock(PostLoanProductsRequest.class);

        expect(request.getFees()).andReturn(List.of(feeCreate));
        expect(feeCreate.getFeeName()).andReturn("feeName");
        expect(request.getShortName()).andReturn("shortName");
        expect(loanProductMapper.toChargeCreate(feeCreate, "shortName")).andReturn(chargeCreate);
        expect(loanProductMapper.loanProductCreationFineractRequest(request, List.of(chargeId))).andReturn(fineractRequest);
        expect(fineractRequest.getGraceOnPrincipalPayment()).andReturn(3);
        expect(fineractRequest.getNumberOfRepayments()).andReturn(3);
        control.replay();

        assertThatThrownBy(() -> useCase.createLoanProduct(request)).isInstanceOf(BadRequestException.class);

        control.verify();
    }


    /**
     * Method under test: {@link SdkCreateLoanProductUseCase#getCurrentDateTime()}
     */
    @Test
    void test_getCurrentDateTime() {
        SdkCreateLoanProductUseCase sdkCreateLoanProductUseCase = SdkCreateLoanProductUseCase.builder()
                .context(context)
                .createCharge(chargeCreateResolver)
                .build();
        var actual = sdkCreateLoanProductUseCase.getCurrentDateTime();
        assertNotNull(actual);
    }

}