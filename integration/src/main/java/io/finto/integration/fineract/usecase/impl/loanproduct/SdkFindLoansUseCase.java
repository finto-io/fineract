package io.finto.integration.fineract.usecase.impl.loanproduct;

import io.finto.domain.bnpl.loan.Loan;
import io.finto.domain.id.CustomerInternalId;
import io.finto.domain.id.fineract.LoanId;
import io.finto.integration.fineract.usecase.impl.SdkFineractUseCaseContext;
import io.finto.usecase.loanproduct.FindLoansUseCase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

@AllArgsConstructor
@Builder
public class SdkFindLoansUseCase implements FindLoansUseCase {

    @NonNull
    private final SdkFineractUseCaseContext context;
    @NonNull
    private final BiFunction<LoanId, Integer, Loan> findLoan;

    @Override
    public List<Loan> findLoans(CustomerInternalId customerInternalId, Integer digitsAfterDecimal) {
        var accounts = context.getResponseBody(context.clientApi()
                .retrieveAssociatedAccounts(customerInternalId.getAsLong(), "loanAccounts"));
        var loanAccounts = accounts.getLoanAccounts();
        if (loanAccounts == null || loanAccounts.isEmpty()) {
            return Collections.emptyList();
        } else {
            List<Loan> loans = new ArrayList<>();
            loanAccounts.forEach(loanAccount -> loans.add(
                    findLoan.apply(LoanId.of(Objects.requireNonNull(loanAccount.getId())), digitsAfterDecimal)
            ));
            return loans;
        }
    }

}
