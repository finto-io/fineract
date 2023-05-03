package io.finto.integration.fineract.test.helpers.account;

public enum AccountStatus {
    CLOSED, APPROVED, ACTIVATED;

    public String getCommand(){
        switch (this){
            case CLOSED:
                return "close";
            case APPROVED:
                return "approve";
            case ACTIVATED:
                return "activate";
            default:
                throw new UnsupportedOperationException();
        }
    }

}
