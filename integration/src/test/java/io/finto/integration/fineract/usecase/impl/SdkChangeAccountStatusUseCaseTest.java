package io.finto.integration.fineract.usecase.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.finto.domain.account.AccountId;
import io.finto.fineract.sdk.api.SavingsAccountApi;
import io.finto.fineract.sdk.models.PostSavingsAccountsAccountIdRequest;
import io.finto.fineract.sdk.models.PostSavingsAccountsAccountIdResponse;
import org.easymock.IMocksControl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Call;

import java.time.LocalDate;

import static io.finto.fineract.sdk.Constants.DATE_FORMAT_PATTERN;
import static io.finto.fineract.sdk.Constants.DEFAULT_DATE_FORMATTER;
import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;

class SdkChangeAccountStatusUseCaseTest {

    private IMocksControl control;
    private SdkFineractUseCaseContext context;
    private AccountId accountId = AccountId.of(10L);
    private ObjectMapper mapper = JsonMapper.builder().findAndAddModules().build();

    private SavingsAccountApi savingsAccountApi;

    private SdkChangeAccountStatusUseCase useCase;

    @BeforeEach
    void setUp() {
        control = createStrictControl();
        context = control.createMock(SdkFineractUseCaseContext.class);
        useCase = SdkChangeAccountStatusUseCase.builder()
                .context(context)
                .build();

        savingsAccountApi = control.createMock(SavingsAccountApi.class);
    }


    /**
     * Method under test: {@link SdkChangeAccountStatusUseCase#approveAccount(AccountId)}
     */
    @Test
    void test_approveAccount_success() {
        Call<PostSavingsAccountsAccountIdResponse> executeCommandCall = control.createMock(Call.class);
        PostSavingsAccountsAccountIdRequest fineractRequest = new PostSavingsAccountsAccountIdRequest();
        fineractRequest.setLocale("en");
        fineractRequest.setDateFormat(DATE_FORMAT_PATTERN);
        // TODO: this wil fail in case the test will be executed at midnight. think about it
        fineractRequest.setApprovedOnDate(LocalDate.now().format(DEFAULT_DATE_FORMATTER));

        PostSavingsAccountsAccountIdResponse response = new PostSavingsAccountsAccountIdResponse();
        response.setResourceId(accountId.getValue().intValue());

        expect(context.savingsAccountApi()).andReturn(savingsAccountApi);
        expect(savingsAccountApi.handleSavingsAccountsCommands(accountId.getValue(), fineractRequest, "approve")).andReturn(executeCommandCall);
        expect(context.getResponseBody(executeCommandCall)).andReturn(response);

        control.replay();

        AccountId actual = useCase.approveAccount(accountId);

        control.verify();

        Assertions.assertEquals(accountId, actual);
    }
    /**
     * Method under test: {@link SdkChangeAccountStatusUseCase#activateAccount(AccountId)}
     */
    @Test
    void test_activateAccount_success() {
        Call<PostSavingsAccountsAccountIdResponse> executeCommandCall = control.createMock(Call.class);
        PostSavingsAccountsAccountIdRequest fineractRequest = new PostSavingsAccountsAccountIdRequest();
        fineractRequest.setLocale("en");
        fineractRequest.setDateFormat(DATE_FORMAT_PATTERN);
        fineractRequest.setActivatedOnDate(LocalDate.now().format(DEFAULT_DATE_FORMATTER));

        PostSavingsAccountsAccountIdResponse response = new PostSavingsAccountsAccountIdResponse();
        response.setResourceId(accountId.getValue().intValue());

        expect(context.savingsAccountApi()).andReturn(savingsAccountApi);
        expect(savingsAccountApi.handleSavingsAccountsCommands(accountId.getValue(), fineractRequest, "activate")).andReturn(executeCommandCall);
        expect(context.getResponseBody(executeCommandCall)).andReturn(response);

        control.replay();

        AccountId actual = useCase.activateAccount(accountId);

        control.verify();

        Assertions.assertEquals(accountId, actual);
    }
    /**
     * Method under test: {@link SdkChangeAccountStatusUseCase#closeAccount(AccountId)}
     */
    @Test
    void test_closeAccount_success() {
        Call<PostSavingsAccountsAccountIdResponse> executeCommandCall = control.createMock(Call.class);
        PostSavingsAccountsAccountIdRequest fineractRequest = new PostSavingsAccountsAccountIdRequest();
        fineractRequest.setLocale("en");
        fineractRequest.setDateFormat(DATE_FORMAT_PATTERN);
        fineractRequest.setClosedOnDate(LocalDate.now().format(DEFAULT_DATE_FORMATTER));

        PostSavingsAccountsAccountIdResponse response = new PostSavingsAccountsAccountIdResponse();
        response.setResourceId(accountId.getValue().intValue());

        expect(context.savingsAccountApi()).andReturn(savingsAccountApi);
        expect(savingsAccountApi.handleSavingsAccountsCommands(accountId.getValue(), fineractRequest, "close")).andReturn(executeCommandCall);
        expect(context.getResponseBody(executeCommandCall)).andReturn(response);

        control.replay();

        AccountId actual = useCase.closeAccount(accountId);

        control.verify();

        Assertions.assertEquals(accountId, actual);
    }

}