package io.finto.integration.fineract.usecase.impl;

import io.finto.fineract.sdk.api.SavingsAccountApi;
import io.finto.fineract.sdk.models.PostSavingsAccountsRequest;
import io.finto.fineract.sdk.models.PostSavingsAccountsResponse;
import io.finto.integration.fineract.converter.FineractAccountMapper;
import io.finto.integration.fineract.domain.AccountId;
import io.finto.integration.fineract.domain.AccountType;
import io.finto.integration.fineract.domain.CurrencyCode;
import io.finto.integration.fineract.domain.CustomerId;
import io.finto.integration.fineract.domain.OpeningAccount;
import io.finto.integration.fineract.domain.Product;
import io.finto.integration.fineract.domain.ProductId;
import org.easymock.IMocksControl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Call;

import java.util.function.BiFunction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;

class SdkCreateAccountUseCaseTest {

    private IMocksControl control;
    private SdkFineractUseCaseContext context;
    private SavingsAccountApi savingsAccountApi;
    private AccountType accountType = testAccountType();
    private CurrencyCode currencyCode = testCurrencyCode();
    private Product product = testProduct();
    private BiFunction<AccountType, CurrencyCode, Product> productResolver = (accountType, currencyCode) -> product;
    private FineractAccountMapper accountMapper;

    private SdkCreateAccountUseCase useCase;

    @BeforeEach
    void setUp() {
        control = createStrictControl();
        context = control.createMock(SdkFineractUseCaseContext.class);
        accountMapper = control.createMock(FineractAccountMapper.class);
        savingsAccountApi = control.createMock(SavingsAccountApi.class);
        useCase = SdkCreateAccountUseCase.builder()
                .context(context)
                .findProduct(productResolver)
                .accountMapper(accountMapper)
                .build();
    }

    private AccountType testAccountType() {
        return AccountType.of("3101");
    }

    private CurrencyCode testCurrencyCode() {
        return CurrencyCode.of("JOD");
    }

    private Product testProduct() {
        return Product.of(ProductId.of(123L));
    }

    /**
     * Method under test: {@link SdkCreateAccountUseCase#initAccount(OpeningAccount)}
     */
    @Test
    void test_findAccount_invalidAdditionalFieldsContent() {
        CustomerId customerId = CustomerId.of(10L);
        AccountId accountId = AccountId.of(23L);

        OpeningAccount request = OpeningAccount.builder()
                .customerId(customerId)
                .accountType(accountType)
                .currencyCode(currencyCode)
                .build();

        PostSavingsAccountsRequest fineractRequest = control.createMock(PostSavingsAccountsRequest.class);
        Call<PostSavingsAccountsResponse> response = control.createMock(Call.class);
        PostSavingsAccountsResponse responseBody = control.createMock(PostSavingsAccountsResponse.class);

        expect(accountMapper.accountCreationFineractRequest(product, request.getCustomerId())).andReturn(fineractRequest);
        expect(context.savingsAccountApi()).andReturn(savingsAccountApi);
        expect(savingsAccountApi.submitSavingsAccountsApplication(fineractRequest)).andReturn(response);
        expect(context.getResponseBody(response)).andReturn(responseBody);
        expect(responseBody.getSavingsId()).andReturn(accountId.getValue().intValue());

        control.replay();

        AccountId actual = useCase.initAccount(request);

        control.verify();

        assertThat(actual).isEqualTo(accountId);
    }

}