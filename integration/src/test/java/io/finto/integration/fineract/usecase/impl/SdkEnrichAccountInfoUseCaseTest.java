package io.finto.integration.fineract.usecase.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.finto.fineract.sdk.api.DataTablesApi;
import io.finto.fineract.sdk.models.PutDataTablesAppTableIdResponse;
import io.finto.integration.fineract.domain.Account;
import io.finto.integration.fineract.domain.AccountAdditionalFields;
import io.finto.integration.fineract.domain.AccountId;
import io.finto.integration.fineract.domain.ProductId;
import org.easymock.IMocksControl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Call;

import java.util.function.Function;

import static io.finto.fineract.sdk.CustomDatatableNames.ACCOUNT_ADDITIONAL_FIELDS;
import static io.finto.integration.fineract.test.Fixtures.testAccountAdditionalFields;
import static org.assertj.core.api.Assertions.assertThat;
import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;

class SdkEnrichAccountInfoUseCaseTest {

    private IMocksControl control;
    private SdkFineractUseCaseContext context;
    private ObjectMapper objectMapper;
    private AccountId accountId = AccountId.of(10L);
    private Account account;
    private Function<AccountId, Account> accountFinder;

    private DataTablesApi dataTablesApi;

    private SdkEnrichAccountInfoUseCase useCase;

    @BeforeEach
    void setUp() {
        control = createStrictControl();
        account = Account.builder().id(accountId).productId(ProductId.of(1)).build();
        accountFinder = control.createMock(Function.class);
        context = control.createMock(SdkFineractUseCaseContext.class);
        objectMapper = control.createMock(ObjectMapper.class);
        useCase = SdkEnrichAccountInfoUseCase.builder()
                .context(context)
                .objectMapper(objectMapper)
                .findAccount(accountFinder)
                .build();

        dataTablesApi = control.createMock(DataTablesApi.class);
    }

    /**
     * Method under test: {@link SdkEnrichAccountInfoUseCase#saveAdditionalFields(AccountId, AccountAdditionalFields)}
     */
    @Test
    void test_saveAdditionalFields_success() throws JsonProcessingException {
        AccountAdditionalFields additionalFields = testAccountAdditionalFields(accountId);
        Call<PutDataTablesAppTableIdResponse> callDataTables = control.createMock(Call.class);

        expect(context.dataTablesApi()).andReturn(dataTablesApi);
        expect(objectMapper.writeValueAsString(additionalFields)).andReturn("testBody");
        expect(dataTablesApi.updateDatatableEntryOnetoOne(ACCOUNT_ADDITIONAL_FIELDS, accountId.getValue(), "testBody"))
                .andReturn(callDataTables);

        expect(context.getResponseBody(callDataTables)).andReturn(null);

        expect(accountFinder.apply(accountId)).andReturn(account);

        control.replay();

        var actual = useCase.saveAdditionalFields(accountId, additionalFields);

        control.verify();

        assertThat(actual).isSameAs(account);
    }

}