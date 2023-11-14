package io.finto.integration.fineract.usecase.impl.loan.transaction;

import io.finto.domain.bnpl.enums.LoanTransactionType;
import io.finto.domain.bnpl.loan.LoanShortInfo;
import io.finto.domain.bnpl.transaction.TransactionTemplate;
import io.finto.domain.id.CustomerInternalId;
import io.finto.domain.id.fineract.LoanId;
import io.finto.integration.fineract.converter.FineractLoanTransactionMapper;
import io.finto.integration.fineract.usecase.impl.SdkFineractUseCaseContext;
import io.finto.integration.fineract.validators.loan.template.TemplateClientValidator;
import io.finto.integration.fineract.validators.loan.template.TemplateStatusValidator;
import io.finto.integration.fineract.validators.loan.template.impl.TemplateClientValidatorImpl;
import io.finto.integration.fineract.validators.loan.template.impl.TemplateStatusValidatorImpl;
import io.finto.usecase.loan.FindLoanUseCase;
import io.finto.usecase.loan.transaction.FindLoanTransactionTemplateUseCase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;

import javax.validation.constraints.NotNull;

import static io.finto.fineract.sdk.Constants.FIELD_CLIENT_ID;
import static io.finto.fineract.sdk.Constants.FIELD_STATUS;

@AllArgsConstructor
@Builder
public class SdkFindLoanTransactionTemplateUseCase implements FindLoanTransactionTemplateUseCase {

    @NonNull
    private final SdkFineractUseCaseContext context;
    @NonNull
    private final FineractLoanTransactionMapper loanTransactionMapper;
    @NonNull
    private final TemplateClientValidator templateClientValidator;
    @NonNull
    private final TemplateStatusValidator templateStatusValidator;
    @NotNull
    private final FindLoanUseCase findLoanUseCase;

    public static class SdkFindLoanTransactionTemplateUseCaseBuilder {
        private FineractLoanTransactionMapper loanTransactionMapper = FineractLoanTransactionMapper.INSTANCE;
        private TemplateClientValidator templateClientValidator = new TemplateClientValidatorImpl();
        private TemplateStatusValidator templateStatusValidator = new TemplateStatusValidatorImpl();
    }

    @Override
    public TransactionTemplate findLoanTransactionTemplate(CustomerInternalId customerInternalId,
                                                           LoanId loanId,
                                                           LoanTransactionType type) {
        var id = loanId.getValue();

        LoanShortInfo loanShortInfo = findLoanUseCase.getLoanShortInfo(loanId, FIELD_CLIENT_ID, FIELD_STATUS);
        templateClientValidator.validate(customerInternalId, loanShortInfo);
        templateStatusValidator.validate(loanShortInfo);

        var loanTransactionTemplate = context.getResponseBody(
                context.loanTransactionApi().retrieveTransactionTemplate(
                        id,
                        loanTransactionMapper.toCommand(type),
                        null,
                        null,
                        null
                )
        );

        return loanTransactionMapper.toDomainBnplTransactionTemplate(loanTransactionTemplate);
    }
}
