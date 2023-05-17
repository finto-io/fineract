package io.finto.integration.fineract.test;

import io.finto.fineract.sdk.models.GetSavingsAccountsAccountIdResponse;
import io.finto.fineract.sdk.models.GetSavingsAccountsSummary;
import io.finto.fineract.sdk.models.GetSavingsCurrency;
import io.finto.fineract.sdk.models.GetSavingsSubStatus;
import io.finto.integration.fineract.domain.AccountAdditionalFields;
import io.finto.integration.fineract.domain.AccountId;

public class Fixtures {

    public static GetSavingsAccountsAccountIdResponse testSavedAccountResponse(AccountId accountId) {
        return GetSavingsAccountsAccountIdResponse.builder()
                .id(accountId.getValue().intValue())
                .accountNo("0123322323")
                .savingsProductId(12)
                .clientName("testCustomerName")
                .currency(GetSavingsCurrency.builder().code("JOD").build())
                .subStatus(GetSavingsSubStatus.builder().blockCredit(false).blockDebit(false).dormant(true).build())
                .summary(GetSavingsAccountsSummary.builder().accountBalance(100).availableBalance(50).build())
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

}
