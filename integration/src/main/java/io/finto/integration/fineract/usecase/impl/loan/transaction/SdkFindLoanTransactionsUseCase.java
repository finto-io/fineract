package io.finto.integration.fineract.usecase.impl.loan.transaction;

import io.finto.domain.bnpl.transaction.Transaction;
import io.finto.domain.id.fineract.LoanId;
import io.finto.integration.fineract.converter.FineractLoanProductMapper;
import io.finto.integration.fineract.usecase.impl.SdkFineractUseCaseContext;
import io.finto.usecase.loan.transaction.FindLoanTransactionsUseCase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;

import java.util.List;

@AllArgsConstructor
@Builder
public class SdkFindLoanTransactionsUseCase implements FindLoanTransactionsUseCase {

    @NonNull
    private final SdkFineractUseCaseContext context;
    @NonNull
    private final FineractLoanProductMapper loanProductMapper;

    public static class SdkFindLoanTransactionsUseCaseBuilder {
        private FineractLoanProductMapper loanProductMapper = FineractLoanProductMapper.INSTANCE;
    }

    @Override
    public List<Transaction> findLoanTransactions(LoanId loanId) {
        var loanResponse = context.getResponseBody(context.loanApi()
                .retrieveLoan(loanId.getValue(), false, "transactions", null, null));
        return loanProductMapper.toTransactions(loanResponse.getTransactions());
    }
}
