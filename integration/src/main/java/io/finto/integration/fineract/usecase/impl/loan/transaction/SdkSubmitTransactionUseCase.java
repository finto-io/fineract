package io.finto.integration.fineract.usecase.impl.loan.transaction;

import io.finto.domain.bnpl.enums.LoanTransactionType;
import io.finto.domain.bnpl.transaction.Transaction;
import io.finto.domain.bnpl.transaction.TransactionSubmit;
import io.finto.domain.id.CustomerInternalId;
import io.finto.domain.id.fineract.LoanId;
import io.finto.exceptions.core.generic.BadRequestException;
import io.finto.fineract.sdk.models.GetLoansLoanIdResponse;
import io.finto.fineract.sdk.models.PostLoansLoanIdTransactionsRequest;
import io.finto.integration.fineract.converter.FineractLoanTransactionMapper;
import io.finto.integration.fineract.usecase.impl.SdkFineractUseCaseContext;
import io.finto.usecase.loan.transaction.SubmitTransactionUseCase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;

import java.time.LocalDate;
import java.util.Objects;

import static io.finto.fineract.sdk.Constants.FORECLOSURE;
import static io.finto.fineract.sdk.Constants.REPAYMENT;

@AllArgsConstructor
@Builder
public class SdkSubmitTransactionUseCase implements SubmitTransactionUseCase {

    @NonNull
    private final SdkFineractUseCaseContext context;
    @NonNull
    private final FineractLoanTransactionMapper loanTransactionMapper;

    private final static String BAD_REQUEST_MESSAGE = "Invalid loan transaction request (%s)";

    public static class SdkSubmitTransactionUseCaseBuilder {
        private FineractLoanTransactionMapper loanTransactionMapper = FineractLoanTransactionMapper.INSTANCE;
    }

    @Override
    public Transaction submitTransaction(CustomerInternalId customerInternalId, LoanId loanId, TransactionSubmit request) {
        var id = loanId.getValue();
        var loan = context.getResponseBody(context.loanApi()
                .retrieveLoan(id,
                        null,
                        null,
                        null,
                        "clientId,status,timeline"));

        validate(customerInternalId, request, loan);

        PostLoansLoanIdTransactionsRequest fineractRequest;
        String command;
        if (Objects.requireNonNull(request.getType()) == LoanTransactionType.FORECLOSURE) {
            fineractRequest = loanTransactionMapper.loanTransactionSubmissionForeclosure(request);
            command = FORECLOSURE;
        } else {
            context.getResponseBody(context.paymentTypeApi().retrieveOnePaymentType(request.getPaymentTypeId()));
            fineractRequest = loanTransactionMapper.loanTransactionSubmissionOther(request);
            command = REPAYMENT;
        }
        var loanTransactionApi = context.loanTransactionApi();
        var submittedTransaction = context.getResponseBody(
                loanTransactionApi.executeLoanTransaction(id, fineractRequest, command)
        );
        var loanTransaction = context.getResponseBody(
                loanTransactionApi.retrieveTransaction(id, submittedTransaction.getResourceId(), null)
        );
        return loanTransactionMapper.toDomainBnplTransaction(loanTransaction);
    }

    private void validate(CustomerInternalId customerInternalId, TransactionSubmit request, GetLoansLoanIdResponse loan) {
        if (!customerInternalId.getAsLong().equals(loan.getClientId())) {
            throw new BadRequestException(BadRequestException.DEFAULT_ERROR_CODE, "Incorrect customer");
        }
        var status = loan.getStatus();
        if (status != null && Boolean.FALSE.equals(status.getActive())) {
            throw new BadRequestException(BadRequestException.DEFAULT_ERROR_CODE,
                    String.format(BAD_REQUEST_MESSAGE, "loan is inactive"));
        }
        var actualDisbursementDate = Objects.requireNonNull(
                Objects.requireNonNull(loan.getTimeline()).getActualDisbursementDate()
        );
        var date = request.getDate();
        if (date.isBefore(actualDisbursementDate) || date.isAfter(getCurrentDate())) {
            throw new BadRequestException(BadRequestException.DEFAULT_ERROR_CODE,
                    String.format(BAD_REQUEST_MESSAGE, "date is invalid"));
        }
    }

    protected LocalDate getCurrentDate() {
        return LocalDate.now();
    }

}
