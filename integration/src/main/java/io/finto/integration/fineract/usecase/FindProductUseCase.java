package io.finto.integration.fineract.usecase;

import io.finto.integration.fineract.domain.AccountType;
import io.finto.integration.fineract.domain.CurrencyCode;
import io.finto.integration.fineract.domain.Product;

public interface FindProductUseCase {
    Product findProduct(AccountType accountType, CurrencyCode currencyCode);
}
