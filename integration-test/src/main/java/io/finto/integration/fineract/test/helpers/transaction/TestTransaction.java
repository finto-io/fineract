package io.finto.integration.fineract.test.helpers.transaction;

import io.finto.fineract.sdk.models.PostSavingsAccountTransactionsRequest;
import io.finto.fineract.sdk.models.PostSavingsAccountsRequest;
import io.finto.fineract.sdk.models.PostSavingsAccountsRequestDatatablesInner;
import io.finto.fineract.sdk.models.PostSavingsAccountsRequestDatatablesInnerData;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Getter
@ToString
@EqualsAndHashCode
@Builder(toBuilder = true)
public class TestTransaction {
    @NonNull
    @ToString.Exclude
    private final TestTransactionRepository<?> repository;
    @NonNull
    private final AtomicBoolean issued = new AtomicBoolean(false);
    // Do not mark the following properties as @NonNull you may want to simulate sending a bad request
    private Long savingAccountId;
    private String command;
    private String dateFormat;
    private String locale;
    private Integer paymentTypeId;
    private BigDecimal transactionAmount;
    private String transactionDate;

    public PostSavingsAccountTransactionsRequest toTransactionRequest() {
        return PostSavingsAccountTransactionsRequest.builder()
                .dateFormat(dateFormat)
                .locale(locale)
                .paymentTypeId(paymentTypeId)
                .transactionAmount(transactionAmount)
                .transactionDate(transactionDate)
                .build();
    }

    public TestTransaction markIssued() {
        issued.set(true);
        return this;
    }
}
