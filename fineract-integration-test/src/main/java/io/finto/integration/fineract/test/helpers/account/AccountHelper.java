package io.finto.integration.fineract.test.helpers.account;

import io.finto.fineract.sdk.util.FineractClient;
import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
public class AccountHelper implements TestSavingAccountRepositoryDelegate<AccountHelper>,
        TestSavingAccountBuilders<AccountHelper>{

    @NonNull
    private final TestSavingAccountRepository<?> repository;

    public AccountHelper(@NonNull FineractClient fineract) {
        this(TestSavingAccountRepositoryImpl.builder().withSavingAccountIssuerREST(fineract.getSavingsAccounts()).build());
    }

    @Override
    public AccountHelper submitSavingAccount(TestSavingAccount savingAccount) {
        repository.submitSavingAccount(savingAccount);
        return this;
    }

    @Override
    public AccountHelper closeLastAccount() {
        repository.closeLastAccount();
        return this;
    }

    @Override
    public AccountHelper approveLastAccount() {
        repository.approveLastAccount();
        return this;
    }

    @Override
    public AccountHelper activateLastAccount() {
        repository.activateLastAccount();
        return this;
    }

    @Override
    public AccountHelper setAccountStatus(TestSavingAccount savingAccount, AccountStatus status) {
        repository.setAccountStatus(savingAccount, status);
        return this;
    }

    @Override
    public TestSavingAccountRepository<?> getSavingAccountRepository() {
        return repository;
    }

    @Override
    public TestSavingAccountCreator<AccountHelper> buildSavingAccount() {
        return new TestSavingAccountCreator<>(this);
    }


}
