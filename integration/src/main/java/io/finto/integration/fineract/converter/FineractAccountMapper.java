package io.finto.integration.fineract.converter;

import io.finto.fineract.sdk.models.GetSavingsAccountsAccountIdResponse;
import io.finto.integration.fineract.domain.Account;
import io.finto.integration.fineract.domain.AccountAdditionalFields;
import io.finto.integration.fineract.domain.AccountId;
import io.finto.integration.fineract.domain.BankName;
import io.finto.integration.fineract.domain.BankSwift;
import io.finto.integration.fineract.domain.ProductId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface FineractAccountMapper {

    FineractAccountMapper INSTANCE = Mappers.getMapper(FineractAccountMapper.class);

    @Mapping(target = "id", source = "response.id")
    @Mapping(target = "number", source = "response.accountNo")
    @Mapping(target = "productId", source = "response.savingsProductId")
    @Mapping(target = "swift", source = "bankSwift.value")
    @Mapping(target = "bankName", source = "bankName.value")
    @Mapping(target = "customer.name", source = "response.clientName")
    @Mapping(target = "customer.fullName", source = "response.clientName")
    @Mapping(target = "currencyCode", source = "response.currency.code")
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
    Account toAccount(GetSavingsAccountsAccountIdResponse response, AccountAdditionalFields accountAdditionalFields, BankSwift bankSwift, BankName bankName);

    default AccountId toAccountId(Long id) {
        return AccountId.of(id);
    }

    default ProductId toProductId(Long id) {
        return ProductId.of(id);
    }

}


