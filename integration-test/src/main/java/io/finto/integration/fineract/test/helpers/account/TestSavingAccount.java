package io.finto.integration.fineract.test.helpers.account;

import io.finto.fineract.sdk.models.PostSavingsAccountsAccountIdRequest;
import io.finto.fineract.sdk.models.PostSavingsAccountsRequest;
import io.finto.fineract.sdk.models.PostSavingsAccountsRequestDatatablesInner;
import io.finto.fineract.sdk.models.PostSavingsAccountsRequestDatatablesInnerData;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder(toBuilder = true)
public class TestSavingAccount {
    @NonNull
    @ToString.Exclude
    private final TestSavingAccountRepository<?> repository;
    @NonNull
    private final AtomicBoolean issued = new AtomicBoolean(false);
    // Do not mark the following properties as @NonNull you may want to simulate sending a bad request
    private Integer clientId;
    private String dateFormat;
    private String locale;
    private Integer productId;
    private String submittedOnDate;
    private String registeredTableName;
    private String iban;
    private String externalAccountNumber;
    private String externalAccountName;
    private String externalBranch;
    private AccountStatus status;

    public PostSavingsAccountsRequest toAccountRequest() {
        return PostSavingsAccountsRequest.builder()
                .clientId(clientId)
                .dateFormat(dateFormat)
                .locale(locale)
                .productId(productId)
                .submittedOnDate(submittedOnDate)
                .datatables(List.of(PostSavingsAccountsRequestDatatablesInner.builder()
                        .registeredTableName(registeredTableName)
                        .data(PostSavingsAccountsRequestDatatablesInnerData.builder()
                                .locale(locale)
                                .iban(iban)
                                .externalAccountNumber(externalAccountNumber)
                                .externalAccountName(externalAccountName)
                                .externalBranch(externalBranch)
                                .build())
                        .build()))
                .build();
    }

    public PostSavingsAccountsAccountIdRequest toStatusRequest() {
        var formatter = DateTimeFormatter.ofPattern(dateFormat);
        switch (status) {
            case CLOSED:
                return PostSavingsAccountsAccountIdRequest.builder()
                        .dateFormat(dateFormat)
                        .locale("en")
                        .closedOnDate(LocalDate.now().format(formatter))
                        .build();
            case APPROVED:
                return PostSavingsAccountsAccountIdRequest.builder()
                        .dateFormat(dateFormat)
                        .locale("en")
                        .approvedOnDate(LocalDate.now().format(formatter))
                        .build();
            case ACTIVATED:
                return PostSavingsAccountsAccountIdRequest.builder()
                        .dateFormat(dateFormat)
                        .locale("en")
                        .activatedOnDate(LocalDate.now().format(formatter))
                        .build();
        }
        throw new UnsupportedOperationException();
    }

    public TestSavingAccount markIssued() {
        issued.set(true);
        return this;
    }
}
