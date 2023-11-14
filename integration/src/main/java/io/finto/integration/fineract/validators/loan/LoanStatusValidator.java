package io.finto.integration.fineract.validators.loan;

import io.finto.domain.id.fineract.LoanStatus;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class LoanStatusValidator {
    public boolean validateStatusChange(String currentStatus, LoanStatus newStatus) {
        switch (newStatus) {
            case APPROVED:
            case WITHDRAWN:
            case REJECTED:
                return currentStatus.equals("Submitted and pending approval");
            case ACTIVATED:
                return currentStatus.equals("Approved");
            case CLOSED:
                return currentStatus.equals("Active");
            default:
                return false;
        }
    }
}

