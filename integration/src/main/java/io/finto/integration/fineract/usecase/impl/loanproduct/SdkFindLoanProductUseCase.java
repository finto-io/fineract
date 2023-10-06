package io.finto.integration.fineract.usecase.impl.loanproduct;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.finto.domain.id.fineract.LoanProductId;
import io.finto.domain.loanproduct.LoanProduct;
import io.finto.exceptions.core.FintoApiException;
import io.finto.integration.fineract.converter.ConverterUtils;
import io.finto.integration.fineract.converter.FineractLoanProductMapper;
import io.finto.integration.fineract.dto.LoanProductDetailsDto;
import io.finto.integration.fineract.usecase.impl.SdkFineractUseCaseContext;
import io.finto.usecase.loanproduct.FindLoanProductUseCase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

import java.time.ZonedDateTime;

import static io.finto.fineract.sdk.CustomDatatableNames.LOAN_PRODUCT_FIELDS;

@AllArgsConstructor
@Builder
public class SdkFindLoanProductUseCase implements FindLoanProductUseCase {

    @NonNull
    private final SdkFineractUseCaseContext context;
    @NonNull
    private final FineractLoanProductMapper loanProductMapper;
    @NonNull
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private final ObjectMapper objectMapper;

    public static class SdkFindLoanProductUseCaseBuilder {
        private FineractLoanProductMapper loanProductMapper = FineractLoanProductMapper.INSTANCE;
        private ObjectMapper objectMapper = JsonMapper.builder().findAndAddModules()
                .addModule(new SimpleModule().addDeserializer(ZonedDateTime.class,
                        new ConverterUtils.ZonedDateTimeDeserializer()))
                .build();
    }

    @Override
    public LoanProduct findLoanProduct(LoanProductId id) {
        var loanProduct = context.getResponseBody(context.loanProductApi()
                .retrieveLoanProductDetails(id.getValue()));
        var additionalDetails = parseLoanProductAdditionalFields(objectMapper, context.getResponseBody(context.dataTablesApi()
                .getDatatableByAppTableId(LOAN_PRODUCT_FIELDS, id.getValue(), null)));
        return loanProductMapper.toDomain(loanProduct, additionalDetails);
    }

    public LoanProductDetailsDto parseLoanProductAdditionalFields(ObjectMapper objectMapper, String content) {
        try {
            LoanProductDetailsDto[] additionalFieldsDtos = objectMapper.readValue(content, LoanProductDetailsDto[].class);
            return additionalFieldsDtos.length > 0 ? additionalFieldsDtos[0] : null;
        } catch (JsonProcessingException e) {
            throw new FintoApiException(e);
        }
    }
}
