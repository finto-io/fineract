package io.finto.integration.fineract.usecase.impl.account;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.finto.domain.account.Account;
import io.finto.domain.account.AccountDetailsUpdate;
import io.finto.domain.account.AccountId;
import io.finto.fineract.sdk.api.DataTablesApi;
import io.finto.fineract.sdk.models.PutDataTablesAppTableIdResponse;
import io.finto.integration.fineract.converter.FineractAccountMapper;
import io.finto.integration.fineract.dto.AccountAdditionalFieldsDto;
import io.finto.integration.fineract.usecase.impl.SdkFineractUseCaseContext;
import io.finto.integration.fineract.usecase.impl.account.SdkEnrichAccountInfoUseCase;
import org.easymock.IMocksControl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Call;

import java.util.function.Function;

import static io.finto.fineract.sdk.CustomDatatableNames.ACCOUNT_ADDITIONAL_FIELDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;

class SdkEnrichAccountInfoUseCaseTest {

    private IMocksControl control;
    private SdkFineractUseCaseContext context;
    private ObjectMapper objectMapper;
    private FineractAccountMapper accountMapper;
    private AccountId accountId = AccountId.of(10L);
    private Account account;
    private AccountDetailsUpdate request;
    private AccountAdditionalFieldsDto dto;
    private Function<AccountId, Account> accountFinder;

    private DataTablesApi dataTablesApi;

    private SdkEnrichAccountInfoUseCase useCase;

    @BeforeEach
    void setUp() {
        control = createStrictControl();
        account = control.createMock(Account.class);
        request = control.createMock(AccountDetailsUpdate.class);
        dto = control.createMock(AccountAdditionalFieldsDto.class);

        accountFinder = control.createMock(Function.class);
        context = control.createMock(SdkFineractUseCaseContext.class);
        objectMapper = control.createMock(ObjectMapper.class);
        accountMapper = control.createMock(FineractAccountMapper.class);
        useCase = SdkEnrichAccountInfoUseCase.builder()
                .context(context)
                .objectMapper(objectMapper)
                .findAccount(accountFinder)
                .accountMapper(accountMapper)
                .build();

        dataTablesApi = control.createMock(DataTablesApi.class);
    }

    /**
     * Method under test: {@link SdkEnrichAccountInfoUseCase#saveAdditionalFields(AccountId, AccountDetailsUpdate)}
     */
    @Test
    void test_saveAdditionalFields_success() throws JsonProcessingException {

        Call<PutDataTablesAppTableIdResponse> callDataTables = control.createMock(Call.class);

        expect(accountMapper.toAccountAdditionalFieldsDto(request)).andReturn(dto);
        expect(context.dataTablesApi()).andReturn(dataTablesApi);
        expect(objectMapper.writeValueAsString(dto)).andReturn("testBody");
        expect(dataTablesApi.updateDatatableEntryOnetoOne(ACCOUNT_ADDITIONAL_FIELDS, accountId.getValue(), "testBody"))
                .andReturn(callDataTables);
        expect(context.getResponseBody(callDataTables)).andReturn(null);
        expect(accountFinder.apply(accountId)).andReturn(account);
        control.replay();

        var actual = useCase.saveAdditionalFields(accountId, request);
        control.verify();

        assertThat(actual).isSameAs(account);
    }

}