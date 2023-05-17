package io.finto.integration.fineract.converter;

import io.finto.fineract.sdk.models.GetSavingsAccountsAccountIdResponse;
import io.finto.integration.fineract.domain.Account;
import io.finto.integration.fineract.domain.AccountAdditionalFields;
import io.finto.integration.fineract.domain.AccountId;
import io.finto.integration.fineract.domain.BankName;
import io.finto.integration.fineract.domain.BankSwift;
import io.finto.integration.fineract.domain.Customer;
import io.finto.integration.fineract.domain.ProductId;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static io.finto.integration.fineract.test.Fixtures.testAccountAdditionalFields;
import static io.finto.integration.fineract.test.Fixtures.testSavedAccountResponse;
import static org.assertj.core.api.Assertions.assertThat;

class FineractAccountMapperTest {

    private FineractAccountMapper mapper = FineractAccountMapper.INSTANCE;

    @Test
    void test_toAccount_withoutAdditional() {
        AccountId accountId = AccountId.of(10L);
        BankSwift bankSwift = BankSwift.of("testSwift");
        BankName bankName = BankName.of("testBankName");
        GetSavingsAccountsAccountIdResponse savedAccount = testSavedAccountResponse(accountId);

        Account actual = mapper.toAccount(savedAccount, null, bankSwift, bankName);

        Account expected = Account.builder()
                .id(accountId)
                .productId(ProductId.of(savedAccount.getSavingsProductId()))
                .branch(null)
                .number(savedAccount.getAccountNo())
                .type(null)
                .alternateNumber(null)
                .name(null)
                .iban(null)
                .swift(bankSwift.getValue())
                .bankName(bankName.getValue())
                .customer(Customer.builder().name(savedAccount.getClientName()).fullName(savedAccount.getClientName()).build())
                .currencyCode(savedAccount.getCurrency().getCode())
                .noDebit(savedAccount.getSubStatus().getBlockDebit())
                .noCredit(savedAccount.getSubStatus().getBlockCredit())
                .dormant(savedAccount.getSubStatus().getDormant())
                .creditCurrentBalance(BigDecimal.valueOf(savedAccount.getSummary().getAccountBalance()))
                .localCreditCurrentBalance(null)
                .creditBlockedAmount(BigDecimal.valueOf(savedAccount.getSummary().getAccountBalance()-savedAccount.getSummary().getAvailableBalance()))
                .creditAvailableBalance(BigDecimal.valueOf(savedAccount.getSummary().getAvailableBalance()))
                .externalAccountNumber(null)
                .externalAccountName(null)
                .build();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void test_toAccount_withAdditional() {
        AccountId accountId = AccountId.of(10L);
        BankSwift bankSwift = BankSwift.of("testSwift");
        BankName bankName = BankName.of("testBankName");
        GetSavingsAccountsAccountIdResponse savedAccount = testSavedAccountResponse(accountId);
        AccountAdditionalFields additionalFields = testAccountAdditionalFields(accountId);

        Account actual = mapper.toAccount(savedAccount, additionalFields, bankSwift, bankName);

        Account expected = Account.builder()
                .id(accountId)
                .productId(ProductId.of(savedAccount.getSavingsProductId()))
                .branch(additionalFields.getExternalBranch())
                .number(savedAccount.getAccountNo())
                .type(null)
                .alternateNumber(null)
                .name(null)
                .iban(additionalFields.getIban())
                .swift(bankSwift.getValue())
                .bankName(bankName.getValue())
                .customer(Customer.builder().name(savedAccount.getClientName()).fullName(savedAccount.getClientName()).build())
                .currencyCode(savedAccount.getCurrency().getCode())
                .noDebit(savedAccount.getSubStatus().getBlockDebit())
                .noCredit(savedAccount.getSubStatus().getBlockCredit())
                .dormant(savedAccount.getSubStatus().getDormant())
                .creditCurrentBalance(BigDecimal.valueOf(savedAccount.getSummary().getAccountBalance()))
                .localCreditCurrentBalance(null)
                .creditBlockedAmount(BigDecimal.valueOf(savedAccount.getSummary().getAccountBalance()-savedAccount.getSummary().getAvailableBalance()))
                .creditAvailableBalance(BigDecimal.valueOf(savedAccount.getSummary().getAvailableBalance()))
                .externalAccountNumber(additionalFields.getExternalAccountNumber())
                .externalAccountName(additionalFields.getExternalAccountName())
                .build();

        assertThat(actual).isEqualTo(expected);
    }

}