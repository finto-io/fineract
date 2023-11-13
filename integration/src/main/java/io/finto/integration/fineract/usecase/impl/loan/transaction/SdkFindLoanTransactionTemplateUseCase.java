package io.finto.integration.fineract.usecase.impl.loan.transaction;

import io.finto.domain.bnpl.enums.LoanTransactionType;
import io.finto.domain.bnpl.transaction.TransactionTemplate;
import io.finto.domain.id.CustomerInternalId;
import io.finto.domain.id.fineract.LoanId;
import io.finto.integration.fineract.converter.FineractLoanTransactionMapper;
import io.finto.integration.fineract.usecase.impl.SdkFineractUseCaseContext;
import io.finto.integration.fineract.validators.loan.template.TemplateClientValidator;
import io.finto.integration.fineract.validators.loan.template.TemplateStatusValidator;
import io.finto.integration.fineract.validators.loan.template.impl.TemplateClientValidatorImpl;
import io.finto.integration.fineract.validators.loan.template.impl.TemplateStatusValidatorImpl;
import io.finto.usecase.loan.transaction.FindLoanTransactionTemplateUseCase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;

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

    public static class SdkFindLoanTransactionTemplateUseCaseBuilder {
        private FineractLoanTransactionMapper loanTransactionMapper = FineractLoanTransactionMapper.INSTANCE;
        private TemplateClientValidator templateClientValidator = new TemplateClientValidatorImpl();
        private TemplateStatusValidator templateStatusValidator = new TemplateStatusValidatorImpl();
    }

    @Override
    public TransactionTemplate findLoanTransaction(CustomerInternalId customerInternalId,
                                                   LoanId loanId,
                                                   LoanTransactionType type) {
        var id = loanId.getValue();
        var loan = context.getResponseBody(context.loanApi()
                .retrieveLoan(id,
                        null,
                        null,
                        null,
                        "clientId,status"));

        templateClientValidator.validate(customerInternalId, loan);
        templateStatusValidator.validate(loan);

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
