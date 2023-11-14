package io.finto.integration.fineract.usecase.impl.loan.transaction;

import io.finto.domain.bnpl.enums.LoanTransactionType;
import io.finto.domain.bnpl.loan.LoanShortInfo;
import io.finto.domain.bnpl.transaction.Transaction;
import io.finto.domain.bnpl.transaction.TransactionSubmit;
import io.finto.domain.id.CustomerInternalId;
import io.finto.domain.id.fineract.LoanId;
import io.finto.fineract.sdk.models.PostLoansLoanIdTransactionsRequest;
import io.finto.integration.fineract.converter.FineractLoanTransactionMapper;
import io.finto.integration.fineract.usecase.impl.SdkFineractUseCaseContext;
import io.finto.integration.fineract.validators.loan.template.TemplateClientValidator;
import io.finto.integration.fineract.validators.loan.template.TemplateDateValidator;
import io.finto.integration.fineract.validators.loan.template.TemplateStatusValidator;
import io.finto.integration.fineract.validators.loan.template.TemplateSubmitRequestValidator;
import io.finto.integration.fineract.validators.loan.template.impl.TemplateClientValidatorImpl;
import io.finto.integration.fineract.validators.loan.template.impl.TemplateDateValidatorImpl;
import io.finto.integration.fineract.validators.loan.template.impl.TemplateStatusValidatorImpl;
import io.finto.integration.fineract.validators.loan.template.impl.TemplateSubmitRequestValidatorImpl;
import io.finto.usecase.loan.FindLoanUseCase;
import io.finto.usecase.loan.transaction.SubmitTransactionUseCase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;

import javax.validation.constraints.NotNull;
import java.util.Objects;

import static io.finto.fineract.sdk.Constants.FIELD_CLIENT_ID;
import static io.finto.fineract.sdk.Constants.FIELD_STATUS;
import static io.finto.fineract.sdk.Constants.FIELD_TIMELINE;

@AllArgsConstructor
@Builder
public class SdkSubmitTransactionUseCase implements SubmitTransactionUseCase {

    @NonNull
    private final SdkFineractUseCaseContext context;
    @NonNull
    private final FineractLoanTransactionMapper loanTransactionMapper;
    @NonNull
    private final TemplateClientValidator templateClientValidator;
    @NonNull
    private final TemplateStatusValidator templateStatusValidator;
    @NonNull
    private final TemplateDateValidator templateDateValidator;
    @NonNull
    private final TemplateSubmitRequestValidator templateSubmitRequestValidator;
    @NotNull
    private final FindLoanUseCase findLoanUseCase;

    private final static String BAD_REQUEST_MESSAGE = "Invalid loan transaction request (%s)";

    public static class SdkSubmitTransactionUseCaseBuilder {
        private FineractLoanTransactionMapper loanTransactionMapper = FineractLoanTransactionMapper.INSTANCE;
        private TemplateClientValidator templateClientValidator = new TemplateClientValidatorImpl();
        private TemplateStatusValidator templateStatusValidator = new TemplateStatusValidatorImpl();
        private TemplateDateValidator templateDateValidator = new TemplateDateValidatorImpl();
        private TemplateSubmitRequestValidator templateSubmitRequestValidator = new TemplateSubmitRequestValidatorImpl();
    }

    @Override
    public Transaction submitTransaction(CustomerInternalId customerInternalId, LoanId loanId, TransactionSubmit request) {
        templateSubmitRequestValidator.validate(request);

        var id = loanId.getValue();
        LoanShortInfo loanShortInfo = findLoanUseCase.getLoanShortInfo(loanId, FIELD_CLIENT_ID, FIELD_STATUS, FIELD_TIMELINE);
        templateClientValidator.validate(customerInternalId, loanShortInfo);
        templateStatusValidator.validate(loanShortInfo);
        templateDateValidator.validate(request, loanShortInfo);

        PostLoansLoanIdTransactionsRequest fineractRequest;
        var type = Objects.requireNonNull(request.getType());
        if (type == LoanTransactionType.FORECLOSURE) {
            fineractRequest = loanTransactionMapper.loanTransactionSubmissionForeclosure(request);
        } else {
            context.getResponseBody(context.paymentTypeApi().retrieveOnePaymentType(request.getPaymentTypeId()));
            fineractRequest = loanTransactionMapper.loanTransactionSubmissionOther(request);
        }
        var loanTransactionApi = context.loanTransactionApi();
        var submittedTransaction = context.getResponseBody(
                loanTransactionApi.executeLoanTransaction(
                        id,
                        fineractRequest,
                        loanTransactionMapper.toCommand(type)
                )
        );
        var loanTransaction = context.getResponseBody(
                loanTransactionApi.retrieveTransaction(id, submittedTransaction.getResourceId(), null)
        );
        return loanTransactionMapper.toDomainBnplTransaction(loanTransaction);
    }

}
