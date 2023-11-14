package io.finto.integration.fineract.usecase.impl.loan.transaction;

import io.finto.domain.bnpl.transaction.Transaction;
import io.finto.domain.id.fineract.LoanId;
import io.finto.domain.id.fineract.TransactionId;
import io.finto.integration.fineract.converter.FineractLoanTransactionMapper;
import io.finto.integration.fineract.usecase.impl.SdkFineractUseCaseContext;
import io.finto.usecase.loan.transaction.FindLoanTransactionUseCase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;

@AllArgsConstructor
@Builder
public class SdkFindLoanTransactionUseCase implements FindLoanTransactionUseCase {

    @NonNull
    private final SdkFineractUseCaseContext context;
    @NonNull
    private final FineractLoanTransactionMapper loanTransactionMapper;

    public static class SdkFindLoanTransactionUseCaseBuilder {
        private FineractLoanTransactionMapper loanTransactionMapper = FineractLoanTransactionMapper.INSTANCE;
    }

    @Override
    public Transaction findLoanTransaction(LoanId loanId, TransactionId transactionId) {
        var loanTransactionApi = context.loanTransactionApi();
        var loanTransaction = context.getResponseBody(
                loanTransactionApi.retrieveTransaction(loanId.getValue(), transactionId.getValue(), null)
        );
        return loanTransactionMapper.toDomainBnplTransaction(loanTransaction);
    }
}
