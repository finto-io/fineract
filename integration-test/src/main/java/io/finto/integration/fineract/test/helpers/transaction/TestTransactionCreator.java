package io.finto.integration.fineract.test.helpers.transaction;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class TestTransactionCreator<T extends TestTransactionRepository<T>> {

    private static final Random PRNG = new Random();
    private static final List<String> availableDateFormat = List.of("dd-MM-yyyy");
    private static final List<String> availableLocale = List.of("en");


    @NonNull
    protected final T repository;
    private String command;
    private String dateFormat;
    private String locale;
    private Integer paymentTypeId;
    private BigDecimal transactionAmount;
    private String transactionDate;


    public TestTransactionCreator<T> withCommand(String command) {
        this.command = command;
        return this;
    }

    public TestTransactionCreator<T> withDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
        return this;
    }

    public TestTransactionCreator<T> withLocale(String locale) {
        this.locale = locale;
        return this;
    }

    public TestTransactionCreator<T> withPaymentTypeId(Integer paymentTypeId) {
        this.paymentTypeId = paymentTypeId;
        return this;
    }

    public TestTransactionCreator<T> withTransactionAmount(BigDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
        return this;
    }

    public TestTransactionCreator<T> withTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
        return this;
    }

    public TestTransactionCreator<T> withRandomParams() {
        var date = LocalDate.now();

        return withCommand("deposit")
                .withDateFormat(availableDateFormat.get(PRNG.nextInt(availableDateFormat.size())))
                .withLocale(availableLocale.get(PRNG.nextInt(availableLocale.size())))
                .withPaymentTypeId(4)
                .withTransactionAmount(BigDecimal.valueOf(PRNG.nextDouble() * 1000))
                .withTransactionDate(date.format(DateTimeFormatter.ofPattern(dateFormat)))
                ;
    }

    public T create(Long savingAccountId) {
        return repository.submitTransaction(TestTransaction.builder()
                .repository(repository)
                .savingAccountId(savingAccountId)
                .command(command)
                .dateFormat(dateFormat)
                .locale(locale)
                .paymentTypeId(paymentTypeId)
                .transactionAmount(transactionAmount)
                .transactionDate(transactionDate)
                .build());
    }

    @SuppressWarnings("unchecked")
    public <R extends TestTransactionCreator<T>> R castTo(Class<R> to) {
        return (R) this;
    }

}
