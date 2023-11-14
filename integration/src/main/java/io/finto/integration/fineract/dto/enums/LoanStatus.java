package io.finto.integration.fineract.dto.enums;

public enum LoanStatus {
    APPROVED, REJECTED, WITHDRAWN, ACTIVATED, CLOSED;

    public String getCommand() {
        switch (this){
            case APPROVED:
                return "approve";
            case REJECTED:
                return "reject";
            case WITHDRAWN:
                return "withdrawnByApplicant";
            case ACTIVATED:
                return "disburse";
            case CLOSED:
                return "close";
            default:
                throw new UnsupportedOperationException();
        }
    }

}
