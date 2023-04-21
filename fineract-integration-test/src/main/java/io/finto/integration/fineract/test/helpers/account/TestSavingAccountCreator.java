package io.finto.integration.fineract.test.helpers.account;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

import static org.apache.commons.lang3.RandomStringUtils.random;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class TestSavingAccountCreator<T extends TestSavingAccountRepository<T>> {

    private static final Random PRNG = new Random();
    private static final List<String> availableDateFormat = List.of("dd-MM-yyyy");
    private static final List<String> availableLocale = List.of("en");


    @NonNull protected final T repository;
    private String dateFormat;
    private String locale;
    private String submittedOnDate;
    private String registeredTableName;
    private String iban;
    private String externalAccountNumber;
    private String externalAccountName;
    private String externalBranch;

    public TestSavingAccountCreator<T> withDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
        return this;
    }
    public TestSavingAccountCreator<T> withLocale(String locale) {
        this.locale = locale;
        return this;
    }
    public TestSavingAccountCreator<T> withSubmittedOnDate(String submittedOnDate) {
        this.submittedOnDate = submittedOnDate;
        return this;
    }

    public TestSavingAccountCreator<T> withRegisteredTableName(String registeredTableName) {
        this.registeredTableName = registeredTableName;
        return this;
    }

    public TestSavingAccountCreator<T> withIban(String iban) {
        this.iban = iban;
        return this;
    }
    public TestSavingAccountCreator<T> withExternalAccountNumber(String externalAccountNumber) {
        this.externalAccountNumber = externalAccountNumber;
        return this;
    }
    public TestSavingAccountCreator<T> withExternalAccountName(String externalAccountName) {
        this.externalAccountName = externalAccountName;
        return this;
    }
    public TestSavingAccountCreator<T> withExternalBranch(String externalBranch) {
        this.externalBranch = externalBranch;
        return this;
    }

    public TestSavingAccountCreator<T> withRandomParams() {
        var date = LocalDate.now().minusDays((long) (PRNG.nextDouble() * 365 * 20));
        var dateFormat = availableDateFormat.get(PRNG.nextInt(availableDateFormat.size()));

        return withDateFormat(availableDateFormat.get(PRNG.nextInt(availableDateFormat.size())))
                .withLocale(availableLocale.get(PRNG.nextInt(availableLocale.size())))
                .withSubmittedOnDate(date.format(DateTimeFormatter.ofPattern(dateFormat)))
                .withRegisteredTableName("account_fields")
                .withIban(random(10, true, true))
                .withExternalAccountNumber(random(10, true, true))
                .withExternalAccountName(random(10, true, false))
                .withExternalBranch(random(10, true, false))
                ;
    }
    
    public T create(Integer clientId, Integer productId) {
        return repository.submitSavingAccount(TestSavingAccount.builder()
                .repository(repository)
                .clientId(clientId)
                .dateFormat(dateFormat)
                .locale(locale)
                .productId(productId)
                .submittedOnDate(submittedOnDate)
                .registeredTableName(registeredTableName)
                .iban(iban)
                .externalAccountNumber(externalAccountNumber)
                .externalAccountName(externalAccountName)
                .externalBranch(externalBranch)
                .build());
    }
    
    @SuppressWarnings("unchecked")
    public <R extends TestSavingAccountCreator<T>> R castTo(Class<R> to) {
        return (R) this;
    }

}
