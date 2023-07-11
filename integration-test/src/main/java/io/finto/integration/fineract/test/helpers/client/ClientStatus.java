package io.finto.integration.fineract.test.helpers.client;

public enum ClientStatus {
    CLOSED, REACTIVATED, ACTIVATED;

    public String getCommand(){
        switch (this){
            case CLOSED:
                return "close";
            case REACTIVATED:
                return "reactivate";
            case ACTIVATED:
                return "activate";
            default:
                throw new UnsupportedOperationException();
        }
    }

}
