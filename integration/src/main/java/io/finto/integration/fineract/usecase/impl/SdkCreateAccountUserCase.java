package io.finto.integration.fineract.usecase.impl;

import io.finto.fineract.sdk.models.PostSavingsAccountsRequest;
import io.finto.integration.fineract.converter.FineractAccountMapper;
import io.finto.integration.fineract.domain.AccountId;
import io.finto.integration.fineract.domain.AccountType;
import io.finto.integration.fineract.domain.CurrencyCode;
import io.finto.integration.fineract.domain.OpeningAccount;
import io.finto.integration.fineract.domain.Product;
import io.finto.integration.fineract.usecase.CreateAccountUseCase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;

import java.util.Objects;
import java.util.function.BiFunction;

@AllArgsConstructor
@Builder
public class SdkCreateAccountUserCase implements CreateAccountUseCase {

    @NonNull
    private final SdkFineractUseCaseContext context;
    @NonNull
    private final FineractAccountMapper accountMapper;
    @NonNull
    private final BiFunction<AccountType, CurrencyCode, Product> findProduct;

    public static class SdkCreateAccountUserCaseBuilder {
        private FineractAccountMapper accountMapper = FineractAccountMapper.INSTANCE;
    }

    @Override
    public AccountId initAccount(OpeningAccount request) {
        Product product = findProduct.apply(request.getAccountType(), request.getCurrencyCode());
        PostSavingsAccountsRequest fineractRequest = new PostSavingsAccountsRequest();
        fineractRequest.setProductId(product.getId().getValue().intValue());
        fineractRequest.setClientId(request.getCustomerId().getValue().intValue());
        //TODO fix contant values
        fineractRequest.setLocale("en");
        fineractRequest.setDateFormat("dd MMMM yyyy");
        fineractRequest.submittedOnDate("12 April 2023");

        return AccountId.of(
                Objects.requireNonNull(context
                                .getResponseBody(
                                        context.savingsAccountApi().submitSavingsAccountsApplication(fineractRequest)
                                )
                                .getSavingsId())
                        .longValue()
        );
    }

}
