package io.finto.integration.fineract.usecase.impl.loanproduct;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.finto.domain.charge.ChargeCreate;
import io.finto.domain.id.fineract.ChargeId;
import io.finto.domain.id.fineract.LoanProductId;
import io.finto.domain.loanproduct.FeeCreate;
import io.finto.domain.loanproduct.LoanProductCreate;
import io.finto.exceptions.core.FintoApiException;
import io.finto.fineract.sdk.models.PostLoanProductsRequest;
import io.finto.integration.fineract.converter.FineractLoanProductMapper;
import io.finto.integration.fineract.usecase.impl.SdkFineractUseCaseContext;
import io.finto.usecase.loanproduct.CreateLoanProductUseCase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static io.finto.fineract.sdk.CustomDatatableNames.LOAN_PRODUCT_FIELDS;

@AllArgsConstructor
@Builder
public class SdkCreateLoanProductUseCase implements CreateLoanProductUseCase {

    @NonNull
    private final SdkFineractUseCaseContext context;
    @NonNull
    private final FineractLoanProductMapper loanProductMapper;
    @NonNull
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private final ObjectMapper objectMapper;
    @NonNull
    private final Function<ChargeCreate, ChargeId> createCharge;

    public static class SdkCreateLoanProductUseCaseBuilder {
        private FineractLoanProductMapper loanProductMapper = FineractLoanProductMapper.INSTANCE;

        private ObjectMapper objectMapper = JsonMapper.builder().findAndAddModules().build();
    }

    @Override
    public LoanProductId createLoanProduct(LoanProductCreate request) {
        try {
            List<ChargeId> chargeIds = null;
            var fees = request.getFees();
            if (fees != null) {
                chargeIds = new ArrayList<>();
                for (FeeCreate feeCreate : fees) {
                    chargeIds.add(createCharge.apply(loanProductMapper.toChargeCreate(feeCreate, request.getShortName())));
                }
            }

            PostLoanProductsRequest fineractRequest = loanProductMapper.loanProductCreationFineractRequest(
                    request, chargeIds
            );

            var productId = LoanProductId.of(
                    Objects.requireNonNull(context
                            .getResponseBody(
                                    context.loanProductApi().createLoanProduct(fineractRequest)
                            )
                            .getResourceId())
            );

            context.getResponseBody(context.dataTablesApi().createDatatableEntry(
                    LOAN_PRODUCT_FIELDS,
                    productId.getValue(),
                    objectMapper.writeValueAsString(loanProductMapper.toLoanProductDetailsCreateDto(request, getCurrentDateTime()))
            ));

            return productId;
        } catch (JsonProcessingException e) {
            throw new FintoApiException(e);
        }
    }

    protected LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now();
    }
}
