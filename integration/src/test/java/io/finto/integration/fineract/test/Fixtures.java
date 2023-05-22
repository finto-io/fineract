package io.finto.integration.fineract.test;

import io.finto.fineract.sdk.models.*;
import io.finto.integration.fineract.domain.AccountAdditionalFields;
import io.finto.integration.fineract.domain.AccountId;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Fixtures {

    public static GetSavingsAccountsAccountIdResponse testSavedAccountResponse(AccountId accountId) {
        return GetSavingsAccountsAccountIdResponse.builder()
                .id(accountId.getValue().intValue())
                .accountNo("0123322323")
                .savingsProductId(12)
                .clientName("testCustomerName")
                .currency(GetSavingsCurrency.builder().code("JOD").build())
                .subStatus(GetSavingsSubStatus.builder().blockCredit(false).blockDebit(false).dormant(true).build())
                .summary(GetSavingsAccountsSummary.builder().accountBalance(BigDecimal.valueOf(100L)).availableBalance(BigDecimal.valueOf(50L)).build())
                .build();
    }

    public static AccountAdditionalFields testAccountAdditionalFields(AccountId accountId) {
        return AccountAdditionalFields.builder()
                .savingsAccountId(accountId.getValue())
                .iban("testIban")
                .externalIntegrationSuccess(true)
                .externalSource("test")
                .integrationFailureType(null)
                .swift(null)
                .externalAccountNumber("1234567890")
                .externalAccountName("testExternalAccountName")
                .externalBranch("testExternalBranch")
                .build();
    }

    public static GetSavingsAccountsAccountIdTransactionsResponse testTransaction() {
        var now = LocalDate.now();
        return GetSavingsAccountsAccountIdTransactionsResponse.builder()
                .date(List.of(now.getYear(), now.getMonthValue(), now.getDayOfMonth()))
                .transactionType(GetSavingsAccountsAccountIdTransactionsResponseTransactionType.builder()
                        .code("savingsAccountTransactionType.withdrawal")
                        .value("top-up")
                        .build())
                .amount(100.003)
                .currency(GetSavingsAccountsAccountIdTransactionsResponseCurrency.builder()
                        .code("JOD")
                        .build())
                .build();
    }


}
