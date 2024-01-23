package io.finto.integration.fineract.usecase.impl.account;

import io.finto.domain.account.AccountId;
import io.finto.domain.account.AccountType;
import io.finto.domain.account.CurrencyCode;
import io.finto.domain.account.OpeningAccount;
import io.finto.domain.product.Product;
import io.finto.fineract.sdk.models.PostSavingsAccountsRequest;
import io.finto.integration.fineract.converter.FineractAccountMapper;
import io.finto.integration.fineract.usecase.impl.SdkFineractUseCaseContext;
import io.finto.usecase.account.CreateAccountUseCase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;

import java.util.Objects;
import java.util.function.BiFunction;

@AllArgsConstructor
@Builder
public class SdkCreateAccountUseCase implements CreateAccountUseCase {

    @NonNull
    private final SdkFineractUseCaseContext context;
    @NonNull
    private final FineractAccountMapper accountMapper;
    @NonNull
    private final BiFunction<AccountType, CurrencyCode, Product> findProduct;

    public static class SdkCreateAccountUseCaseBuilder {
        private FineractAccountMapper accountMapper = FineractAccountMapper.INSTANCE;
    }

    @Override
    public AccountId initAccount(OpeningAccount request) {
        PostSavingsAccountsRequest fineractRequest = accountMapper.accountCreationFineractRequest(
                findProduct.apply(request.getAccountType(), request.getCurrencyCode()),
                request.getCustomerId()
        );

        return AccountId.of(
                Objects.requireNonNull(context
                                .getResponseBody(
                                        context.savingsAccountApi().submitSavingsAccountsApplication(fineractRequest)
                                )
                                .getSavingsId())
                        .longValue()
        );
    }

    @Override
    public AccountId initPocketAccount(OpeningAccount request) {
        PostSavingsAccountsRequest fineractRequest = accountMapper.pocketAccountCreationFineractRequest(
                findProduct.apply(request.getAccountType(), request.getCurrencyCode()),
                request.getCustomerId()
        );

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
