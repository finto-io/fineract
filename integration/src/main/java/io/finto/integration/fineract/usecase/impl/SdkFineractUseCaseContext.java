package io.finto.integration.fineract.usecase.impl;

import io.finto.fineract.sdk.api.*;
import io.finto.fineract.sdk.util.FineractClient;
import io.finto.integration.fineract.common.FineractResponseHandler;
import io.finto.integration.fineract.common.ResponseHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import retrofit2.Call;

@Builder
@AllArgsConstructor
public class SdkFineractUseCaseContext {

    @NonNull
    private final FineractClient fineractClient;
    @NonNull
    private final ResponseHandler responseHandler;

    public static class SdkFineractUseCaseContextBuilder {
        private ResponseHandler responseHandler = FineractResponseHandler.getDefaultInstance();
    }

    public FineractClient fineract() {
        return fineractClient;
    }

    public ResponseHandler responseHandler() {
        return responseHandler;
    }

    public <T> T getResponseBody(Call<T> call) {
        return responseHandler().getResponseBody(call);
    }

    public ChargesApi chargeApi() {
        return fineract().getCharges();
    }

    public LoanProductsApi loanProductApi() {
        return fineract().getLoanProducts();
    }

    public LoansApi loanApi() {
        return fineract().getLoans();
    }

    public PaymentTypeApi paymentTypeApi() {
        return fineract().getPaymentTypes();
    }

    public LoanTransactionsApi loanTransactionApi() {
        return fineract().getLoanTransactions();
    }

    public SavingsAccountApi savingsAccountApi() {
        return fineract().getSavingsAccounts();
    }

    public DataTablesApi dataTablesApi() {
        return fineract().getDataTables();
    }

    public ClientApi clientApi() {
        return fineract().getClients();
    }

    public ClientsAddressApi clientsAddressApi(){
        return fineractClient.getClientAddresses();
    }

    public CodeValuesApi codeValuesApi() {
        return fineract().getCodeValues();
    }

    public ClientIdentifierApi clientIdentifierApi() {
        return fineract().getClientIdentifiers();
    }
    
}
