package io.finto.integration.fineract.usecase.impl.loan;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.finto.domain.bnpl.loan.Loan;
import io.finto.domain.bnpl.loan.LoanShortInfo;
import io.finto.domain.id.CustomerInternalId;
import io.finto.domain.id.fineract.LoanId;
import io.finto.exceptions.core.FintoApiException;
import io.finto.fineract.sdk.models.GetLoansLoanIdResponse;
import io.finto.fineract.sdk.models.RunReportsResponse;
import io.finto.integration.fineract.converter.ConverterUtils;
import io.finto.integration.fineract.converter.FineractLoanProductMapper;
import io.finto.integration.fineract.usecase.impl.SdkFineractUseCaseContext;
import io.finto.usecase.loan.FindLoanUseCase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

import java.time.ZonedDateTime;
import java.util.Arrays;

import static io.finto.fineract.sdk.CustomDatatableNames.LOAN_FIELDS;

@AllArgsConstructor
@Builder
public class SdkFindLoanUseCase implements FindLoanUseCase {

    @NonNull
    private final SdkFineractUseCaseContext context;
    @NonNull
    private final FineractLoanProductMapper loanProductMapper;
    @NonNull
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private final ObjectMapper objectMapper;

    public static class SdkFindLoanUseCaseBuilder {
        private FineractLoanProductMapper loanProductMapper = FineractLoanProductMapper.INSTANCE;
        private ObjectMapper objectMapper = JsonMapper.builder().findAndAddModules()
                .addModule(new SimpleModule().addDeserializer(ZonedDateTime.class,
                        new ConverterUtils.ZonedDateTimeDeserializer()))
                .build();
    }

    @Override
    public Loan findLoan(LoanId id, Integer digitsAfterDecimal) {
        var loanId = id.getValue();
        var loan = context.getResponseBody(context.loanApi()
                .retrieveLoan(loanId, false, "all", null, null));
        var additionalDetails = parseLoanAdditionalFields(objectMapper, context.getResponseBody(context.dataTablesApi()
                .getDatatableByAppTableId(LOAN_FIELDS, loanId, null, true)));
        return loanProductMapper.toDomain(loan, additionalDetails, digitsAfterDecimal);
    }

    @Override
    public LoanShortInfo getLoanShortInfo(LoanId loanId, String... fields) {
        StringBuilder sb = new StringBuilder();
        Arrays.stream(fields).forEach(fieldName -> {
            if (sb.length() != 0)
                sb.append(",");
            sb.append(fieldName);
        });
        String fieldsToGetFromFineract = sb.toString();
        GetLoansLoanIdResponse loanResponse = context.getResponseBody(context.loanApi()
                .retrieveLoan(loanId.getValue(), false, null, null, fieldsToGetFromFineract));

        return loanProductMapper.toLoanShortInfo(loanResponse);
    }

    public RunReportsResponse parseLoanAdditionalFields(ObjectMapper objectMapper, String content) {
        try {
            return objectMapper.readValue(content, RunReportsResponse.class);
        } catch (JsonProcessingException e) {
            throw new FintoApiException(e);
        }
    }
}
