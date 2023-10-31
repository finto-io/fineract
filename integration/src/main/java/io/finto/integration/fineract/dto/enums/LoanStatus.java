package io.finto.integration.fineract.dto.enums;

public enum LoanStatus {
    APPROVED, REJECTED, WITHDRAWN, ACTIVATED;

    public String getCommand() {
        switch (this){
            case APPROVED:
                return "approve";
            case REJECTED:
                return "reject";
            case WITHDRAWN:
                return "withdraw";
            case ACTIVATED:
                return "disburse";
            default:
                throw new UnsupportedOperationException();
        }
    }

}
