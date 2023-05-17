package io.finto.integration.fineract.common;

public class FineractResponseHandler {

    public static ResponseHandler getDefaultInstance() {
        return new FineractResponseHandlerMini(new ErrorResponseHandlerMini());
    }

}
