package io.finto.integration.fineract.converter;

import io.finto.domain.account.Account;
import io.finto.domain.account.AccountDetailsUpdate;
import io.finto.domain.account.AccountId;
import io.finto.domain.account.BankName;
import io.finto.domain.account.BankSwift;
import io.finto.domain.account.PocketAccount;
import io.finto.domain.customer.CustomerId;
import io.finto.domain.product.Product;
import io.finto.domain.product.ProductId;
import io.finto.fineract.sdk.models.GetSavingsAccountsAccountIdResponse;
import io.finto.fineract.sdk.models.PostSavingsAccountsRequest;
import io.finto.fineract.sdk.models.PostSavingsAccountsRequestDatatablesInner;
import io.finto.fineract.sdk.models.PostSavingsAccountsRequestDatatablesInnerData;
import io.finto.integration.fineract.dto.AccountAdditionalFieldsDto;
import io.finto.integration.fineract.dto.PocketAccountAdditionalFieldsDto;
import org.jetbrains.annotations.NotNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.util.List;

import static io.finto.fineract.sdk.Constants.DATE_FORMAT_PATTERN;
import static io.finto.fineract.sdk.Constants.DEFAULT_DATE_FORMATTER;
import static io.finto.fineract.sdk.Constants.LOCALE;
import static io.finto.fineract.sdk.CustomDatatableNames.ACCOUNT_ADDITIONAL_FIELDS;
import static io.finto.fineract.sdk.CustomDatatableNames.CARD_ADDITIONAL_FIELDS;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface FineractAccountMapper {

    FineractAccountMapper INSTANCE = Mappers.getMapper(FineractAccountMapper.class);

    @Mapping(target = "id", source = "response.id")
    @Mapping(target = "number", expression = "java(accountAdditionalFields != null && accountAdditionalFields.getExternalAccountNumber() != null ? accountAdditionalFields.getExternalAccountNumber() : response.getAccountNo())")
    @Mapping(target = "productId", source = "response.savingsProductId")
    @Mapping(target = "swift", source = "bankSwift.value")
    @Mapping(target = "bankName", source = "bankName.value")
    @Mapping(target = "customer.fullName", source = "response.clientName")
    @Mapping(target = "currencyCode.value", source = "response.currency.code")
    @Mapping(target = "noDebit", source = "response.subStatus.blockDebit")
    @Mapping(target = "noCredit", source = "response.subStatus.blockCredit")
    @Mapping(target = "dormant", source = "response.subStatus.dormant")
    @Mapping(target = "creditCurrentBalance", source = "response.summary.accountBalance")
    @Mapping(target = "creditBlockedAmount", expression = "java(response.getSummary().getAccountBalance().subtract(response.getSummary().getAvailableBalance()))")
    @Mapping(target = "creditAvailableBalance", source = "response.summary.availableBalance")
    @Mapping(target = "iban", source = "accountAdditionalFields.iban")
    @Mapping(target = "externalAccountNumber", source = "accountAdditionalFields.externalAccountNumber")
    @Mapping(target = "externalAccountName", source = "accountAdditionalFields.externalAccountName")
    @Mapping(target = "branch", source = "accountAdditionalFields.externalBranch")
    @Mapping(target = "name", source = "accountAdditionalFields.externalAccountName")
    Account toAccount(GetSavingsAccountsAccountIdResponse response, AccountAdditionalFieldsDto accountAdditionalFields, BankSwift bankSwift, BankName bankName);

    @Mapping(target = "id", source = "response.id")
    @Mapping(target = "number", source = "response.accountNo")
    @Mapping(target = "productId", source = "response.savingsProductId")
    @Mapping(target = "swift", source = "bankSwift.value")
    @Mapping(target = "bankName", source = "bankName.value")
    @Mapping(target = "customer.fullName", source = "response.clientName")
    @Mapping(target = "currencyCode.value", source = "response.currency.code")
    @Mapping(target = "noDebit", source = "response.subStatus.blockDebit")
    @Mapping(target = "noCredit", source = "response.subStatus.blockCredit")
    @Mapping(target = "dormant", source = "response.subStatus.dormant")
    @Mapping(target = "creditCurrentBalance", source = "response.summary.accountBalance")
    @Mapping(target = "creditBlockedAmount", expression = "java(response.getSummary().getAccountBalance().subtract(response.getSummary().getAvailableBalance()))")
    @Mapping(target = "creditAvailableBalance", source = "response.summary.availableBalance")
    @Mapping(target = "cardId", source = "accountAdditionalFields.cardId")
    @Mapping(target = "cardBin", source = "accountAdditionalFields.cardBin")
    @Mapping(target = "cardPan", source = "accountAdditionalFields.cardPan")
    @Mapping(target = "cardHolderName", source = "accountAdditionalFields.cardHolderName")
    PocketAccount toPocketAccount(GetSavingsAccountsAccountIdResponse response, PocketAccountAdditionalFieldsDto accountAdditionalFields, BankSwift bankSwift, BankName bankName);

    default AccountId toAccountId(Long id) {
        return AccountId.of(id);
    }

    default ProductId toProductId(Long id) {
        return ProductId.of(id);
    }

    default PostSavingsAccountsRequest accountCreationFineractRequest(Product product, CustomerId customerId) {
        return getPostSavingsAccountsRequest(product, customerId, ACCOUNT_ADDITIONAL_FIELDS);
    }

    default PostSavingsAccountsRequest pocketAccountCreationFineractRequest(Product product, CustomerId customerId) {
        return getPostSavingsAccountsRequest(product, customerId, CARD_ADDITIONAL_FIELDS);
    }

    @NotNull
    private PostSavingsAccountsRequest getPostSavingsAccountsRequest(Product product, CustomerId customerId, String customerDatatablesName) {
        PostSavingsAccountsRequestDatatablesInnerData data = PostSavingsAccountsRequestDatatablesInnerData.builder().build();
        data.setLocale(LOCALE);

        PostSavingsAccountsRequestDatatablesInner additionalFields = PostSavingsAccountsRequestDatatablesInner.builder().build();
        additionalFields.setRegisteredTableName(customerDatatablesName);
        additionalFields.setData(data);

        PostSavingsAccountsRequest fineractRequest = new PostSavingsAccountsRequest();
        fineractRequest.setProductId(product.getId().getValue().intValue());
        fineractRequest.setClientId(customerId.getValue().intValue());
        fineractRequest.setLocale(LOCALE);
        fineractRequest.setDateFormat(DATE_FORMAT_PATTERN);
        fineractRequest.submittedOnDate(LocalDate.now().format(DEFAULT_DATE_FORMATTER));
        fineractRequest.setDatatables(List.of(additionalFields));

        return fineractRequest;
    }

    AccountAdditionalFieldsDto toAccountAdditionalFieldsDto(AccountDetailsUpdate account);

}


