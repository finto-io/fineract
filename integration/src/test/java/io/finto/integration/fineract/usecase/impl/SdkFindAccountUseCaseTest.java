package io.finto.integration.fineract.usecase.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.finto.exceptions.core.FintoApiException;
import io.finto.fineract.sdk.api.DataTablesApi;
import io.finto.fineract.sdk.api.SavingsAccountApi;
import io.finto.fineract.sdk.models.GetSavingsAccountsAccountIdResponse;
import io.finto.fineract.sdk.util.FineractClient;
import io.finto.integration.fineract.common.ResponseHandler;
import io.finto.integration.fineract.converter.FineractAccountMapper;
import io.finto.integration.fineract.domain.Account;
import io.finto.integration.fineract.domain.AccountAdditionalFields;
import io.finto.integration.fineract.domain.AccountId;
import io.finto.integration.fineract.domain.BankName;
import io.finto.integration.fineract.domain.BankSwift;
import io.finto.integration.fineract.domain.ProductId;
import org.easymock.IMocksControl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Call;

import java.util.List;
import java.util.function.Supplier;

import static io.finto.integration.fineract.test.Fixtures.testAccountAdditionalFields;
import static io.finto.integration.fineract.test.Fixtures.testSavedAccountResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;

class SdkFindAccountUseCaseTest {

    private IMocksControl control;
    private FineractClient fineractClient;
    private ResponseHandler responseHandler;
    private FineractAccountMapper fineractAccountMapper;
    private Supplier<BankName> bankName = () -> BankName.of("testBankName");
    private Supplier<BankSwift> bankSwift = () -> BankSwift.of("testBankSwift");
    private AccountId accountId = AccountId.of(10L);
    private ObjectMapper mapper = JsonMapper.builder().findAndAddModules().build();

    private SavingsAccountApi savingsAccountApi;
    private DataTablesApi dataTablesApi;

    private SdkFindAccountUseCase useCase;

    @BeforeEach
    void setUp() {
        control = createStrictControl();
        fineractClient = control.createMock(FineractClient.class);
        responseHandler = control.createMock(ResponseHandler.class);
        fineractAccountMapper = control.createMock(FineractAccountMapper.class);
        useCase = SdkFindAccountUseCase.builder()
                .fineractClient(fineractClient)
                .objectMapper(mapper)
                .accountMapper(fineractAccountMapper)
                .responseHandler(responseHandler)
                .bankNameInfo(bankName)
                .bankSwiftInfo(bankSwift)
                .build();

        savingsAccountApi = control.createMock(SavingsAccountApi.class);
        dataTablesApi = control.createMock(DataTablesApi.class);
    }

    @Test
    void test_defaultInstance_creation() {
        assertThat(SdkFindAccountUseCase.defaultInstance(fineractClient, bankSwift, bankName)).isNotNull();
    }

    /**
     * Method under test: {@link SdkFindAccountUseCase#findAccount(AccountId)}
     */
    @Test
    void test_findAccount_invalidAdditionalFieldsContent() {
        GetSavingsAccountsAccountIdResponse savedAccount = testSavedAccountResponse(accountId);
        Call<GetSavingsAccountsAccountIdResponse> savingCall = control.createMock(Call.class);
        Call<String> callDataTables = control.createMock(Call.class);
        String additionalDetailsContent = "";

        expect(fineractClient.getSavingsAccounts()).andReturn(savingsAccountApi);
        expect(savingsAccountApi.retrieveOneSavingsAccount(accountId.getValue(), null, null))
                .andReturn(savingCall);
        expect(responseHandler.getResponseBody(savingCall)).andReturn(savedAccount);
        expect(fineractClient.getDataTables()).andReturn(dataTablesApi);
        expect(dataTablesApi.getDatatableByAppTableId("account_fields", accountId.getValue(), null))
                .andReturn(callDataTables);
        expect(responseHandler.getResponseBody(callDataTables)).andReturn(additionalDetailsContent);

        control.replay();

        assertThatThrownBy(() -> useCase.findAccount(accountId))
                .isInstanceOf(FintoApiException.class)
                .hasCauseInstanceOf(JsonProcessingException.class);

        control.verify();
    }

    /**
     * Method under test: {@link SdkFindAccountUseCase#findAccount(AccountId)}
     */
    @Test
    void test_findAccount_emptyAdditionalFieldsContent() {
        GetSavingsAccountsAccountIdResponse savedAccount = testSavedAccountResponse(accountId);
        Call<GetSavingsAccountsAccountIdResponse> savingCall = control.createMock(Call.class);
        Call<String> callDataTables = control.createMock(Call.class);
        String additionalDetailsContent = "[]";
        Account result = Account.builder().id(accountId).productId(ProductId.builder().value(10L).build()).build();

        expect(fineractClient.getSavingsAccounts()).andReturn(savingsAccountApi);
        expect(savingsAccountApi.retrieveOneSavingsAccount(accountId.getValue(), null, null))
                .andReturn(savingCall);
        expect(responseHandler.getResponseBody(savingCall)).andReturn(savedAccount);
        expect(fineractClient.getDataTables()).andReturn(dataTablesApi);
        expect(dataTablesApi.getDatatableByAppTableId("account_fields", accountId.getValue(), null))
                .andReturn(callDataTables);
        expect(responseHandler.getResponseBody(callDataTables)).andReturn(additionalDetailsContent);
        expect(fineractAccountMapper.toAccount(savedAccount, null, bankSwift.get(), bankName.get()))
                .andReturn(result);

        control.replay();

        Account actual = useCase.findAccount(accountId);

        control.verify();

        assertThat(actual).isSameAs(result);
    }

    /**
     * Method under test: {@link SdkFindAccountUseCase#findAccount(AccountId)}
     */
    @Test
    void test_findAccount_withNoBlankAdditionalFieldsContent() throws JsonProcessingException {
        GetSavingsAccountsAccountIdResponse savedAccount = testSavedAccountResponse(accountId);
        AccountAdditionalFields additionalFields = testAccountAdditionalFields(accountId);
        Call<GetSavingsAccountsAccountIdResponse> savingCall = control.createMock(Call.class);
        Call<String> callDataTables = control.createMock(Call.class);
        String additionalDetailsContent = mapper.writeValueAsString(List.of(additionalFields));
        Account result = Account.builder().id(accountId).productId(ProductId.builder().value(10L).build()).build();

        expect(fineractClient.getSavingsAccounts()).andReturn(savingsAccountApi);
        expect(savingsAccountApi.retrieveOneSavingsAccount(accountId.getValue(), null, null))
                .andReturn(savingCall);
        expect(responseHandler.getResponseBody(savingCall)).andReturn(savedAccount);
        expect(fineractClient.getDataTables()).andReturn(dataTablesApi);
        expect(dataTablesApi.getDatatableByAppTableId("account_fields", accountId.getValue(), null))
                .andReturn(callDataTables);
        expect(responseHandler.getResponseBody(callDataTables)).andReturn(additionalDetailsContent);
        expect(fineractAccountMapper.toAccount(savedAccount, additionalFields, bankSwift.get(), bankName.get()))
                .andReturn(result);

        control.replay();

        Account actual = useCase.findAccount(accountId);

        control.verify();

        assertThat(actual).isSameAs(result);
    }

}