package io.finto.integration.fineract.converter;

import io.finto.fineract.sdk.models.PostLoansLoanIdRequest;
import io.finto.fineract.sdk.models.PostLoansLoanIdTransactionsRequest;
import io.finto.integration.fineract.dto.enums.LoanStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static io.finto.fineract.sdk.Constants.DATE_FORMAT_PATTERN;
import static io.finto.fineract.sdk.Constants.DEFAULT_DATE_FORMATTER;
import static io.finto.fineract.sdk.Constants.LOCALE;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface FineractLoanMapper {

    FineractLoanMapper INSTANCE = Mappers.getMapper(FineractLoanMapper.class);

    LoanStatus toCommandDto(io.finto.domain.id.fineract.LoanStatus status);

    @Mapping(target = "dateFormat", constant = DATE_FORMAT_PATTERN)
    @Mapping(target = "locale", constant = LOCALE)
    PostLoansLoanIdRequest toRequest(io.finto.domain.id.fineract.LoanStatus loanStatus);

    default PostLoansLoanIdRequest toRequestWithDate(io.finto.domain.id.fineract.LoanStatus loanStatus) {
        PostLoansLoanIdRequest request = toRequest(loanStatus);
        String currentDate = LocalDateTime.now().format(DEFAULT_DATE_FORMATTER);
        switch (loanStatus) {
            case APPROVED:
                request.setApprovedOnDate(currentDate);
                break;
            case REJECTED:
                request.setRejectedOnDate(currentDate);
                break;
            case WITHDRAWN:
                request.setWithdrawnOnDate(currentDate);
                break;
            case ACTIVATED:
                request.setActualDisbursementDate(currentDate);
                break;
            default:
                throw new IllegalArgumentException("Invalid loan status");
        }
        return request;
    }

    @Mapping(target = "dateFormat", constant = DATE_FORMAT_PATTERN)
    @Mapping(target = "locale", constant = LOCALE)
    PostLoansLoanIdTransactionsRequest toCloseRequest(io.finto.domain.id.fineract.LoanStatus loanStatus);

    default PostLoansLoanIdTransactionsRequest toRequestWithDateForClose(io.finto.domain.id.fineract.LoanStatus loanStatus) {
        PostLoansLoanIdTransactionsRequest requestForClose = toCloseRequest(loanStatus);
        String currentDate = LocalDateTime.now().format(DEFAULT_DATE_FORMATTER);
        requestForClose.setTransactionDate(currentDate);
        return requestForClose;
    }
}